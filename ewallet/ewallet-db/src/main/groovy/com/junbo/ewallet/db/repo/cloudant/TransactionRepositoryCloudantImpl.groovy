package com.junbo.ewallet.db.repo.cloudant

import com.junbo.ewallet.db.repo.TransactionRepository
import com.junbo.ewallet.spec.model.Transaction
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite

/**
 * Created by minhao on 6/23/14.
 */
class TransactionRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<Transaction, Long> implements TransactionRepository {
    @Override
    Promise<List<Transaction>> getByWalletId(Long walletId) {
        return null
    }

    @Override
    Promise<Transaction> getByTrackingUuid(Long shardMasterId, UUID uuid) {
        return null
    }
}
