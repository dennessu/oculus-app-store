/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.cloudant

import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.ItemSnapshotRepository
import com.junbo.order.spec.model.ItemSnapshot
import com.junbo.order.spec.model.OfferSnapshot
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by LinYi on 2014/8/28.
 */
@CompileStatic
@TypeChecked
class ItemSnapshotRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<ItemSnapshot, Long> implements ItemSnapshotRepository {
    @Override
    Promise<List<OfferSnapshot>> getByOfferSnapshotId(Long offerSnapshotId) {
        return super.queryView('by_snapshot', offerSnapshotId.toString())
    }
}
