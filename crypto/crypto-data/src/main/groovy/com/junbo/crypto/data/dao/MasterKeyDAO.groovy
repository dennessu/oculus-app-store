package com.junbo.crypto.data.dao

import com.junbo.crypto.data.entity.MasterKeyEntity
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
public interface MasterKeyDAO {
    MasterKeyEntity get(Long id)
    MasterKeyEntity create(MasterKeyEntity masterKeyEntity)

    List<MasterKeyEntity> getAll()
}