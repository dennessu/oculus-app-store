/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.cloudant

import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.BillingHistoryRepository
import com.junbo.order.spec.model.BillingHistory
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class BillingHistoryRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<BillingHistory, Long> implements BillingHistoryRepository {

    @Override
    Promise<List<BillingHistory>> getByOrderId(Long orderId) {
        return super.queryView('by_order', orderId.toString());
    }
}
