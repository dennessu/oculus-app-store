package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/25/14.
 */
@CompileStatic
class UserPersonalInfoRepositoryCloudantImpl extends CloudantClient<UserPersonalInfo>
        implements UserPersonalInfoRepository {

    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<UserPersonalInfo> create(UserPersonalInfo model) {
        if (model.id == null) {
            model.id = new UserPersonalInfoId(idGenerator.nextId())
        }
        return Promise.pure((UserPersonalInfo)super.cloudantPost(model))
    }

    @Override
    Promise<UserPersonalInfo> update(UserPersonalInfo model) {
        return Promise.pure((UserPersonalInfo)super.cloudantPut(model))
    }

    @Override
    Promise<UserPersonalInfo> get(UserPersonalInfoId id) {
        return Promise.pure((UserPersonalInfo)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(UserPersonalInfoId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
