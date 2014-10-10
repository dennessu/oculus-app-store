/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider;

import com.junbo.payment.spec.internal.ProviderCriteria;
import org.springframework.transaction.annotation.Transactional;

/**
 * provider routing service.
 */
public interface ProviderRoutingService {
    @Transactional(readOnly = true)
    PaymentProviderService getPaymentProvider(ProviderCriteria criteria);
    PaymentProviderService getProviderByName(String provider);
    @Transactional
    void updatePaymentConfiguration();
}
