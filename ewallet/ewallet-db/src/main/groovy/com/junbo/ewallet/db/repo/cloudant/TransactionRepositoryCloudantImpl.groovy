package com.junbo.ewallet.db.repo.cloudant

import com.junbo.ewallet.db.repo.TransactionRepository
import com.junbo.ewallet.spec.model.Transaction
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic

/**
 * Created by minhao on 6/23/14.
 */
@CompileStatic
class TransactionRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<Transaction, Long> implements TransactionRepository {
    @Override
    Promise<List<Transaction>> getByWalletId(Long walletId) {
        return super.queryView('by_wallet_id', walletId.toString())
    }

    @Override
    Promise<Transaction> getByTrackingUuid(Long shardMasterId, UUID uuid) {
        return super.queryView('by_tracking_uuid', uuid.toString()).then { List<Transaction> list ->
            if (list.size() > 0) {
                return Promise.pure(list.get(0))
            }
            else {
                return Promise.pure(null)
            }
        }
    }
}
