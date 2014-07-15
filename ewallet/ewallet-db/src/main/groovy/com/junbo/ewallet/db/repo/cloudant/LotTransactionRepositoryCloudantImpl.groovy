package com.junbo.ewallet.db.repo.cloudant

import com.junbo.ewallet.db.repo.LotTransactionRepository
import com.junbo.ewallet.spec.model.LotTransaction
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic

/**
 * Created by minhao on 6/23/14.
 */
@CompileStatic
class LotTransactionRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<LotTransaction, Long> implements LotTransactionRepository {
    @Override
    Promise<List<LotTransaction>> getByTransactionId(Long transactionId) {
        return super.queryView('by_transaction_id', transactionId.toString()).then { List<LotTransaction> list ->
            list.removeAll { LotTransaction lotTransaction ->
                return lotTransaction.unrefundedAmount == 0
            }

            return Promise.pure(list);
        }
    }
}
