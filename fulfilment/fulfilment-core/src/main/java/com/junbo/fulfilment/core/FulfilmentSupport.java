/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core;

import com.junbo.fulfilment.core.service.ClassifyResult;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;

/**
 * FulfilmentSupport.
 */
public interface FulfilmentSupport {
    void validate(FulfilmentRequest request);

    void distill(FulfilmentRequest request);

    void store(FulfilmentRequest request);

    ClassifyResult classify(FulfilmentRequest request);

    void dispatch(FulfilmentRequest request, ClassifyResult classifyResult);

    void dispatchRevoke(FulfilmentRequest request, ClassifyResult classifyResult);
}
