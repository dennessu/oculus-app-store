/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.cloudant

import com.junbo.billing.db.repo.BalanceRepository
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.common.id.BalanceId
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite

/**
 * Created by haomin on 14-6-19.
 */
class BalanceRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<Balance, BalanceId> implements BalanceRepository {
    @Override
    Promise<List<Balance>> getByTrackingUuid(UUID trackingUuid) {
        return super.queryView('by_tracking_uuid', trackingUuid.toString())
    }

    @Override
    Promise<List<Balance>> getInitBalances() {
        return super.queryView('by_balance_status', BalanceStatus.INIT.toString());
    }

    @Override
    Promise<List<Balance>> getAwaitingPaymentBalances() {
        return super.queryView('by_balance_status', BalanceStatus.AWAITING_PAYMENT.toString());
    }

    @Override
    Promise<List<Balance>> getUnconfirmedBalances() {
        return super.queryView('by_balance_status', BalanceStatus.UNCONFIRMED.toString());
    }

    @Override
    Promise<List<Balance>> getRefundBalancesByOriginalId(Long balanceId) {
        return super.queryView('by_original_balance_id', balanceId.toString()).then { List<Balance> balances ->
            balances.retainAll { Balance balance ->
                balance.getType() == BalanceType.REFUND.toString() && (balance.getStatus() == BalanceStatus.COMPLETED.toString() || balance.getStatus() == BalanceStatus.AWAITING_PAYMENT.toString())
            }

            return Promise.pure(balances);
        }
    }
}
