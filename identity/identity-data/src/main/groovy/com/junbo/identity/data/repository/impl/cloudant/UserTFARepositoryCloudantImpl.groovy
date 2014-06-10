package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.data.repository.UserTFARepository
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFARepositoryCloudantImpl extends CloudantClient<UserTFA> implements UserTFARepository  {
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<UserTFA>> searchTFACodeByUserIdAndPersonalInfoId(UserId userId, UserPersonalInfoId personalInfo,
                                                               Integer limit, Integer offset) {
        return super.queryView('by_user_id_personal_info',
                "${userId.toString()}:${personalInfo.toString()}", limit, offset, false)
    }

    @Override
    Promise<UserTFA> create(UserTFA entity) {
        if (entity.id == null) {
            entity.id = new UserTFAId(idGenerator.nextId(entity.userId.value))
        }
        return super.cloudantPost(entity)
    }

    @Override
    Promise<UserTFA> update(UserTFA entity) {
        return super.cloudantPut(entity)
    }

    @Override
    Promise<UserTFA> get(UserTFAId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserTFAId id) {
        return super.cloudantDelete(id.toString())
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                'by_user_id_personal_info': new CloudantViews.CloudantView(
                    map: 'function(doc) {' +
                            '  emit(doc.userId + \':\' + doc.personalInfo, doc._id)' +
                            '}',
                    resultClass: String)
            ]
    )

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}