package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.AddressId
import com.junbo.identity.data.repository.AddressRepository
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-21.
 */
class AddressRepositoryCloudantImpl extends CloudantClient<Address> implements AddressRepository {
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
        return null
    }

    @Override
    Promise<Address> create(Address model) {
        if (model.id == null) {
            model.id = new AddressId(idGenerator.nextId(model.userId.value))
        }
        return Promise.pure((Address)super.cloudantPost(model))
    }

    @Override
    Promise<Address> update(Address model) {
        throw new IllegalStateException('update address not support')
    }

    @Override
    Promise<Address> get(AddressId id) {
        return Promise.pure((Address)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(AddressId id) {
        throw new IllegalStateException('delete address not support')
    }
}
