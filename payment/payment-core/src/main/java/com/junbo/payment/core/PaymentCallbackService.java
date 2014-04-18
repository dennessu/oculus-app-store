/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core;

import com.junbo.payment.spec.enums.PropertyField;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * payment call back service.
 */
public interface PaymentCallbackService {
    @Transactional
    void addPaymentProperties(Long paymentId, Map<PropertyField, String> properties);
}
