package com.junbo.data.handler

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.data.model.UserAddressData
import com.junbo.data.model.UserData
import com.junbo.data.model.UserNameData
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserCredentialResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.client.TypeReference
import com.junbo.langur.core.context.JunboHttpContext
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource
import org.springframework.util.StringUtils

import java.security.SecureRandom

/**
 * Created by haomin on 14-7-11.
 */
@CompileStatic
class UserDataHandler extends BaseDataHandler {
    private UserResource userResource
    private UserCredentialResource userCredentialResource
    private UserPersonalInfoResource userPersonalInfoResource
    private static final char[] CHARSET =
            "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserCredentialResource(UserCredentialResource userCredentialResource) {
        this.userCredentialResource = userCredentialResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Override
    void handle(String content) {
        UserData userData
        try {
            userData = transcoder.decode(new TypeReference<UserData>() {}, content) as UserData
        } catch (Exception e) {
            logger.error("Error parsing user $content", e)
            exit()
        }

        String username = userData.username

        int length = 10
        Random random = new SecureRandom()
        byte[] bytes = new byte[length]
        random.nextBytes(bytes)
        char[] chars = new char[length]
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char)CHARSET[((bytes[i] & 0xFF) % CHARSET.length)];
        }
        String password = new String(chars) + 'Ww0'
        if (!StringUtils.isEmpty(userData.password)) {
            password = userData.password
        }
        String email = userData.email
        CountryId cor = userData.cor
        UserAddressData userAddressData = userData.address
        UserNameData userNameData = userData.name

        User user = new User()
        user.status = 'ACTIVE'
        user.isAnonymous = true
        user.countryOfResidence = cor

        User existing = null
        try {
            Results<User> results = userResource.list(new UserListOptions(username: username)).get()
            if (results != null && results.items != null && results.items.size() > 0) {
                existing = results.items.get(0)
            }
        } catch (AppErrorException e) {
            logger.debug('error happens', e)
        }
        if (existing != null) {
            Boolean updated = false
            // update username
            if (existing.username != null) {
                UserPersonalInfo usernamePII = userPersonalInfoResource.get(existing.username, new UserPersonalInfoGetOptions()).get()
                UserLoginName loginName = (UserLoginName)JsonHelper.jsonNodeToObj(usernamePII.value, UserLoginName)
                if (loginName.userName != username) {
                    updated = true
                }
            }
            if (updated || existing.username == null) {
                existing = createUserNamePII(username, existing)
            }

            // create or update Email
            updated = false || createOrUpdateEmail(email, existing)

            // create or update name
            updated = updated || createOrUpdateName(userNameData, existing)

            if (updated) {
                userResource.silentPut(existing.getId(), existing).get()
            }

            // Existing user's credential won't be updated.
        } else {
            User created = null
            logger.debug("Create new user with username: $username")
            try {
                created = userResource.create(user).get()
            } catch (Exception e) {
                logger.error("Error creating user $user.username.", e)
            }
            created = createUserNamePII(userData.username, created)

            logger.debug("Create new user credential for username: $username")
            createUserCredential(password, created)

            logger.debug("Create new user email for username: $username")
            try {
                // user mail
                createOrUpdateEmail(email, created)

                //Address Pii
                createOrUpdateAddress(userAddressData, created)

                //Name Pii
                createOrUpdateName(userNameData, created)

                userResource.silentPut(created.id as UserId, created).get()

            } catch (Exception e) {
                logger.error("Error creating user credential for $user.username.", e)
            }
        }
        JunboHttpContext.requestHeaders.remove("X-DISABLE-EMAIL")
    }

    private User createUserNamePII(String username, User user) {
        logger.debug("Create new user with username: $username")
        try {
            UserPersonalInfo createdPersonalInfo = userPersonalInfoResource.create(new UserPersonalInfo(
                    userId: user.getId(),
                    type: 'USERNAME',
                    value: ObjectMapperProvider.instance().valueToTree(new UserLoginName(
                            userName: username
                    ))
            )).get()
            user.username = createdPersonalInfo.getId()
            user.isAnonymous = false
            return userResource.silentPut(user.getId(), user).get()
        } catch (Exception e) {
            logger.error("Error creating user $user.username.", e)
        }
        return user
    }

    private void createUserCredential(String password, User user) {
        try {
            userCredentialResource.create(user.id as UserId, new UserCredential(value: password, type: 'PASSWORD')).get()
        } catch (Exception e) {
            logger.error("Error creating user credential for $user.username.", e)
        }
    }

    private Boolean createOrUpdateEmail(String email, User user) {
        if (StringUtils.isEmpty(email) && !CollectionUtils.isEmpty(user.emails)) {
            user.emails = []
        }

        if (!CollectionUtils.isEmpty(user.emails)) {
            UserPersonalInfo emailPII = userPersonalInfoResource.get(user.emails.get(0).getValue(), new UserPersonalInfoGetOptions()).get()
            Email mail = (Email)JsonHelper.jsonNodeToObj(emailPII.value, Email)

            if (mail.info != email) {
                UserPersonalInfo emailPii = new UserPersonalInfo(
                        userId: user.id as UserId,
                        type: 'EMAIL',
                        value: ObjectMapperProvider.instance().valueToTree(new Email(info: email))
                )
                // set as validated
                emailPii.lastValidateTime = new Date()
                UserPersonalInfo newEmailPii = userPersonalInfoResource.create(emailPii).get()

                user.emails = [new UserPersonalInfoLink(
                        isDefault: true,
                        value: newEmailPii.id as UserPersonalInfoId
                )]

                return true
            }
        } else {
            UserPersonalInfo emailPii = new UserPersonalInfo(
                    userId: user.id as UserId,
                    type: 'EMAIL',
                    value: ObjectMapperProvider.instance().valueToTree(new Email(info: email))
            )
            // set as validated
            emailPii.lastValidateTime = new Date()
            UserPersonalInfo newEmailPii = userPersonalInfoResource.create(emailPii).get()

            user.emails = [new UserPersonalInfoLink(
                    isDefault: true,
                    value: newEmailPii.id as UserPersonalInfoId
            )]

            return true
        }

        return false
    }

    private Boolean createOrUpdateAddress(UserAddressData userAddressData, User user) {
        if (userAddressData == null && !CollectionUtils.isEmpty(user.addresses)) {
            user.addresses = []
        }

        if (!CollectionUtils.isEmpty(user.addresses)) {
            UserPersonalInfo addressPII = userPersonalInfoResource.get(user.addresses.get(0).getValue(), new UserPersonalInfoGetOptions()).get()
            Address address = (Address)JsonHelper.jsonNodeToObj(addressPII.value, Address)

            if (address.city != userAddressData.city || address.countryId != userAddressData.countryId
             || address.postalCode != userAddressData.postalCode || address.subCountry != userAddressData.subCountry
             || address.street1 != userAddressData.street1 || address.street2 != userAddressData.street2 || address.street3 != userAddressData.street3) {
                UserPersonalInfo addressPii = new UserPersonalInfo(
                        userId: user.id as UserId,
                        type: 'ADDRESS',
                        value: ObjectMapperProvider.instance().valueToTree(new Address(
                                street1: userAddressData.street1,
                                street2: userAddressData.street2,
                                street3: userAddressData.street3,
                                city: userAddressData.city,
                                subCountry: userAddressData.subCountry,
                                countryId: userAddressData.countryId,
                                postalCode: userAddressData.postalCode
                        ))
                )
                UserPersonalInfo newAddressPii = userPersonalInfoResource.create(addressPii).get()

                user.addresses = [new UserPersonalInfoLink(
                        isDefault: true,
                        value: newAddressPii.id as UserPersonalInfoId
                )]

                return true
            }
        } else {
            UserPersonalInfo addressPii = new UserPersonalInfo(
                    userId: user.id as UserId,
                    type: 'ADDRESS',
                    value: ObjectMapperProvider.instance().valueToTree(new Address(
                            street1: userAddressData.street1,
                            street2: userAddressData.street2,
                            street3: userAddressData.street3,
                            city: userAddressData.city,
                            subCountry: userAddressData.subCountry,
                            countryId: userAddressData.countryId,
                            postalCode: userAddressData.postalCode
                    ))
            )
            UserPersonalInfo newAddressPii = userPersonalInfoResource.create(addressPii).get()

            user.addresses = [new UserPersonalInfoLink(
                    isDefault: true,
                    value: newAddressPii.id as UserPersonalInfoId
            )]

            return true
        }

        return false
    }

    private Boolean createOrUpdateName(UserNameData userNameData, User user) {
        if (userNameData == null && user.name != null) {
            user.name = null
        }

        if (user.name != null) {
            UserPersonalInfo namePII = userPersonalInfoResource.get(user.name, new UserPersonalInfoGetOptions()).get()
            UserName name = (UserName)JsonHelper.jsonNodeToObj(namePII.value, UserName)

            if (userNameData.familyName != name.familyName || userNameData.givenName != name.givenName) {
                UserPersonalInfo namePii = new UserPersonalInfo(
                        userId: user.id as UserId,
                        type: 'NAME',
                        value: ObjectMapperProvider.instance().valueToTree(new UserName(
                                givenName: userNameData.givenName,
                                //middleName: userNameData.middleName,
                                familyName: userNameData.familyName,
                                //nickName: userNameData.nickName
                        ))
                )
                UserPersonalInfo newNamePii = userPersonalInfoResource.create(namePii).get()
                user.name = newNamePii.id as UserPersonalInfoId

                return true
            }
        } else {
            UserPersonalInfo namePii = new UserPersonalInfo(
                    userId: user.id as UserId,
                    type: 'NAME',
                    value: ObjectMapperProvider.instance().valueToTree(new UserName(
                            givenName: userNameData.givenName,
                            //middleName: userNameData.middleName,
                            familyName: userNameData.familyName,
                            //nickName: userNameData.nickName
                    ))
            )
            UserPersonalInfo newNamePii = userPersonalInfoResource.create(namePii).get()
            user.name = newNamePii.id as UserPersonalInfoId

            return true
        }

        return false
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }
}
