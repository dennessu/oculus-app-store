package com.junbo.authorization.core.authorize.callback

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.spec.model.Role

/**
 * Created by Zhanxin on 5/16/2014.
 */
class RoleAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Role> {
    @Override
    AuthorizeCallback<Role> create(String apiName, Role entity) {
        return new RoleAuthorizeCallback(this, apiName, entity)
    }
}
