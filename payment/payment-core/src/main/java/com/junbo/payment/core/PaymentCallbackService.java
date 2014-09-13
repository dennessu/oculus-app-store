/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core;

import com.junbo.langur.core.promise.Promise;
import org.springframework.transaction.annotation.Transactional;

/**
 * payment call back service.
 */
public interface PaymentCallbackService {
    @Transactional
    Promise<Void> addPaymentProperties(String request);
}
