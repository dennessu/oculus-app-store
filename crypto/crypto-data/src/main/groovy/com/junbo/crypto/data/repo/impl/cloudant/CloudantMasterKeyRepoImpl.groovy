package com.junbo.crypto.data.repo.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.MasterKeyId
import com.junbo.crypto.data.repo.MasterKeyRepo
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
class CloudantMasterKeyRepoImpl extends CloudantClient<MasterKey> implements MasterKeyRepo {
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<List<MasterKey>> getAllMaterKeys() {
        return Promise.pure(super.cloudantGetAll())
    }

    @Override
    Promise<MasterKey> create(MasterKey model) {
        if (model.id == null) {
            model.id = new MasterKeyId(idGenerator.nextId())
        }

        return Promise.pure((MasterKey)super.cloudantPost(model))
    }

    @Override
    Promise<MasterKey> update(MasterKey model) {
        throw new IllegalStateException('Update operation isn\'t supported')
    }

    @Override
    Promise<MasterKey> get(MasterKeyId id) {
        return Promise.pure((MasterKey)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(MasterKeyId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
