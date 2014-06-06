package com.junbo.authorization.core.authorize.callback

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.OwnerCallback
import com.junbo.authorization.spec.model.Role
import com.junbo.common.id.Id
import org.springframework.beans.factory.annotation.Required

/**
 * Created by Zhanxin on 5/16/2014.
 */
class RoleAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Role> {

    private Map<Class<? extends Id>, OwnerCallback> ownerCallbacks = [:]

    @Required
    void setOwnerCallbacks(Map<Class<? extends Id>, OwnerCallback> ownerCallbacks) {
        this.ownerCallbacks = ownerCallbacks
    }

    Map<Class<? extends Id>, OwnerCallback> getOwnerCallbacks() {
        return ownerCallbacks
    }

    @Override
    AuthorizeCallback<Role> create(Role entity) {
        return new RoleAuthorizeCallback(this, entity)
    }
}
