/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.*;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by chriszhu on 2/18/14.
 */
public interface FulfillmentHistoryRepository extends BaseRepository<FulfillmentHistory, Long> {

    @ReadMethod
    Promise<List<FulfillmentHistory>> getByOrderItemId(Long orderItemId);
}
