package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.role.RoleEntity
import groovy.transform.CompileStatic

/**
 * RoleDAO.
 */
@CompileStatic
interface RoleDAO {
    RoleEntity create(RoleEntity entity)

    RoleEntity update(RoleEntity entity)

    RoleEntity get(Long roleId)

    RoleEntity findByRoleName(String roleName, String resourceType, Long resourceId, String subResourceType)
}