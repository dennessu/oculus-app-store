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
    Promise<WalletLot> update(WalletLot entity, WalletLot oldEntity) {
        Date now = new Date()
        if (entity.remainingAmount.compareTo(BigDecimal.ZERO) <= 0 || entity.expirationDate.compareTo(now) < 0) {
            entity.isValid = Boolean.FALSE
        } else {
            entity.isValid = Boolean.TRUE
        }
        return super.update(entity, oldEntity)
    }

    @Override
    Promise<List<WalletLot>> getValidLot(Long walletId) {
        return super.queryView('by_wallet_id_and_is_valid', walletId.toString() + ":true")
    }

    @Override
    Promise<BigDecimal> getValidAmount(Long walletId) {
        return super.queryView('by_wallet_id_and_is_valid', walletId.toString() + ":true").then { List<WalletLot> list ->
            BigDecimal v = BigDecimal.ZERO
            list.each { WalletLot walletLot ->
                v = v.add(walletLot.remainingAmount)
            }

            return Promise.pure(v)
        }
    }
}
