package com.junbo.ewallet.db.repo.cloudant

import com.junbo.ewallet.db.repo.WalletLotRepository
import com.junbo.ewallet.spec.model.WalletLot
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite

/**
 * Created by minhao on 6/23/14.
 */
class WalletLotRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<WalletLot, Long> implements WalletLotRepository {
    @Override
    Promise<List<WalletLot>> getValidLot(Long walletId) {
        return null
    }

    @Override
    Promise<BigDecimal> getValidAmount(Long walletId) {
        return null
    }
}
