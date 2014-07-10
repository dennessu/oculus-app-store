/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.internal;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.core.impl.order.OrderServiceContext;

/**
 * Created by xmchen on 14-6-23.
 */
public interface RiskService {
    Promise<RiskReviewResult> reviewOrder(OrderServiceContext orderContext);

    Promise<Void> updateReview(OrderServiceContext orderContext);
}
