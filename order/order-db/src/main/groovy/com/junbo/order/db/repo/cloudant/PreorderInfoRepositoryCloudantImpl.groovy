/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant

import com.junbo.common.id.PreorderId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.PreorderInfoRepository
import com.junbo.order.spec.model.PreorderInfo
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class PreorderInfoRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<PreorderInfo, PreorderId> implements PreorderInfoRepository {

    @Override
    Promise<List<PreorderInfo>> getByOrderItemId(Long orderItemId) {
        return super.queryView('by_order_item', orderItemId.toString());
    }
}
