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
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
class CloudantMasterKeyRepoImpl extends CloudantClient<MasterKey> implements MasterKeyRepo {
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
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
    Promise<MasterKey> getMasterKeyByVersion(Integer version) {
        def list = super.queryView('by_key_version', version.toString(),
                Integer.MAX_VALUE, 0, false)

        if (CollectionUtils.isEmpty(list)) {
            return Promise.pure(null)
        }
        return Promise.pure((MasterKey)list.get(0))
    }

    @Override
    Promise<MasterKey> update(MasterKey model) {
        throw new IllegalStateException('Update operation isn\'t supported')
    }

    @Override
    Promise<MasterKey> get(MasterKeyId id) {
        return Promise.pure((MasterKey)super.cloudantGet(id.toString()))
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_key_version': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.keyVersion.toString(), doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

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