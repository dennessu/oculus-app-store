/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core;

import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * FulfilmentService.
 */
@Transactional
public interface FulfilmentService {
    FulfilmentRequest fulfill(FulfilmentRequest request);

    FulfilmentRequest revoke(FulfilmentRequest request);

    FulfilmentRequest retrieveRequest(Long requestId);

    FulfilmentRequest retrieveRequestByOrderId(Long billingOrderId);

    FulfilmentItem retrieveFulfilmentItem(Long fulfilmentId);
}
