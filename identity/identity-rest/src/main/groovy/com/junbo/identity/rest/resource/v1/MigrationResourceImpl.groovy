package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.OrganizationValidator
import com.junbo.identity.core.service.validator.UserPasswordValidator
import com.junbo.identity.core.service.validator.UserPersonalInfoValidator
import com.junbo.identity.core.service.validator.UserValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.model.migration.OculusInput
import com.junbo.identity.spec.v1.resource.migration.MigrationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
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
    Promise<Void> migrate(OculusInput oculusInput) {
        if (StringUtils.isEmpty(oculusInput.username)) {
            throw new IllegalArgumentException('username can\'t be null')
        }
        if (oculusInput.oldPasswordHash) {
            throw new IllegalArgumentException('user has old PasswordHash')
        }
        // If the user exists, update current user's information;
        // else, create user's information
        return userRepository.getUserByCanonicalUsername(normalizeService.normalize(oculusInput.username)).then { User user ->
            if (user == null) {
                return createMigrateUser(oculusInput)
            } else {
                return updateMigrateUser(oculusInput)
            }
        }
    }

    Promise<Void> createMigrateUser(OculusInput oculusInput) {
        User user = new User(
                username: oculusInput.username,
                canonicalUsername: normalizeService.normalize(oculusInput.username),
                preferredLocale: new LocaleId(mapToLocaleCode(oculusInput.language)),
                preferredTimezone: timeZoneMap.get(oculusInput.timeZone),
                status: mapToStatus(oculusInput.status),
                createdTime: oculusInput.createdDate,
                updatedTime: oculusInput.updateDate
        )

        return userRepository.create(user).then { User createdUser ->
            // Create user name
            UserName userName = new UserName(
                givenName: oculusInput.firstName,
                familyName: oculusInput.lastName,
                nickName: oculusInput.nickName
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
                return createdUser
            }
        }.then { User createdUser ->
            Email userEmail = new Email(
                    info: oculusInput.email
            )
            UserPersonalInfo email = new UserPersonalInfo(
                    userId: (UserId)createdUser.id,
                    type: UserPersonalInfoType.EMAIL.toString(),
                    value: ObjectMapperProvider.instance().valueToTree(userEmail),
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
                return createdUser
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
                return createdUser
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
            if (StringUtils.isEmpty(oculusInput.devCenterCompany)) {
                return Promise.pure(null)
            }

            Organization organization = new Organization(
                    ownerId: (UserId)createdUser.id,
                    name: oculusInput.devCenterCompany,
                    isValidated: false
            )

            return organizationRepository.create(organization).then {
                return Promise.pure(null)
            }
        }
    }

    Promise<Void> updateMigrateUser(OculusInput oculusInput) {
        return Promise.pure(null)
    }

    private String mapToLocaleCode(String language) {
        if ("en".equalsIgnoreCase(language)) {
            return "en-US"
        } else {
            throw new IllegalArgumentException('language is not supported.')
            // to determine, throw exception first
            // return
        }
    }

    private String mapToStatus(String status) {
        if ("ACTIVE".equalsIgnoreCase(status)) {
            return UserStatus.ACTIVE.toString()
        } else {
            // Need to do map
            throw new IllegalArgumentException('Unknown status')
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
}
