package com.junbo.authorization.core.authorize.callback

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.spec.model.Role
import com.junbo.common.id.GroupId
import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.option.model.GroupGetOptions

/**
 * Created by Zhanxin on 5/16/2014.
 */
class RoleAuthorizeCallback extends AbstractAuthorizeCallback<Role> {
    RoleAuthorizeCallback(AbstractAuthorizeCallbackFactory<Role> factory, String apiName, Role entity) {
        super(factory, apiName, entity)
    }

    @Override
    boolean ownedByCurrentUser() {
        def currentUserId = AuthorizeContext.currentUserId
        if (currentUserId == null) {
            return false
        }

        Role entity = getEntity()

        if (entity != null && entity.target != null && entity.target.filterLink != null) {
            Id filterLinkId = IdUtil.fromHref(entity.target.filterLink.href)

            if (filterLinkId instanceof UserId) {
                return filterLinkId as UserId == currentUserId
            }

            if (filterLinkId instanceof GroupId) {
                Group group = factory.groupResource.get(filterLinkId as GroupId, new GroupGetOptions()).wrapped().get()

                if (group == null) {
                    return false
                }

                return group.ownerUserId == currentUserId
            }
        }

        return false
    }
}
