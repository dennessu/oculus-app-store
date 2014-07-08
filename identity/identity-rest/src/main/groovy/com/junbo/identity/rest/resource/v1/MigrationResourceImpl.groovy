package com.junbo.identity.rest.resource.v1
import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.authorization.spec.model.RoleTarget
import com.junbo.authorization.spec.option.list.RoleAssignmentListOptions
import com.junbo.authorization.spec.option.list.RoleListOptions
import com.junbo.authorization.spec.resource.RoleAssignmentResource
import com.junbo.authorization.spec.resource.RoleResource
import com.junbo.common.cloudant.CloudantClientBase
import com.junbo.common.cloudant.CloudantMarshaller
import com.junbo.common.cloudant.DefaultCloudantMarshaller
import com.junbo.common.cloudant.client.CloudantClientBulk
import com.junbo.common.cloudant.client.CloudantDbUri
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.CommunicationId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.identity.common.util.JsonHelper
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
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private static final Logger logger = LoggerFactory.getLogger(MigrationResourceImpl)

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
    private CommunicationRepository communicationRepository

    @Autowired
    private UserCommunicationRepository userCommunicationRepository

    @Autowired
    private NormalizeService normalizeService

    @Override
    @Transactional
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
            return checkCommunicationExists(oculusInput)
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

    @Override
    @Transactional
    Promise<Map<String, OculusOutput>> bulkMigrate(List<OculusInput> oculusInputs) {
        CloudantClientBase.useBulk = true
        CloudantClientBulk.callback = new MigrationQueryViewCallback()

        Map<String, OculusOutput> result = new HashMap<>()
        return Promise.each(oculusInputs) { OculusInput oculusInput ->
            return migrate(oculusInput).then { OculusOutput output ->
                result.put(oculusInput.currentId.toString(), output)
                return Promise.pure(null)
            }.recover { Throwable ex ->
                if (ex instanceof AppErrorException) {
                    OculusOutput output = new OculusOutput(error: ((AppErrorException)ex).error)
                    result.put(oculusInput.currentId.toString(), output)
                    JunboHttpContext.responseStatus = 400
                    return Promise.pure(null)
                } else {
                    throw ex;
                }
            }
        }.then {
            return CloudantClientBulk.commit()
        }.then {
            return Promise.pure(result)
        }.recover { Throwable exOuter ->
            logger.warn("Migrate bulk is under retry.", exOuter)

            // retry once
            result.clear()
            CloudantClientBulk.clearBulkData()
            JunboHttpContext.responseStatus = 200

            return Promise.each(oculusInputs) { OculusInput oculusInput ->
                return migrate(oculusInput).then { OculusOutput output ->
                    result.put(oculusInput.currentId.toString(), output)
                    return Promise.pure(null)
                }.recover { Throwable ex ->
                    if (ex instanceof AppErrorException) {
                        OculusOutput output = new OculusOutput(error: ((AppErrorException) ex).error)
                        result.put(oculusInput.currentId.toString(), output)
                        JunboHttpContext.responseStatus = 400
                        return Promise.pure(null)
                    } else {
                        throw ex;
                    }
                }
            }
        }
    }

    Promise<Void> createOrUpdateMigrateUser(OculusInput oculusInput) {
        return saveOrUpdateUser(oculusInput).then { User createdUser ->
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
            return saveOrUpdateUserCommunication(oculusInput, createdUser).then {
                return Promise.pure(createdUser)
            }
        }.then { User createdUser ->
            if (oculusInput.company == null) {
                return Promise.pure(new OculusOutput(
                        userId: createdUser.getId()
                ))
            }
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

    Promise<Void> saveOrUpdateUserCommunication(OculusInput oculusInput, User user) {
        if (CollectionUtils.isEmpty(oculusInput.communications)) {
            return Promise.pure(null)
        }

        return Promise.each(oculusInput.communications) { Map<String, Boolean> map ->
            if (map == null || map.isEmpty()) {
                return Promise.pure(null)
            }
            return Promise.each(map.entrySet()) { Map.Entry<String, Boolean> entry ->
                if (entry == null) {
                    return Promise.pure(null)
                }
                return userCommunicationRepository.searchByUserIdAndCommunicationId(user.getId(), new CommunicationId(entry.key),
                        Integer.MAX_VALUE, 0).then { List<UserCommunication> userCommunicationList ->
                    if (org.springframework.util.CollectionUtils.isEmpty(userCommunicationList) && entry.value) {
                        // create
                        return userCommunicationRepository.create(new UserCommunication(
                                userId: user.getId(),
                                communicationId: new CommunicationId(entry.key)
                        ))
                    } else if (org.springframework.util.CollectionUtils.isEmpty(userCommunicationList) && !entry.value) {
                        // do nothing
                        return Promise.pure(null)
                    } else {
                        UserCommunication userCommunication = userCommunicationList.get(0)
                        if (entry.value) {
                            // update
                            userCommunication.setUserId(user.getId())
                            userCommunication.setCommunicationId(new CommunicationId(entry.key))
                            return userCommunicationRepository.update(userCommunication)
                        } else {
                            // delete
                            return userCommunicationRepository.delete(userCommunication.getId())
                        }
                    }
                }
            }.then {
                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(null)
        }
    }

    Promise<User> saveOrUpdateUser(OculusInput oculusInput) {
        User user = new User(
                username: oculusInput.username,
                canonicalUsername: normalizeService.normalize(oculusInput.username),
                preferredLocale: new LocaleId(oculusInput.language),
                preferredTimezone: timeZoneMap.get(oculusInput.timezone),
                status: getMappedUserStatus(oculusInput),
                isAnonymous: StringUtils.isEmpty(oculusInput.username),
                profile: getMappedUserProfile(oculusInput),
                migratedUserId: oculusInput.currentId,
                createdTime: oculusInput.createdDate,
                updatedTime: oculusInput.updateDate
        )

        return userRepository.searchUserByMigrateId(oculusInput.currentId).then { User existing ->
            if (existing == null) {
                return userRepository.create(user)
            } else {
                existing.username = user.username
                existing.canonicalUsername = user.canonicalUsername
                existing.preferredLocale = user.preferredLocale
                existing.preferredTimezone = user.preferredTimezone
                existing.status = user.status
                existing.isAnonymous = user.isAnonymous
                existing.profile = user.profile
                existing.migratedUserId = user.migratedUserId
                existing.createdTime = user.createdTime
                existing.updatedTime = user.updatedTime
                return userRepository.update(existing)
            }
        }
    }

    Promise<User> checkEmailValid(OculusInput oculusInput) {
        return userRepository.searchUserByMigrateId(oculusInput.currentId).then { User existing ->
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

    // The logic here should be:
    // 1.   search organization by migratedCompanyId
    //      i.  if organization is null, this organization is valid, create organization with this name;
    //      ii. if organization isn't null, during the migration we will ignore this case
    Promise<Void> checkOrganizationValid(OculusInput oculusInput, User user) {
        if (oculusInput.company == null) {
            return Promise.pure(null)
        }

        if (oculusInput.company.name == null) {
            throw new IllegalArgumentException('company.name is null')
        }

        if (oculusInput.company.companyId == null) {
            throw new IllegalArgumentException('company.companyId is null')
        }

        checkCompanyType(oculusInput)

        return Promise.pure(null)
    }

    Promise<Void> checkCommunicationExists(OculusInput oculusInput) {
        if (CollectionUtils.isEmpty(oculusInput.communications)) {
            return Promise.pure(null)
        }

        return Promise.each(oculusInput.communications) { Map<String, Boolean> communicationIdMap ->
            return Promise.each(communicationIdMap.entrySet()) { Map.Entry<String, Boolean> entry ->
                if (entry.value == null) {
                    throw AppErrors.INSTANCE.fieldInvalid('communications').exception()
                }
                return communicationRepository.get(new CommunicationId(entry.key)).then { Communication communication ->
                    if (communication == null) {
                        throw AppErrors.INSTANCE.communicationNotFound(new CommunicationId(entry.key)).exception()
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(null)
        }
    }

    // The logic here should be:
    // 1.   search user by migratedUserId
    //      i.  if user is null, this username is valid, create user with this username;
    //      ii. if user isn't null, check the username is changed or not
    //              a.  If the username isn't changed, update user;
    //              b.  If the username is changed, check this username isn't used, then update user.
    Promise<Void> checkUserValid(OculusInput oculusInput) {
        if (StringUtils.isEmpty(oculusInput.status)) {
            throw new IllegalArgumentException('user Status error with currentId: ' + oculusInput.currentId)
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

        if (oculusInput.currentId == null) {
            throw AppErrors.INSTANCE.fieldInvalidException('currentId', 'Migration must have user\'s currentId').exception()
        }

        return userRepository.searchUserByMigrateId(oculusInput.currentId).then { User existingUser ->
            if (existingUser != null && existingUser.canonicalUsername == normalizeService.normalize(oculusInput.username)) {
                return Promise.pure(null)
            }

            return userRepository.searchUserByCanonicalUsername(normalizeService.normalize(oculusInput.username)).then { User newUser ->
                if (newUser == null) {
                    return Promise.pure(null)
                }

                throw AppErrors.INSTANCE.fieldInvalidException('username', 'username is already used by others').exception()
            }
        }
    }

    void checkCompanyType(OculusInput oculusInput) {
        if (oculusInput.company == null) {
            throw new IllegalArgumentException('company is missing with currentId: ' + oculusInput.currentId)
        }

        if (oculusInput.company.type == null) {
            throw new IllegalArgumentException('company.type is missing with currentId: ' + oculusInput.currentId)
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
            throw new IllegalArgumentException('password is null or empty with currentId: ' + oculusInput.currentId)
        }
        String[] passwords = oculusInput.password.split(":")
        if (passwords.length != 4 && passwords[0] != "1") {
            throw new IllegalArgumentException('password only accept version 1 with currentId: ' + oculusInput.currentId)
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
        // always set organization status to false
        /*
        if (oculusInput.status == MigrateUserStatus.ACTIVE.toString()) {
            return true
        }
        */
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
            webpage: oculusInput.profile.url,
            avatar: new UserAvatar(
                    href: oculusInput.profile.avatar == null ? null : oculusInput.profile.avatar.href
            )
        )

        return userProfile
    }

    private Promise<OculusOutput> migrateOrganization(OculusInput oculusInput, User createdUser) {
        // During the migration, remove all the relationship between this user and all his group
        return deleteAllUserGroup(createdUser).then {
            return saveOrUpdateOrganization(oculusInput, createdUser).then { Organization org ->
                OculusOutput oculusOutput = new OculusOutput(
                        userId: createdUser.getId(),
                        organizationId: org.getId()
                )

                return Promise.pure(oculusOutput)
            }
        }
    }

    private Promise<Void> deleteAllUserGroup(User createdUser) {
        return userGroupRepository.searchByUserId(createdUser.getId(), Integer.MAX_VALUE, 0).then { List<UserGroup> userGroupList ->
            if (CollectionUtils.isEmpty(userGroupList)) {
                return Promise.pure(null)
            }

            return Promise.each(userGroupList) { UserGroup userGroup ->
                return userGroupRepository.delete(userGroup.getId()).then {
                    return Promise.pure(null)
                }
            }
        }
    }

    // if organization.name exists, then
    //          i.  if the user is the first founder of the organization:
    //                      a.  update the organization;
    //                      b.  check and update role;
    //                      c.  check and update group;
    //                      d.  check and update roleAssignment
    //          ii. if the user isn't the first founder of the organization:
    //                      a.  if isAdmin = true,
    //                              a): If the user doesn't exist in "Admin" group, add the user to group having "Organization Admin",
    //                              b): If the user exist in "Admin" group, do nothing
    //                      b. if isAdmin = false,
    //                              a): If the user doesn't exist in "Developer" group, add the user to "Developer" group; if it doesn't exists, create "Developer" group.
    //                              b): If the user exists in "Developer" group, do nothing
    // if organization.name doesn't exists, then
    //          i.  create organization, create group with "Organization Admin" role and "Developer Admin" role;
    Promise<Organization> saveOrUpdateOrganization(OculusInput oculusInput, User createdUser) {
        return organizationRepository.searchByMigrateCompanyId(oculusInput.company.companyId).then { Organization organization ->
            if (organization == null) {
                // create organization, create role, create group
                return createNewOrg(oculusInput, createdUser)
            } else {
                // user is not the first found of the organization
                return updateNewOrg(oculusInput, createdUser, organization)
            }
        }
    }

    private Promise<Organization> updateNewOrg(OculusInput oculusInput, User createdUser, Organization existingOrg) {
        return saveOrUpdateOrg(oculusInput, existingOrg).then { Organization org ->
            return addToUserGroup(oculusInput, createdUser, org)
        }
    }

    private Promise<Organization> createNewOrg(OculusInput oculusInput, User createdUser) {
        // create organization, create role, create group
        Role createdRole = null
        Group createdGroup = null
        return saveOrUpdateOrg(oculusInput, null).then { Organization organization ->
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
            return removeUserGroupMemberShip(oculusInput, createdUser, existingOrganization, DEVELOPER_ROLE).then {
                return groupRepository.searchByOrganizationIdAndName(existingOrganization.getId(), ADMIN_ROLE, Integer.MAX_VALUE, null).then { Group group ->
                    if (group == null) {
                        return saveOrUpdateGroup(existingOrganization, ADMIN_ROLE).then { Group newGroup ->
                            return saveOrUpdateUserGroup(createdUser, newGroup).then {
                                return Promise.pure(existingOrganization)
                            }
                        }
                    }
                    return saveOrUpdateUserGroup(createdUser, group).then {
                        return Promise.pure(existingOrganization)
                    }
                }
            }
        } else {
            // Add to DEVELOPER group
            return removeUserGroupMemberShip(oculusInput, createdUser, existingOrganization, ADMIN_ROLE).then {
                return groupRepository.searchByOrganizationIdAndName(existingOrganization.getId(), DEVELOPER_ROLE, Integer.MAX_VALUE, null).then { Group group ->
                    if (group == null) {
                        return saveOrUpdateGroup(existingOrganization, DEVELOPER_ROLE).then { Group newGroup ->
                            return saveOrUpdateUserGroup(createdUser, newGroup).then {
                                return Promise.pure(existingOrganization)
                            }
                        }
                    }

                    return saveOrUpdateUserGroup(createdUser, group).then {
                        return Promise.pure(existingOrganization)
                    }
                }
            }
        }
    }


    private Promise<Void> removeUserGroupMemberShip(OculusInput oculusInput, User createdUser, Organization existingOrganization, String roleName) {
        // remove from developer group
        return groupRepository.searchByOrganizationIdAndName(existingOrganization.getId(), roleName, Integer.MAX_VALUE, null).then { Group group ->
            if (group == null) {
                return Promise.pure(null)
            }

            return userGroupRepository.searchByUserIdAndGroupId(createdUser.getId(), group.getId(), Integer.MAX_VALUE, null).then { List<UserGroup> userGroupList ->
                if (CollectionUtils.isEmpty(userGroupList)) {
                    return Promise.pure(null)
                }

                return Promise.each(userGroupList) { UserGroup userGroup ->
                    return userGroupRepository.delete(userGroup.getId()).then {
                        return Promise.pure(null)
                    }
                }.then {
                    return Promise.pure(null)
                }
            }
        }
    }

    private Promise<Organization> saveOrUpdateOrg(OculusInput oculusInput, Organization existingOrg) {
        if (existingOrg == null) {
            Organization org = new Organization()
            org.name = oculusInput.company.name
            org.migratedCompanyId = oculusInput.company.companyId
            org.canonicalName = normalizeService.normalize(oculusInput.company.name)
            org.ownerId = null
            org.isValidated = getOrganizationStatus(oculusInput)
            org.type = oculusInput.company.type
            org.createdTime = oculusInput.createdDate
            org.updatedTime = oculusInput.updateDate

            return organizationRepository.create(org).then { Organization createdOrg ->
                return getOrgShippingAddressId(oculusInput, createdOrg).then { UserPersonalInfoId orgShippingAddressId ->
                    org.shippingAddress = orgShippingAddressId
                    return Promise.pure(createdOrg)
                }
            }.then { Organization createdOrg ->
                return getOrgPhoneId(oculusInput, createdOrg).then { UserPersonalInfoId orgPhoneId ->
                    org.shippingPhone = orgPhoneId
                    return Promise.pure(createdOrg)
                }
            }.then { Organization createdOrg ->
                return retryOrganizationUpdate(createdOrg)
            }
        } else {
            existingOrg.name = oculusInput.company.name
            existingOrg.isValidated = getOrganizationStatus(oculusInput)
            existingOrg.type = oculusInput.company.type
            existingOrg.migratedCompanyId = oculusInput.company.companyId
            existingOrg.createdTime = oculusInput.createdDate
            existingOrg.updatedTime = oculusInput.updateDate

            return getOrgShippingAddressId(oculusInput, existingOrg).then { UserPersonalInfoId orgShippingAddressId ->
                existingOrg.shippingAddress = orgShippingAddressId
                return Promise.pure(existingOrg)
            }.then { Organization updatedOrg ->
                return getOrgPhoneId(oculusInput, updatedOrg).then { UserPersonalInfoId orgPhoneId ->
                    existingOrg.shippingPhone = orgPhoneId
                    return Promise.pure(existingOrg)
                }
            }.then { Organization updatedOrg ->
                return retryOrganizationUpdate(updatedOrg)
            }
        }
    }

    private Promise<Organization> retryOrganizationUpdate(Organization existingOrg) {
        return organizationRepository.get(existingOrg.getId()).then { Organization organization ->
            if (existingOrg == organization) {
                return Promise.pure(existingOrg)
            }
            existingOrg.rev = organization.rev
            return organizationRepository.update(existingOrg)
        }.recover { Throwable e ->
            // retry here
            return organizationRepository.get(existingOrg.getId()).then { Organization organization ->
                if (existingOrg == organization) {
                    return Promise.pure(existingOrg)
                }
                existingOrg.rev = organization.rev
                return organizationRepository.update(existingOrg)
            }
        }.recover { Throwable e ->
            // retry here
            return organizationRepository.get(existingOrg.getId()).then { Organization organization ->
                if (existingOrg == organization) {
                    return Promise.pure(existingOrg)
                }
                existingOrg.rev = organization.rev
                return organizationRepository.update(existingOrg)
            }
        }.recover { Throwable e ->
            // retry here
            return organizationRepository.get(existingOrg.getId()).then { Organization organization ->
                if (existingOrg == organization) {
                    return Promise.pure(existingOrg)
                }
                existingOrg.rev = organization.rev
                return organizationRepository.update(existingOrg)
            }
        }.recover { Throwable e ->
            return Promise.pure(null)
        }.then {
            return Promise.pure(existingOrg)
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

    // check whether the address is changed or not
    // if the address is changed, just create new and return;
    // if the address isn't changed, just return
    // Due to state isn't valid in oculus side, so we decided to use street2 to put oculus' state
    private Promise<UserPersonalInfoId> getOrgShippingAddressId(OculusInput oculusInput, Organization createdOrg) {
        Address address = new Address(
            street1: oculusInput.company.address,
            city: oculusInput.company.city,
            street2: oculusInput.company.state,
            countryId: new CountryId(oculusInput.company.country),
            postalCode: oculusInput.company.postalCode
        )

        return checkAddressEqual(address, createdOrg).then { Boolean isEquals ->
            if (isEquals) {
                return Promise.pure(createdOrg.shippingAddress)
            }

            UserPersonalInfo orgShippingAddress = new UserPersonalInfo()
            orgShippingAddress.userId = null
            orgShippingAddress.organizationId = createdOrg.getId()
            orgShippingAddress.type = UserPersonalInfoType.ADDRESS.toString()
            orgShippingAddress.value = ObjectMapperProvider.instance().valueToTree(address)
            orgShippingAddress.createdTime = oculusInput.createdDate
            orgShippingAddress.updatedTime = oculusInput.updateDate

            return userPersonalInfoRepository.create(orgShippingAddress).then { UserPersonalInfo userPersonalInfo ->
                return Promise.pure(userPersonalInfo.getId())
            }
        }
    }

    private Promise<Boolean> checkAddressEqual(Address address, Organization createdOrg) {
        if (createdOrg == null || createdOrg.shippingAddress == null) {
            return Promise.pure(false)
        }

        return userPersonalInfoRepository.get(createdOrg.shippingAddress).then { UserPersonalInfo userPersonalInfo ->
            Address existingAddress = (Address)JsonHelper.jsonNodeToObj(userPersonalInfo.value, Address)

            if (address != existingAddress) {
                return Promise.pure(false)
            }

            return Promise.pure(true)
        }
    }

    private Promise<UserPersonalInfoId> getOrgPhoneId(OculusInput oculusInput, Organization createdOrg) {
        PhoneNumber phoneNumber = new PhoneNumber(
                info: oculusInput.company.phone
        )

        return checkPhoneEqual(phoneNumber, createdOrg).then { Boolean isEquals ->
            if (isEquals) {
                return Promise.pure(createdOrg.shippingPhone)
            }

            UserPersonalInfo orgPhone = new UserPersonalInfo()
            orgPhone.userId = null
            orgPhone.organizationId = createdOrg.getId()
            orgPhone.type = UserPersonalInfoType.PHONE.toString()
            orgPhone.value = ObjectMapperProvider.instance().valueToTree(phoneNumber)
            orgPhone.createdTime = oculusInput.createdDate
            orgPhone.updatedTime = oculusInput.updateDate

            return userPersonalInfoRepository.create(orgPhone).then { UserPersonalInfo userPersonalInfo ->
                return Promise.pure(userPersonalInfo.getId())
            }
        }
    }

    private Promise<Boolean> checkPhoneEqual(PhoneNumber phoneNumber, Organization createdOrg) {
        if (createdOrg == null || createdOrg.shippingPhone == null) {
            return Promise.pure(false)
        }

        return userPersonalInfoRepository.get(createdOrg.shippingPhone).then { UserPersonalInfo userPersonalInfo ->
            PhoneNumber existingPhoneNumber = (PhoneNumber)JsonHelper.jsonNodeToObj(userPersonalInfo.value, PhoneNumber)

            if (existingPhoneNumber != phoneNumber) {
                return Promise.pure(false)
            }

            return Promise.pure(true)
        }
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

    private static class MigrationQueryViewCallback implements CloudantClientBulk.Callback {
        private static CloudantMarshaller marshaller = DefaultCloudantMarshaller.instance()

        void onQueryView(CloudantQueryResult results, CloudantDbUri dbUri, String viewName, String key) {
            if (dbUri.dbName == "user" && viewName == "by_migrate_user_id") {
                def bulkCache = CloudantClientBulk.getBulkReadonly(dbUri)
                searchUserByMigrateId(results, key, bulkCache)
            } else if (dbUri.dbName == "organization" && viewName == "by_migrate_company_id") {
                def bulkCache = CloudantClientBulk.getBulkReadonly(dbUri)
                searchByMigrateCompanyId(results, key, bulkCache)
            } else if (dbUri.dbName == "group" && viewName == "by_organization_id") {
                def bulkCache = CloudantClientBulk.getBulkReadonly(dbUri)
                searchGroupByOrganizationId(results, key, bulkCache)
            }
        }

        @Override
        void onQueryView(CloudantQueryResult results, CloudantDbUri dbUri, String viewName, String startKey, String endKey) {
        }

        @Override
        void onQueryView(CloudantQueryResult results, CloudantDbUri dbUri, String viewName, Object[] startKey, Object... endKey) {
            if (dbUri.dbName == "group" && viewName == "by_organization_id_and_name") {
                def bulkCache = CloudantClientBulk.getBulkReadonly(dbUri)
                searchGroupByOrganizationIdAndName(results, startKey, endKey, bulkCache)
            }
        }

        void onSearch(CloudantQueryResult results, CloudantDbUri dbUri, String searchName, String queryString) {
            throw new RuntimeException("Not supported.");
        }

        private void searchUserByMigrateId(CloudantQueryResult results, String key, Map<String, CloudantClientBulk.EntityWithType> bulkCache) {
            for (CloudantClientBulk.EntityWithType entityWithType : bulkCache.values()) {
                User user = (User)marshaller.unmarshall(entityWithType.entity, entityWithType.type)
                if (user.getMigratedUserId().toString() == key) {
                    results.rows.add(new CloudantQueryResult.ResultObject(
                            id: user.cloudantId,
                            key: key,
                            value: user.cloudantId,
                            doc: user
                    ))
                }
            }
        }

        private void searchByMigrateCompanyId(CloudantQueryResult results, String key, Map<String, CloudantClientBulk.EntityWithType> bulkCache) {
            for (CloudantClientBulk.EntityWithType entityWithType : bulkCache.values()) {
                Organization org = (Organization)marshaller.unmarshall(entityWithType.entity, entityWithType.type)
                if (org.getMigratedCompanyId().toString() == key) {
                    results.rows.add(new CloudantQueryResult.ResultObject(
                            id: org.cloudantId,
                            key: key,
                            value: org.cloudantId,
                            doc: org
                    ))
                }
            }
        }

        private void searchGroupByOrganizationIdAndName(CloudantQueryResult results, Object[] startKey, Object[] endKey, Map<String, CloudantClientBulk.EntityWithType> bulkCache) {
            for (CloudantClientBulk.EntityWithType entityWithType : bulkCache.values()) {
                Group group = (Group)marshaller.unmarshall(entityWithType.entity, entityWithType.type)
                if (isGroupInRange(startKey, endKey, group.organizationId.toString(), group.name)) {
                    results.rows.add(new CloudantQueryResult.ResultObject(
                            id: group.cloudantId,
                            key: [group.organizationId.toString(), group.name],
                            value: group.cloudantId,
                            doc: group
                    ))
                }
            }
        }

        // It must be as the format as startKey: [organizationId, name], endKey: [organizationId, name]
        // Here we won't consider the scenarios for non-string comparation.
        private boolean isGroupInRange(Object[] startKey, Object[] endKey, String organizationId, String name) {
            assert startKey != null
            assert startKey.size() == 2

            assert endKey != null
            assert endKey.size() == 2

            String startOrganizationId = startKey[0].toString()
            String startName = startKey[1].toString()

            String endOrganizationId = endKey[0].toString()
            String endName = endKey[1].toString()

            return startOrganizationId <= organizationId && organizationId <= endOrganizationId && startName <= name && name <= endName
        }

        private void searchGroupByOrganizationId(CloudantQueryResult results, String key, Map<String, CloudantClientBulk.EntityWithType> bulkCache) {
            for (CloudantClientBulk.EntityWithType entityWithType : bulkCache.values()) {
                Group group = (Group)marshaller.unmarshall(entityWithType.entity, entityWithType.type)
                if ("${group.getOrganizationId().value}" == key) {
                    results.rows.add(new CloudantQueryResult.ResultObject(
                            id: group.cloudantId,
                            key: key,
                            value: group.cloudantId,
                            doc: group
                    ))
                }
            }
        }
    }
}
