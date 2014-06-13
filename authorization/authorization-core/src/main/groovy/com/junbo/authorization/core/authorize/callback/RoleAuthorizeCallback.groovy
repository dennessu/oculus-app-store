package com.junbo.authorization.core.authorize.callback

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.OwnerCallback
import com.junbo.authorization.spec.model.Role

import com.junbo.common.id.UniversalId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.RoleId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil

/**
 * Created by Zhanxin on 5/16/2014.
 */
class RoleAuthorizeCallback extends AbstractAuthorizeCallback<Role> {
    RoleAuthorizeCallback(AbstractAuthorizeCallbackFactory<Role> factory, Role entity) {
        super(factory, entity)
    }

    @Override
    String getApiName() {
        return 'roles'
    }

    @Override
    Boolean getOwnedByCurrentUser() {
        def currentUserId = AuthorizeContext.currentUserId
        if (currentUserId == null) {
            return false
        }

        Role entity = getEntity()

        if (entity != null && entity.target != null && entity.target.filterLink != null) {
            UniversalId filterLinkId = IdUtil.fromLink(entity.target.filterLink)

            if (filterLinkId != null) {
                OwnerCallback callback = (factory as RoleAuthorizeCallbackFactory).ownerCallbacks[filterLinkId.class]

                if (callback != null) {
                    UserId userOwnerId = callback.getUserOwnerId(filterLinkId)
                    if (userOwnerId != null) {
                        return userOwnerId == currentUserId
                    }

                    OrganizationId organizationOwnerId = callback.getOrganizationOwnerId(filterLinkId)
                    RoleId roleId = getRoleId(organizationOwnerId, 'admin')

                    if (roleId != null) {
                        return hasRoleAssignments(roleId, currentUserId, [])
                    }

                    return false
                }
            }
        }

        return false
    }
}
