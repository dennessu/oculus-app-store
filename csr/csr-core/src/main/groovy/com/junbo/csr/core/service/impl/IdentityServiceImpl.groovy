package com.junbo.csr.core.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.core.service.IdentityService
import com.junbo.csr.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-17.
 */
@CompileStatic
class IdentityServiceImpl implements IdentityService {
    private UserPersonalInfoResource userPersonalInfoResource
    private UserResource userResource

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<List<User>> getUserByUserEmail(String userEmail) {
        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(email: userEmail)).then { Results<UserPersonalInfo> results ->
            if (results == null || results.items == null || results.items.isEmpty()) {
                throw AppErrors.INSTANCE.userNotFound().exception()
            }

            List<User> users = new ArrayList<>()
            return Promise.each(results.items) { UserPersonalInfo userPersonalInfo ->
                return userResource.get(userPersonalInfo.userId as UserId, new UserGetOptions()).then { User user ->
                    if (user != null) {
                        users.add(user)
                    }
                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(users)
            }
        }
    }

    @Override
    Promise<User> getUserByUsername(String username) {
        return userResource.list(new UserListOptions(username: username)).then { Results<User> userResults ->
            if (userResults == null || userResults.items == null || userResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.userNotFound().exception()
            }

            return Promise.pure(userResults.items.get(0))
        }
    }

    @Override
    Promise<User> getUserByVerifiedEmail(String userEmail) {
        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(email: userEmail.toLowerCase(Locale.ENGLISH), isValidated: true)).then { Results<UserPersonalInfo> results ->
            if (results == null || results.items == null || results.items.isEmpty()) {
                throw AppErrors.INSTANCE.userNotFound().exception()
            }

            return userResource.get(results.items.get(0).userId as UserId, new UserGetOptions()).then { User user ->
                return Promise.pure(user)
            }
        }
    }
}
