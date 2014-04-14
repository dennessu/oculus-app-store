package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserPiiEntity
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
interface UserPiiDAO {
    UserPiiEntity save(UserPiiEntity entity)

    UserPiiEntity update(UserPiiEntity entity)

    UserPiiEntity get(Long id)

    void delete(Long id)

    UserPiiEntity findByUserId(Long userId)
}