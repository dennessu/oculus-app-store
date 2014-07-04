package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
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
    Promise<List<UserPersonalInfoIdToUserIdLink>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<UserPersonalInfoIdToUserIdLink> searchByUserPersonalInfoId(UserPersonalInfoId userPersonalInfoId) {
        return queryView('by_user_personal_info_id', userPersonalInfoId.toString()).then { List<UserPersonalInfoIdToUserIdLink> list ->
            if (CollectionUtils.isEmpty(list)) {
                return Promise.pure(null)
            }

            return Promise.pure((UserPersonalInfoIdToUserIdLink)list.get(0))
        }
    }

    @Override
    Promise<UserPersonalInfoIdToUserIdLink> create(UserPersonalInfoIdToUserIdLink model) {
        return cloudantPost(model)
    }

    @Override
    Promise<UserPersonalInfoIdToUserIdLink> update(UserPersonalInfoIdToUserIdLink model) {
        return cloudantPut(model)
    }

    @Override
    Promise<UserPersonalInfoIdToUserIdLink> get(UserPersonalInfoIdToUserIdLinkId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserPersonalInfoIdToUserIdLinkId id) {
        return cloudantDelete(id.toString())
    }
}
