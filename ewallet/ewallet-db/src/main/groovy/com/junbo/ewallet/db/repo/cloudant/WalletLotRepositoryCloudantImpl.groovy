package com.junbo.ewallet.db.repo.cloudant

import com.junbo.ewallet.db.repo.WalletLotRepository
import com.junbo.ewallet.spec.model.WalletLot
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic

/**
 * Created by minhao on 6/23/14.
 */
@CompileStatic
class WalletLotRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<WalletLot, Long> implements WalletLotRepository {
    @Override
    Promise<List<WalletLot>> getValidLot(Long walletId) {
        return super.queryView('by_wallet_id', walletId.toString()).then { List<WalletLot> list ->
            Date now = new Date()
            list.retainAll { WalletLot walletLot ->
                return walletLot.remainingAmount != 0 && walletLot.expirationDate > now
            }

            return Promise.pure(list)
        }
    }

    @Override
    Promise<BigDecimal> getValidAmount(Long walletId) {
        return super.queryView('by_wallet_id', walletId.toString()).then { List<WalletLot> list ->
            BigDecimal v = BigDecimal.ZERO
            Date now = new Date()
            list.each { WalletLot walletLot ->
                if (walletLot.expirationDate > now && walletLot.remainingAmount != 0) {
                    v = v.add(walletLot.remainingAmount)
                }
            }

            return Promise.pure(v)
        }
    }
}
