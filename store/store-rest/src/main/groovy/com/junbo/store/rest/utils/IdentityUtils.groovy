package com.junbo.store.rest.utils

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.identity.PersonalInfo
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The IdentityUtils class.
 */
@CompileStatic
@Component('storeIdentityUtils')
class IdentityUtils {

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = "storeDataConverter")
    private DataConverter dataConverter

    Promise<UserPersonalInfo> createPhoneInfoIfNotExist(UserId userId, PersonalInfo phoneInfo) {
        PhoneNumber updatePhone = ObjectMapperProvider.instance().treeToValue(phoneInfo.value, PhoneNumber)
        return resourceContainer.userPersonalInfoResource.list(new UserPersonalInfoListOptions(type: 'PHONE', phoneNumber: updatePhone.info)).then { Results<UserPersonalInfo> results ->
            UserPersonalInfo result = results.items.find {UserPersonalInfo e -> e.userId == userId}
            if (result == null) {
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(userId: userId, type: 'PHONE', value: phoneInfo.value)
                )
            } else {
                return Promise.pure(result)
            }
        }.syncThen { UserPersonalInfo userPersonalInfo ->
            return dataConverter.toStorePersonalInfo(userPersonalInfo, null)
        }
    }

    public Promise<User> getActiveUserFromToken() {
        UserId userId = AuthorizeContext.currentUserId
        return resourceContainer.userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user.isAnonymous || user.status != Constants.UserStatus.ACTIVE) {
                throw AppErrors.INSTANCE.invalidUserStatus().exception()
            }

            return Promise.pure(user)
        }
    }

    public Promise<User> getVerifiedUserFromToken() {
        return getActiveUserFromToken().then { User user ->
            if (CollectionUtils.isEmpty(user.emails)) {
                throw AppErrors.INSTANCE.userEmailNotFound().exception()
            }

            UserPersonalInfoLink link = user.emails.find { UserPersonalInfoLink infoLink ->
                return infoLink.isDefault
            }

            if (link == null) {
                throw AppErrors.INSTANCE.userEmailPrimaryEmailNotFound().exception()
            }

            return resourceContainer.userPersonalInfoResource.get(link.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo pii ->
                if (!pii.isValidated) {
                    throw AppErrors.INSTANCE.userPrimaryEmailNotVerified().exception()
                }

                return Promise.pure(user)
            }
        }
    }

    public Promise<String> getVerifiedUserPrimaryMail(User user) {
        if (CollectionUtils.isEmpty(user.emails)) {
            throw AppErrors.INSTANCE.userEmailNotFound().exception()
        }

        UserPersonalInfoLink link = user.emails.find { UserPersonalInfoLink infoLink ->
            return infoLink.isDefault
        }

        if (link == null) {
            throw AppErrors.INSTANCE.userEmailPrimaryEmailNotFound().exception()
        }

        return resourceContainer.userPersonalInfoResource.get(link.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo pii ->
            if (!pii.isValidated) {
                throw AppErrors.INSTANCE.userPrimaryEmailNotVerified().exception()
            }

            Email email = ObjectMapperProvider.instance().treeToValue(pii.value, Email)
            return Promise.pure(email.info)
        }
    }
}
