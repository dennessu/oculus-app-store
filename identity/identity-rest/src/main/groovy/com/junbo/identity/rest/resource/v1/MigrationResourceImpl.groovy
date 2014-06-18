package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.authorization.spec.model.RoleTarget
import com.junbo.authorization.spec.option.list.RoleAssignmentListOptions
import com.junbo.authorization.spec.option.list.RoleListOptions
import com.junbo.authorization.spec.resource.RoleAssignmentResource
import com.junbo.authorization.spec.resource.RoleResource
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.model.migration.OculusInput
import com.junbo.identity.spec.v1.model.migration.OculusOutput
import com.junbo.identity.spec.v1.resource.MigrationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 6/6/14.
 */
@Transactional
@CompileStatic
class MigrationResourceImpl implements MigrationResource {

    private static final String ADMIN_ROLE = 'admin'
    private static final String DEVELOPER_ROLE = 'developer'
    private static final String PUBLISHER_ROLE = 'publisher'

    private static final String ORGANIZATION_TARGET_TYPE = 'organizations'
    private static final String ORGANIZATION_FILTER_TYPE = 'SINGLEINSTANCEFILTER'

    @Autowired
    private OrganizationRepository organizationRepository

    @Autowired
    private GroupRepository groupRepository

    @Autowired
    private UserGroupRepository userGroupRepository

    @Autowired
    @Qualifier('identityRoleResource')
    private RoleResource roleResource

    @Autowired
    @Qualifier('identityRoleAssignmentResource')
    private RoleAssignmentResource roleAssignmentResource

    @Autowired
    private UserPersonalInfoRepository userPersonalInfoRepository

    @Autowired
    private UserPasswordRepository userPasswordRepository

    @Autowired
    private UserRepository userRepository

    @Autowired
    private NormalizeService normalizeService

    @Override
    Promise<OculusOutput> migrate(OculusInput oculusInput) {
        if (StringUtils.isEmpty(oculusInput.username)) {
            throw new IllegalArgumentException('username can\'t be null')
        }
        if (StringUtils.isEmpty(oculusInput.email)) {
            throw new IllegalArgumentException('email can\'t be null')
        }

        // Check current email isn't used by others
        return checkPasswordValid(oculusInput).then {
            return checkUserValid(oculusInput)
        }.then {
            return checkEmailValid(oculusInput).then { User existing ->
                return checkOrganizationValid(oculusInput, existing).then {
                    // If the user exists, update current user's information;
                    // else, create user's information
                    return createOrUpdateMigrateUser(oculusInput)
                }
            }
        }
    }

    Promise<Void> createOrUpdateMigrateUser(OculusInput oculusInput) {
        User user = new User(
                username: oculusInput.username,
                canonicalUsername: normalizeService.normalize(oculusInput.username),
                preferredLocale: new LocaleId(oculusInput.language),
                preferredTimezone: timeZoneMap.get(oculusInput.timezone),
                status: getMappedUserStatus(oculusInput),
                isAnonymous: StringUtils.isEmpty(oculusInput.username),
                profile: getMappedUserProfile(oculusInput),
                createdTime: oculusInput.createdDate,
                updatedTime: oculusInput.updateDate
        )

        return saveOrUpdateUser(user).then { User createdUser ->
            // Create user name
            UserName userName = new UserName(
                givenName: oculusInput.firstName,
                familyName: oculusInput.lastName,
                nickName: oculusInput.nickname
            )
            UserPersonalInfo name = new UserPersonalInfo(
                    userId: (UserId)createdUser.id,
                    type: UserPersonalInfoType.NAME.toString(),
                    value: ObjectMapperProvider.instance().valueToTree(userName),
                    createdTime: oculusInput.createdDate,
                    updatedTime: oculusInput.updateDate
            )

            return userPersonalInfoRepository.create(name).then { UserPersonalInfo userPersonalInfo ->
                createdUser.name = new UserPersonalInfoLink(
                        isDefault: true,
                        value: (UserPersonalInfoId)userPersonalInfo.id
                )
                return Promise.pure(createdUser)
            }
        }.then { User createdUser ->
            Email userEmail = new Email(
                    info: oculusInput.email
            )
            UserPersonalInfo email = new UserPersonalInfo(
                    userId: (UserId)createdUser.id,
                    type: UserPersonalInfoType.EMAIL.toString(),
                    value: ObjectMapperProvider.instance().valueToTree(userEmail),
                    lastValidateTime: getMappedEmailValidateTime(oculusInput),
                    createdTime: oculusInput.createdDate,
                    updatedTime: oculusInput.updateDate
            )

            return userPersonalInfoRepository.create(email).then { UserPersonalInfo userPersonalInfo ->
                createdUser.emails = new ArrayList<>()
                UserPersonalInfoLink emailLink = new UserPersonalInfoLink(
                        isDefault: true,
                        value: (UserPersonalInfoId)userPersonalInfo.id
                )
                createdUser.emails.add(emailLink)
                return Promise.pure(createdUser)
            }
        }.then { User createdUser ->
            if (StringUtils.isEmpty(oculusInput.gender)) {
                return Promise.pure(createdUser)
            }

            UserGender userGender = new UserGender(
                    info: oculusInput.gender
            )
            UserPersonalInfo gender = new UserPersonalInfo(
                    userId: (UserId)createdUser.id,
                    type: UserPersonalInfoType.GENDER.toString(),
                    value: ObjectMapperProvider.instance().valueToTree(userGender),
                    createdTime: oculusInput.createdDate,
                    updatedTime: oculusInput.updateDate
            )

            return userPersonalInfoRepository.create(gender).then { UserPersonalInfo userPersonalInfo ->
                createdUser.gender = new UserPersonalInfoLink(
                        isDefault: true,
                        value: (UserPersonalInfoId)userPersonalInfo.id
                )
                return Promise.pure(createdUser)
            }
        }.then { User createdUser ->
            if (oculusInput.dob == null) {
                return Promise.pure(createdUser)
            }

            UserDOB userDOB = new UserDOB(
                    info: oculusInput.dob
            )
            UserPersonalInfo dob = new UserPersonalInfo(
                    userId: (UserId)createdUser.id,
                    type: UserPersonalInfoType.DOB.toString(),
                    value: ObjectMapperProvider.instance().valueToTree(userDOB),
                    createdTime: oculusInput.createdDate,
                    updatedTime: oculusInput.updateDate
            )

            return userPersonalInfoRepository.create(dob).then { UserPersonalInfo userPersonalInfo ->
                createdUser.dob = new UserPersonalInfoLink(
                        isDefault: true,
                        value: (UserPersonalInfoId)userPersonalInfo.id
                )
                return Promise.pure(createdUser)
            }
        }.then { User createdUser ->
            return userRepository.update(createdUser)
        }.then { User createdUser ->
            return saveOrUpdatePassword(oculusInput, createdUser).then {
                return Promise.pure(createdUser)
            }
        }.then { User createdUser ->

            return migrateOrganization(oculusInput, createdUser)

        }
    }

    Promise<UserPassword> saveOrUpdatePassword(OculusInput oculusInput, User user) {
        return userPasswordRepository.searchByUserId(user.getId(), Integer.MAX_VALUE, 0).then { List<UserPassword> userPasswordList ->
            if (CollectionUtils.isEmpty(userPasswordList)) {
                UserPassword userPassword = new UserPassword(
                        changeAtNextLogin: oculusInput.forceResetPassword,
                        userId: user.getId(),
                        active: true,
                        passwordHash: oculusInput.password
                )
                // create password
                return userPasswordRepository.create(userPassword)
            } else {
                UserPassword activePassword = userPasswordList.find { UserPassword userPassword ->
                    return userPassword.active
                }

                activePassword.changeAtNextLogin = oculusInput.forceResetPassword
                activePassword.passwordHash = oculusInput.password

                return userPasswordRepository.update(activePassword)
            }
        }
    }

    Promise<User> saveOrUpdateUser(User user) {
        return userRepository.getUserByCanonicalUsername(normalizeService.normalize(user.username)).then { User existing ->
            if (existing == null) {
                return userRepository.create(user)
            } else {
                user.id = (UserId)existing.id
                user.rev = existing.rev
                return userRepository.update(user)
            }
        }
    }

    Promise<User> checkEmailValid(OculusInput oculusInput) {
        return userRepository.getUserByCanonicalUsername(normalizeService.normalize(oculusInput.username)).then { User existing ->
            return userPersonalInfoRepository.searchByEmail(oculusInput.email.toLowerCase(java.util.Locale.ENGLISH),
                    Integer.MAX_VALUE, 0).then { List<UserPersonalInfo> emails ->
                if (CollectionUtils.isEmpty(emails)) {
                    return Promise.pure(null)
                }

                emails.removeAll { UserPersonalInfo userPersonalInfo ->
                    existing != null && userPersonalInfo.userId == existing.id
                }

                if (CollectionUtils.isEmpty(emails)) {
                    return Promise.pure(existing)
                }

                throw AppErrors.INSTANCE.userEmailAlreadyUsed(oculusInput.email).exception()
            }
        }
    }

    Promise<Void> checkOrganizationValid(OculusInput oculusInput, User user) {
        if (oculusInput.company == null) {
            throw new IllegalArgumentException('company is null')
        }

        if (oculusInput.company.name == null) {
            throw new IllegalArgumentException('company.name is null')
        }

        checkCompanyType(oculusInput)

        return Promise.pure(null)
    }

    Promise<Void> checkUserValid(OculusInput oculusInput) {
        if (StringUtils.isEmpty(oculusInput.status)) {
            throw new IllegalArgumentException('user Status error')
        }
        List<String> allowedValues = MigrateUserStatus.values().collect { MigrateUserStatus userStatus ->
            userStatus.toString()
        }

        if (!(oculusInput.status in allowedValues)) {
            throw AppErrors.INSTANCE.fieldInvalid('status', allowedValues.join(',')).exception()
        }

        if (oculusInput.language != null) {
            if (!ValidatorUtil.isValidLocale(oculusInput.language)) {
                throw AppErrors.INSTANCE.fieldInvalidException('language', "language format should be en_US and etc").exception()
            }
        }

        return Promise.pure(null)
    }

    void checkCompanyType(OculusInput oculusInput) {
        if (oculusInput.company == null) {
            throw new IllegalArgumentException('company is missing')
        }

        if (oculusInput.company.type == null) {
            throw new IllegalArgumentException('company.type is missing')
        }

        List<String> allowedValues = MigrateCompanyType.values().collect { MigrateCompanyType migrateCompanyType ->
            migrateCompanyType.toString()
        }
        if (!(oculusInput.company.type in allowedValues)) {
            throw AppErrors.INSTANCE.fieldInvalid('company.type', allowedValues.join(',')).exception()
        }
    }

    Promise<Void> checkPasswordValid(OculusInput oculusInput) {
        if (StringUtils.isEmpty(oculusInput.password)) {
            throw new IllegalArgumentException('password is null or empty')
        }
        String[] passwords = oculusInput.password.split(":")
        if (passwords.length != 4 && passwords[0] != "1") {
            throw new IllegalArgumentException('password only accept version 1')
        }

        return Promise.pure(null)
    }

    private Date getMappedEmailValidateTime(OculusInput oculusInput) {
        if (oculusInput.status == MigrateUserStatus.ACTIVE.toString()
         || oculusInput.status == MigrateUserStatus.VERIFIED.toString()) {
            return oculusInput.updateDate == null ? oculusInput.createdDate : oculusInput.updateDate
        } else {
            return null
        }
    }

    private Boolean getOrganizationStatus(OculusInput oculusInput) {
        if (oculusInput.status == MigrateUserStatus.ACTIVE.toString()) {
            return true
        }
        return false
    }

    private String getMappedUserStatus(OculusInput oculusInput) {
        if (oculusInput.status == MigrateUserStatus.ARCHIVE.toString()) {
            return UserStatus.DELETED.toString()
        } else {
            return UserStatus.ACTIVE.toString()
        }
    }

    private UserProfile getMappedUserProfile(OculusInput oculusInput) {
        if (oculusInput.profile == null) {
            return null
        }

        UserProfile userProfile = new UserProfile(
            headline: oculusInput.profile.headline,
            summary: oculusInput.profile.summary,
            url: oculusInput.profile.url,
            avatar: oculusInput.profile.avatar
        )

        return userProfile
    }

    private Promise<OculusOutput> migrateOrganization(OculusInput oculusInput, User createdUser) {
        return saveOrUpdateOrganization(oculusInput, createdUser).then { Organization org ->
            OculusOutput oculusOutput = new OculusOutput(
                    userId: createdUser.getId(),
                    organizationId: org.getId()
            )

            return Promise.pure(oculusOutput)
        }
    }

    // if organization.name exists, then
    //          i.  if the user is the owner of the organization:
    //                      a.  update the organization;
    //                      b.  check and update role;
    //                      c.  check and update group;
    //                      d.  check and update roleAssignment
    //          ii. if the user isn't the owner of the organization:
    //                      a.  if isAdmin = true,
    //                              a): If the user doesn't exist in "Admin" group, add the user to group having "Organization Admin",
    //                              b): If the user exist in "Admin" group, do nothing
    //                      b. if isAdmin = false,
    //                              a): If the user doesn't exist in "Developer" group, add the user to "Developer" group; if it doesn't exists, create "Developer" group.
    //                              b): If the user exists in "Developer" group, do nothing
    // if organization.name doesn't exists, then
    //          i.  if isAdmin = true, create organization, create group with "Organization Admin" role;
    //          ii. if isAdmin = false, throw exception
    Promise<Organization> saveOrUpdateOrganization(OculusInput oculusInput, User createdUser) {
        return organizationRepository.searchByCanonicalName(normalizeService.normalize(oculusInput.company.name),
                Integer.MAX_VALUE, 0).then { List<Organization> organizationList ->
            if (CollectionUtils.isEmpty(organizationList)) {
                // todo:    Need to communicate with Tianxiang, isAdmin=true should be posted at first.
                // create organization, create role, create group
                return createNewOrg(oculusInput, createdUser)
            } else {
                Organization existingOrg = organizationList.find { Organization organization ->
                    organization.ownerId == createdUser.id
                }

                if (existingOrg == null) {
                    // user isn't the owner of the organization
                    return addToUserGroup(oculusInput, createdUser, organizationList.get(0))
                } else {
                    // user is the owner of the organization
                    return updateNewOrg(oculusInput, createdUser, existingOrg)
                }
            }
        }
    }

    private Promise<Organization> updateNewOrg(OculusInput oculusInput, User createdUser, Organization existingOrg) {
        return saveOrUpdateOrg(oculusInput, createdUser, existingOrg).then { Organization org ->
            return addToUserGroup(oculusInput, createdUser, org)
        }
    }

    private Promise<Organization> createNewOrg(OculusInput oculusInput, User createdUser) {
        // create organization, create role, create group
        Role createdRole = null
        Group createdGroup = null
        return saveOrUpdateOrg(oculusInput, null, null).then { Organization organization ->
            return saveOrUpdateRole(organization, ADMIN_ROLE).then { Role role ->
                createdRole = oculusInput.company.isAdmin ? role : createdRole
                return saveOrUpdateRole(organization, DEVELOPER_ROLE).then { Role developerRole ->
                    createdRole = !oculusInput.company.isAdmin ? developerRole : createdRole
                    return Promise.pure(organization)
                }
            }
        }.then { Organization organization ->
            return saveOrUpdateGroup(organization, ADMIN_ROLE).then { Group group ->
                createdGroup = oculusInput.company.isAdmin ? group : createdGroup
                return saveOrUpdateGroup(organization, DEVELOPER_ROLE).then { Group developerGroup ->
                    createdGroup = !oculusInput.company.isAdmin ? developerGroup : createdGroup
                    return Promise.pure(organization)
                }
            }
        }.then { Organization organization ->
            return saveOrUpdateUserGroup(createdUser, createdGroup).then {
                return Promise.pure(organization)
            }
        }.then { Organization organization ->
            return saveOrUpdateRoleAssignment(createdRole, createdGroup).then {
                return Promise.pure(organization)
            }
        }
    }

    private Promise<Organization> addToUserGroup(OculusInput oculusInput, User createdUser, Organization existingOrganization) {
        if (oculusInput.company.isAdmin) {
            // Add to ADMIN group
            return groupRepository.searchByOrganizationIdAndName(existingOrganization.getId(), ADMIN_ROLE, Integer.MAX_VALUE, null).then { Group group ->
                return saveOrUpdateUserGroup(createdUser, group).then {
                    return Promise.pure(existingOrganization)
                }
            }
        } else {
            // Add to DEVELOPER group
            return groupRepository.searchByOrganizationIdAndName(existingOrganization.getId(), DEVELOPER_ROLE, Integer.MAX_VALUE, null).then { Group group ->
                return saveOrUpdateUserGroup(createdUser, group).then {
                    return Promise.pure(existingOrganization)
                }
            }
        }
    }

    private Promise<Organization> saveOrUpdateOrg(OculusInput oculusInput, User createdUser, Organization existingOrg) {
        if (existingOrg == null) {
            Organization org = new Organization()
            org.name = oculusInput.company.name
            org.canonicalName = normalizeService.normalize(oculusInput.company.name)
            org.ownerId = createdUser != null ? createdUser.id : null
            org.isValidated = getOrganizationStatus(oculusInput)
            org.type = oculusInput.company.type
            org.createdTime = oculusInput.createdDate
            org.updatedTime = oculusInput.updateDate

            return organizationRepository.create(org).then { Organization createdOrg ->
                return getOrgShippingAddress(oculusInput, createdOrg).then { UserPersonalInfo orgShippingAddress ->
                    org.shippingAddress = orgShippingAddress.getId()
                    return Promise.pure(createdOrg)
                }
            }.then { Organization createdOrg ->
                return getOrgPhone(oculusInput, createdOrg).then { UserPersonalInfo orgPhone ->
                    org.shippingPhone = orgPhone.getId()
                    return Promise.pure(createdOrg)
                }
            }.then { Organization createdOrg ->
                return organizationRepository.update(createdOrg)
            }
        } else {
            existingOrg.name = oculusInput.company.name
            existingOrg.isValidated = getOrganizationStatus(oculusInput)
            existingOrg.type = oculusInput.company.type
            existingOrg.createdTime = oculusInput.createdDate
            existingOrg.updatedTime = oculusInput.updateDate

            return getOrgShippingAddress(oculusInput, existingOrg).then { UserPersonalInfo orgShippingAddress ->
                existingOrg.shippingAddress = orgShippingAddress.getId()
                return Promise.pure(existingOrg)
            }.then { Organization updatedOrg ->
                return getOrgPhone(oculusInput, updatedOrg).then { UserPersonalInfo orgPhone ->
                    existingOrg.shippingPhone = orgPhone.getId()
                    return Promise.pure(existingOrg)
                }
            }.then { Organization updatedOrg ->
                return organizationRepository.update(existingOrg)
            }
        }
    }

    private Promise<Role> saveOrUpdateRole(Organization organization, String roleType) {
        return roleResource.list(new RoleListOptions(
            name: roleType,
            targetType: ORGANIZATION_TARGET_TYPE,
            filterType: ORGANIZATION_FILTER_TYPE,
            filterLink: IdUtil.toHref(organization.getId())
        )).then { Results<Role> roleResults ->
            if (CollectionUtils.isEmpty(roleResults.items)) {
                // need to create the new Organization
                Role role = new Role(
                        name: roleType,
                        target: new RoleTarget(
                            targetType: ORGANIZATION_TARGET_TYPE,
                            filterType: ORGANIZATION_FILTER_TYPE,
                            filterLink: new Link(
                                    href: IdUtil.toHref(organization.getId())
                            )
                        )
                )

                return roleResource.create(role)
            } else {
                // need to update the existing organization
                // do nothing here
                return Promise.pure(roleResults.items.get(0))
            }
        }
    }

    private Promise<Group> saveOrUpdateGroup(Organization org, String roleName) {
        return groupRepository.searchByOrganizationId(org.getId(), Integer.MAX_VALUE, 0).then { List<Group> groupList ->
            if (CollectionUtils.isEmpty(groupList)) {
                Group newGroup = new Group(
                        name: roleName,
                        active: true,
                        organizationId: org.getId()
                )

                return groupRepository.create(newGroup)
            } else {
                return groupRepository.searchByOrganizationIdAndName(org.getId(), roleName, Integer.MAX_VALUE, 0).then { Group existingGroup ->
                    if (existingGroup == null) {
                        Group newGroup = new Group(
                            name: roleName,
                            active: true,
                            organizationId: org.getId()
                        )

                        return groupRepository.create(newGroup)
                    }

                    return Promise.pure(existingGroup)
                }
            }
        }
    }

    private Promise<UserGroup> saveOrUpdateUserGroup(User user, Group group) {
        return userGroupRepository.searchByUserIdAndGroupId(user.getId(), group.getId(), Integer.MAX_VALUE, 0).then { List<UserGroup> userGroupList ->
            if (CollectionUtils.isEmpty(userGroupList)) {
                UserGroup userGroup = new UserGroup(
                        userId: user.getId(),
                        groupId: group.getId()
                )

                return userGroupRepository.create(userGroup)
            }

            return Promise.pure(userGroupList.get(0))
        }
    }

    private Promise<RoleAssignment> saveOrUpdateRoleAssignment(Role role, Group group) {
        return roleAssignmentResource.list(new RoleAssignmentListOptions(
                roleId: role.getId(),
                assignee: IdUtil.toHref(group.getId())
        )).then { Results<RoleAssignment> roleAssignmentResults ->
            if (CollectionUtils.isEmpty(roleAssignmentResults.items)) {
                RoleAssignment roleAssignment = new RoleAssignment(
                        roleId: role.getId(),
                        assignee: new Link(
                                href:  IdUtil.toHref(group.getId())
                        )
                )

                return roleAssignmentResource.create(roleAssignment)
            }

            return Promise.pure(roleAssignmentResults.items.get(0))
        }
    }

    private Promise<UserPersonalInfo> getOrgShippingAddress(OculusInput oculusInput, Organization createdOrg) {
        Address address = new Address(
            street1: oculusInput.company.address,
            city: oculusInput.company.city,
            subCountry: oculusInput.company.state,
            countryId: new CountryId(oculusInput.company.country),
            postalCode: oculusInput.company.postalCode
        )

        UserPersonalInfo orgShippingAddress = new UserPersonalInfo()
        orgShippingAddress.userId = null
        orgShippingAddress.organizationId = createdOrg.getId()
        orgShippingAddress.type = UserPersonalInfoType.ADDRESS.toString()
        orgShippingAddress.value = ObjectMapperProvider.instance().valueToTree(address)
        orgShippingAddress.createdTime = oculusInput.createdDate
        orgShippingAddress.updatedTime = oculusInput.updateDate

        return userPersonalInfoRepository.create(orgShippingAddress)
    }

    private Promise<UserPersonalInfo> getOrgPhone(OculusInput oculusInput, Organization createdOrg) {
        PhoneNumber phoneNumber = new PhoneNumber(
                info: oculusInput.company.phone
        )

        UserPersonalInfo orgPhone = new UserPersonalInfo()
        orgPhone.userId = null
        orgPhone.organizationId = createdOrg.getId()
        orgPhone.type = UserPersonalInfoType.PHONE.toString()
        orgPhone.value = ObjectMapperProvider.instance().valueToTree(phoneNumber)
        orgPhone.createdTime = oculusInput.createdDate
        orgPhone.updatedTime = oculusInput.updateDate

        return userPersonalInfoRepository.create(orgPhone)
    }

    private static Map<Number, String> timeZoneMap

    static {
        timeZoneMap = new HashMap<>()
        timeZoneMap.put(-12, "UTC-12:00")
        timeZoneMap.put(-11, "UTC-11:00")
        timeZoneMap.put(-10, "UTC-10:00")
        timeZoneMap.put(-9, "UTC-09:00")
        timeZoneMap.put(-8, "UTC-08:00")
        timeZoneMap.put(-7, "UTC-07:00")
        timeZoneMap.put(-6, "UTC-06:00")
        timeZoneMap.put(-5, "UTC-05:00")
        timeZoneMap.put(-4.5, "UTC-04:30")
        timeZoneMap.put(-4, "UTC-04:00")
        timeZoneMap.put(-3.5, "UTC-03:30")
        timeZoneMap.put(-3, "UTC-03:00")
        timeZoneMap.put(-2, "UTC-02:00")
        timeZoneMap.put(-1, "UTC-01:00")
        timeZoneMap.put(0, "UTC")
        timeZoneMap.put(1, "UTC+01:00")
        timeZoneMap.put(2, "UTC+02:00")
        timeZoneMap.put(3, "UTC+03:00")
        timeZoneMap.put(4, "UTC+04:00")
        timeZoneMap.put(4.5, "UTC+04:30")
        timeZoneMap.put(5, "UTC+05:00")
        timeZoneMap.put(5.5, "UTC+05:30")
        timeZoneMap.put(5.75, "UTC+05:45")
        timeZoneMap.put(6, "UTC+06:00")
        timeZoneMap.put(6.5, "UTC+06:30")
        timeZoneMap.put(7, "UTC+07:00")
        timeZoneMap.put(8, "UTC+08:00")
        timeZoneMap.put(9, "UTC+09:00")
        timeZoneMap.put(9.5, "UTC+09:30")
        timeZoneMap.put(10, "UTC+10:00")
        timeZoneMap.put(11, "UTC+11:00")
        timeZoneMap.put(12, "UTC+12:00")
        timeZoneMap.put(13, "UTC+13:00")
    }

    enum MigrateUserStatus {
        ACTIVE,
        ARCHIVE,
        PENDING,
        PENDING_EMAIL_VERIFICATION,
        VERIFIED
    }

    enum MigrateCompanyType {
        CORPORATE,
        INDIVIDUAL
    }
}
