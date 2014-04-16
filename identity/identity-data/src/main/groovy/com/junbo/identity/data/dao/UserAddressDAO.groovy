package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserAddressEntity
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/16/14.
 */
@CompileStatic
interface UserAddressDAO {
    UserAddressEntity save(UserAddressEntity entity)

    UserAddressEntity get(Long id)

    void delete(Long id)

    List<UserAddressEntity> search(Long userPiiId)
}
