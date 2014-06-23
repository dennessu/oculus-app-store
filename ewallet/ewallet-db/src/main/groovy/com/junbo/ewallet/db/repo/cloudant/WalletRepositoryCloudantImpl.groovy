package com.junbo.ewallet.db.repo.cloudant

import com.junbo.ewallet.db.repo.WalletRepository
import com.junbo.ewallet.spec.def.WalletType
import com.junbo.ewallet.spec.model.Wallet
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite

/**
 * Created by minhao on 6/23/14.
 */
class WalletRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<Wallet, Long> implements WalletRepository {
    @Override
    Promise<Wallet> getByTrackingUuid(Long shardMasterId, UUID uuid) {
        return null
    }

    @Override
    Promise<Wallet> get(Long userId, WalletType type, String currency) {
        return null
    }

    @Override
    Promise<List<Wallet>> getAll(Long userId) {
        return null
    }
}
