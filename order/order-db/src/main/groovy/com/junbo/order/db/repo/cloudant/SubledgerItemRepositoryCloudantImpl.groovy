/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant

import com.junbo.common.id.SubledgerItemId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.SubledgerItemRepository
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class SubledgerItemRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<SubledgerItem, SubledgerItemId> implements SubledgerItemRepository {

    @Override
    Promise<List<SubledgerItem>> getByStatus(Integer dataCenterId, Object shardKey, String status, PageParam pageParam) {
        throw new RuntimeException("SubledgerItemRepository::getByStatus is only available in SQL mode for backend jobs.");
    }
}
