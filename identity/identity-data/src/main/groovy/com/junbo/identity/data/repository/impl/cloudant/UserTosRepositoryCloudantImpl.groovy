package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.TosId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosAgreementId
import com.junbo.identity.data.repository.UserTosRepository
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by minhao on 4/13/14.
 */
@CompileStatic
class UserTosRepositoryCloudantImpl extends CloudantClient<UserTosAgreement> implements UserTosRepository {

    @Override
    Promise<UserTosAgreement> create(UserTosAgreement entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<UserTosAgreement> update(UserTosAgreement entity) {
        return cloudantPut(entity)
    }

    @Override
    Promise<UserTosAgreement> get(UserTosAgreementId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserTosAgreement>> search(UserTosAgreementListOptions getOption) {
        return queryView('by_user_id', getOption.userId.value.toString(),
                getOption.limit, getOption.offset, false).then { List<UserTosAgreement> list ->
            if (getOption.tosId != null) {
                list.retainAll { UserTosAgreement agreement ->
                    agreement.tosId == getOption.tosId
                }
            }
            return Promise.pure(list)
        }
    }

    @Override
    Promise<List<UserTosAgreement>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserTosAgreement>> searchByTosId(TosId tosId, Integer limit, Integer offset) {
        return queryView('by_tos_id', tosId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserTosAgreement>> searchByUserIdAndTosId(UserId userId, TosId tosId, Integer limit, Integer offset) {
        return queryView('by_user_id_tos_id', "${userId.toString()}:${tosId.toString()}", limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserTosAgreementId id) {
        return cloudantDelete(id.toString())
    }
}
