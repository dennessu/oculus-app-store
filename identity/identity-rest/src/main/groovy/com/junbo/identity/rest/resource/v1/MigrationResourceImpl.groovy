package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.data.identifiable.UserPasswordStrength
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
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
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 6/6/14.
 */
@Transactional
@CompileStatic
class MigrationResourceImpl implements MigrationResource {

    @Autowired
    private OrganizationRepository organizationRepository

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
            return checkUserStatusValid(oculusInput)
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
                preferredLocale: new LocaleId(mapToLocaleCode(oculusInput.language)),
                preferredTimezone: timeZoneMap.get(oculusInput.timezone),
                status: getMappedUserStatus(oculusInput),
                isAnonymous: StringUtils.isEmpty(oculusInput.username),
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
            if (StringUtils.isEmpty(oculusInput.devCenterCompany)) {
                return Promise.pure(null)
            }

            Organization organization = new Organization(
                    ownerId: (UserId)createdUser.id,
                    name: oculusInput.devCenterCompany,
                    canonicalName: normalizeService.normalize(oculusInput.devCenterCompany),
                    isValidated: getOrganizationStatus(oculusInput),
                    headline: getHeadline(oculusInput),
                    summary: getSummary(oculusInput),
                    url: getUrl(oculusInput)
            )

            return saveOrUpdateOrganization(organization).then { Organization createdOrganization ->
                OculusOutput output = new OculusOutput(
                        userId: (UserId)createdUser.id,
                        organizationId: (OrganizationId)createdOrganization.id
                )
                return Promise.pure(output)
            }
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

    Promise<Organization> saveOrUpdateOrganization(Organization organization) {
        return organizationRepository.searchByOwner(organization.ownerId, Integer.MAX_VALUE, 0).then { List<Organization> organizationList ->
            if (CollectionUtils.isEmpty(organizationList)) {
               // create new organization
                return organizationRepository.create(organization)
            } else {
                // update the first organization
                Organization organizationToUpdate = organizationList.get(0)
                organizationToUpdate.name = organization.name
                organizationToUpdate.isValidated = organization.isValidated
                organizationToUpdate.canonicalName = organization.canonicalName
                organizationToUpdate.headline = organization.headline
                organizationToUpdate.summary = organization.summary
                organizationToUpdate.url = organization.url
                return organizationRepository.update(organizationToUpdate)
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

                throw AppErrors.INSTANCE.userEmailAlreadyUsed(oculusInput.currentId, oculusInput.email).exception()
            }
        }
    }

    Promise<Void> checkOrganizationValid(OculusInput oculusInput, User user) {
        return organizationRepository.searchByCanonicalName(normalizeService.normalize(oculusInput.devCenterCompany),
                Integer.MAX_VALUE, 0).then { List<Organization> organizationList ->
            if (CollectionUtils.isEmpty(organizationList)) {
                return Promise.pure(null)
            }

            organizationList.removeAll { Organization org ->
                return user != null && org.ownerId == user.id
            }

            if (CollectionUtils.isEmpty(organizationList)) {
                return Promise.pure(null)
            }

            throw AppErrors.INSTANCE.organizationAlreadyUsed(oculusInput.devCenterCompany).exception()
        }
    }

    Promise<Void> checkUserStatusValid(OculusInput oculusInput) {
        if (StringUtils.isEmpty(oculusInput.status)) {
            throw new IllegalArgumentException('user Status error')
        }
        List<String> allowedValues = MigrateUserStatus.values().collect { MigrateUserStatus userStatus ->
            userStatus.toString()
        }

        if (!(oculusInput.status in allowedValues)) {
            throw AppErrors.INSTANCE.fieldInvalid('status', allowedValues.join(',')).exception()
        }

        return Promise.pure(null)
    }

    Promise<Void> checkPasswordValid(OculusInput oculusInput) {
        if (oculusInput.oldPasswordHash) {
            throw new IllegalArgumentException('user has old PasswordHash')
        }
        if (StringUtils.isEmpty(oculusInput.password)) {
            throw new IllegalArgumentException('password is null or empty')
        }
        String[] passwords = oculusInput.password.split(":")
        if (passwords.length != 4 && passwords[0] != "1") {
            throw new IllegalArgumentException('password only accept version 1')
        }

        return Promise.pure(null)
    }

    private String mapToLocaleCode(String language) {
        if ("en".equalsIgnoreCase(language)) {
            return "en_US"
        } else {
            throw new IllegalArgumentException('language is not supported.')
            // to determine, throw exception first
            // return
        }
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

    private String getHeadline(OculusInput oculusInput) {
        if (oculusInput.shareProfile != null) {
            return oculusInput.shareProfile.headline
        }

        return null
    }

    private String getSummary(OculusInput oculusInput) {
        if (oculusInput.shareProfile != null) {
            return oculusInput.shareProfile.summary
        }
        return null
    }

    private String getUrl(OculusInput oculusInput) {
        if (oculusInput.shareProfile != null) {
            return oculusInput.shareProfile.url
        }

        return null
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
}
