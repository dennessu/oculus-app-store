package com.junbo.crypto.data.repo.impl.sql


import com.junbo.crypto.data.dao.MasterKeyDAO
import com.junbo.crypto.data.entity.MasterKeyEntity
import com.junbo.crypto.data.repo.MasterKeyRepo
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 6/11/14.
 */
@CompileStatic
class SqlMasterKeyRepoImpl implements MasterKeyRepo {
    private MasterKeyDAO masterKeyDAO

    @Override
    Promise<List<MasterKey>> getAllMaterKeys() {
        List<MasterKeyEntity> entities = masterKeyDAO.getAll()
        if (CollectionUtils.isEmpty(entities)) {
            return Promise.pure(null)
        }
        List<MasterKey> masterKeyList = new ArrayList<>()
        entities.each { MasterKeyEntity entity ->
            masterKeyList.add(wrap(entity))
        }
        return Promise.pure(masterKeyList)
    }

    @Override
    Promise<MasterKey> get(Long id) {
        MasterKeyEntity entity = masterKeyDAO.get(id)
        return Promise.pure(wrap(entity))
    }

    @Override
    Promise<MasterKey> create(MasterKey model) {
        MasterKeyEntity entity = unwrap(model)
        entity = masterKeyDAO.create(entity)
        return get(entity.keyVersion)
    }

    @Override
    Promise<MasterKey> update(MasterKey model, MasterKey oldModel) {
        throw new IllegalStateException('unsupported operation')
    }

    @Override
    Promise<Void> delete(Long id) {
        throw new IllegalStateException('unsupported operation')
    }

    @Required
    void setMasterKeyDAO(MasterKeyDAO masterKeyDAO) {
        this.masterKeyDAO = masterKeyDAO
    }

    private MasterKey wrap(MasterKeyEntity entity) {
        if (entity == null) {
            return null
        }

        return new MasterKey(
                encryptValue: entity.encryptValue,
                keyVersion: entity.keyVersion,
                createdBy: entity.createdBy,
                createdTime: entity.createdTime
        )
    }

    private MasterKeyEntity unwrap(MasterKey masterKey) {
        if (masterKey == null) {
            return null
        }

        return new MasterKeyEntity(
                encryptValue: masterKey.encryptValue,
                keyVersion: masterKey.keyVersion,
                createdBy: masterKey.createdBy,
                createdTime: masterKey.createdTime
        )
    }
}
