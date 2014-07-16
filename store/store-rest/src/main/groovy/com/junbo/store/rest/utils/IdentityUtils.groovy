package com.junbo.store.rest.utils

import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.PhoneNumber
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
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

    Promise<UserPersonalInfo> createEmailInfoIfNotExist(UserId userId, UserPersonalInfo emailInfo) {
        Email updateEmail = ObjectMapperProvider.instance().treeToValue(emailInfo.value, Email)
        return resourceContainer.userPersonalInfoResource.list(new UserPersonalInfoListOptions(type: 'EMAIL', email: updateEmail.info)).then { Results<UserPersonalInfo> results ->
            UserPersonalInfo result = results.items.find {UserPersonalInfo e -> e.userId == userId}
            if (result == null) {
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(userId: userId, type: 'EMAIL', value: emailInfo.value)
                )
            } else {
                return Promise.pure(result)
            }
        }
    }

    Promise<UserPersonalInfo> createPhoneInfoIfNotExist(UserId userId, UserPersonalInfo phoneInfo) {
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
        }
    }


}
