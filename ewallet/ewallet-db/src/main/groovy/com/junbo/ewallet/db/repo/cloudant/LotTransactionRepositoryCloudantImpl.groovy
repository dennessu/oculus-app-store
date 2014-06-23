package com.junbo.ewallet.db.repo.cloudant

import com.junbo.ewallet.db.repo.LotTransactionRepository
import com.junbo.ewallet.spec.model.LotTransaction
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite

/**
 * Created by minhao on 6/23/14.
 */
class LotTransactionRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<LotTransaction, Long> implements LotTransactionRepository {
    @Override
    Promise<List<LotTransaction>> getByTransactionId(Long transactionId) {
        return null
    }
}
