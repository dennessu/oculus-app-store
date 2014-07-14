package com.junbo.ewallet.db.repo.cloudant

import com.junbo.ewallet.db.repo.WalletRepository
import com.junbo.ewallet.spec.def.WalletType
import com.junbo.ewallet.spec.model.Wallet
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic

/**
 * Created by minhao on 6/23/14.
 */
@CompileStatic
class WalletRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<Wallet, Long> implements WalletRepository {
    @Override
    Promise<Wallet> getByTrackingUuid(Long shardMasterId, UUID uuid) {
        return super.queryView('by_tracking_uuid', uuid.toString()).then { List<Wallet> list ->
            if (list.size() > 0) {
                return Promise.pure(list.get(0))
            }
            else {
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Wallet> get(Long userId, WalletType type, String currency) {
        return super.queryView('by_user_id', userId.toString()).then { List<Wallet> list ->
            list.retainAll { Wallet wallet ->
                return wallet.type == type.toString() && wallet.currency == currency
            }

            if (list.size() > 0) {
                return Promise.pure(list.get(0))
            }
            else {
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<List<Wallet>> getAll(Long userId) {
        return super.queryView('by_user_id', userId.toString())
    }
}
