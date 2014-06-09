package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserPersonalInfoIdToUserIdLinkId
import com.junbo.identity.data.repository.UserPersonalInfoIdToUserIdLinkRepository
import com.junbo.identity.spec.v1.model.UserPersonalInfoIdToUserIdLink
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.util.CollectionUtils
/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
class UserPersonalInfoIdToUserIdLinkRepositoryImpl extends CloudantClient<UserPersonalInfoIdToUserIdLink>
        implements UserPersonalInfoIdToUserIdLinkRepository {

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<UserPersonalInfoIdToUserIdLink>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return super.queryView('by_user_id', userId.value.toString(), limit, offset, false)
    }

    @Override
    Promise<UserPersonalInfoIdToUserIdLink> searchByUserPersonalInfoId(UserPersonalInfoId userPersonalInfoId) {
        return super.queryView('by_user_personal_info_id', userPersonalInfoId.value.toString()).then { List<UserPersonalInfoIdToUserIdLink> list ->
            if (CollectionUtils.isEmpty(list)) {
                return Promise.pure(null)
            }

            return Promise.pure((UserPersonalInfoIdToUserIdLink)list.get(0))
        }
    }

    @Override
    Promise<UserPersonalInfoIdToUserIdLink> create(UserPersonalInfoIdToUserIdLink model) {

        if (model.id == null) {
            model.id = new UserPersonalInfoIdToUserIdLinkId(model.userPersonalInfoId.value)
        }
        return super.cloudantPost(model)
    }

    @Override
    Promise<UserPersonalInfoIdToUserIdLink> update(UserPersonalInfoIdToUserIdLink model) {
        if (model.id == null) {
            model.id = new UserPersonalInfoIdToUserIdLinkId(model.userPersonalInfoId.value)
        }
        return super.cloudantPut(model)
    }

    @Override
    Promise<UserPersonalInfoIdToUserIdLink> get(UserPersonalInfoIdToUserIdLinkId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserPersonalInfoIdToUserIdLinkId id) {
        return super.cloudantDelete(id.toString())
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_personal_info_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userPersonalInfoId, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )
}
