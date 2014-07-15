package com.junbo.data.handler

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.data.model.UserData
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.resource.UserCredentialResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by haomin on 14-7-11.
 */
@CompileStatic
class UserDataHandler extends BaseDataHandler {
    private UserResource userResource
    private UserCredentialResource userCredentialResource
    private UserPersonalInfoResource userPersonalInfoResource

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
        String password = userData.password
        String email = userData.email

        User user = new User()
        user.username = username
        user.status = 'ACTIVE'
        user.isAnonymous = false

        logger.info("loading user $username")

        User existing = null
        try {
            Results<User> results = userResource.list(new UserListOptions(username: username)).syncGet()
            if (results != null && results.items != null && results.items.size() > 0) {
                existing = results.items.get(0)
            }
        } catch (AppErrorException e) {
            logger.debug('error happens', e)
        }

        if (existing == null) {
            User created = null
            logger.debug("Create new user with username: $username")
            try {
                created = userResource.create(user).syncGet()
            } catch (Exception e) {
                logger.error("Error creating user $user.username.", e)
            }

            logger.debug("Create new user credential for username: $username")
            try {
                userCredentialResource.create(created.id as UserId, new UserCredential(value: password, type: 'PASSWORD')).syncGet()
            } catch (Exception e) {
                logger.error("Error creating user credential for $user.username.", e)
            }

            logger.debug("Create new user email for username: $username")
            try {
                UserPersonalInfo emailPii = new UserPersonalInfo(
                        userId: created.id as UserId,
                        type: 'EMAIL',
                        value: ObjectMapperProvider.instance().valueToTree(new Email(info: email))
                )
                UserPersonalInfo newEmailPii = userPersonalInfoResource.create(emailPii).syncGet()
                created.emails = [new UserPersonalInfoLink(
                        isDefault: true,
                        value: newEmailPii.id as UserPersonalInfoId
                )]

                userResource.put(created.id as UserId, created).syncGet()

            } catch (Exception e) {
                logger.error("Error creating user credential for $user.username.", e)
            }
        }
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }
}
