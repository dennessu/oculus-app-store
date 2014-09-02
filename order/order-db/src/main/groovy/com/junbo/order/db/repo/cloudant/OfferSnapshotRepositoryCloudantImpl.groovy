/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.cloudant

import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.OfferSnapshotRepository
import com.junbo.order.spec.model.OfferSnapshot
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by LinYi on 2014/8/28.
 */
@CompileStatic
@TypeChecked
class OfferSnapshotRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<OfferSnapshot, Long> implements OfferSnapshotRepository {
    @Override
    Promise<List<OfferSnapshot>> getByOrderId(Long orderId) {
        return super.queryView('by_order', orderId.toString())
    }
}
