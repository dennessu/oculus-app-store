package com.junbo.authorization.core.authorize.callback

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.OwnerCallback
import com.junbo.authorization.spec.model.Role
import com.junbo.common.id.RoleId
import com.junbo.common.id.UniversalId
import org.springframework.beans.factory.annotation.Required

/**
 * Created by Zhanxin on 5/16/2014.
 */
class RoleAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Role> {

    private Map<Class<? extends UniversalId>, OwnerCallback> ownerCallbacks = [:]

    @Required
    void setOwnerCallbacks(Map<Class<? extends UniversalId>, OwnerCallback> ownerCallbacks) {
        this.ownerCallbacks = ownerCallbacks
    }

    Map<Class<? extends UniversalId>, OwnerCallback> getOwnerCallbacks() {
        return ownerCallbacks
    }

    @Override
    AuthorizeCallback<Role> create(Role entity) {
        return new RoleAuthorizeCallback(this, entity)
    }

    AuthorizeCallback<Role> create(RoleId roleId) {
        return create(roleResource.get(roleId).get())
    }
}
