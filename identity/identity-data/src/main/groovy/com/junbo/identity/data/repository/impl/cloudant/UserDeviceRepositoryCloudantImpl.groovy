package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserDeviceId
import com.junbo.identity.data.repository.UserDeviceRepository
import com.junbo.identity.spec.v1.model.UserDevice
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-11.
 */
@CompileStatic
class UserDeviceRepositoryCloudantImpl extends CloudantClient<UserDevice> implements UserDeviceRepository {
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }


    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<UserDevice> create(UserDevice entity) {
        if (entity.id == null) {
            entity.id = new UserDeviceId(idGenerator.nextId(entity.userId.value))
        }

        return Promise.pure((UserDevice)super.cloudantPost(entity))
    }

    @Override
    Promise<UserDevice> update(UserDevice entity) {
        return Promise.pure((UserDevice)super.cloudantPut(entity))
    }

    @Override
    Promise<UserDevice> get(UserDeviceId id) {
        return Promise.pure((UserDevice)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserDevice>> search(UserDeviceListOptions getOption) {
        def result = []
        if (getOption.userId != null) {
            if (getOption.deviceId != null) {
                result = super.queryView('by_user_id_device_id',
                        "${getOption.userId.value}:${getOption.deviceId.value}",
                        getOption.limit, getOption.offset, false)
            }
            else {
                result = super.queryView('by_user_id', getOption.userId.toString(),
                        getOption.limit, getOption.offset, false)
            }
        }
        else if (getOption.deviceId != null) {
            result = super.queryView('by_device_id', getOption.deviceId.toString(),
                    getOption.limit, getOption.offset, false)
        }
        return Promise.pure(result)
    }

    @Override
    Promise<Void> delete(UserDeviceId id) {
        super.cloudantDelete(id.value)
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_device_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.deviceId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_device_id': new CloudantViews.CloudantView(
                        map: 'function(doc) {' +
                            '  emit(doc.userId.value.toString() + \':\' + doc.deviceId.value.toString(), doc._id)' +
                            '}',
                        resultClass: String)
            ]
    )
}
