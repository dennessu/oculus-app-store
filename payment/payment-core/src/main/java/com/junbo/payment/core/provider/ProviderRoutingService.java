/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider;

import com.junbo.common.id.PIType;
import org.springframework.transaction.annotation.Transactional;

/**
 * provider routing service.
 */
public interface ProviderRoutingService {
    @Transactional(readOnly = true)
    PaymentProviderService getPaymentProvider(PIType piType);
    PaymentProviderService getProviderByName(String provider);
    @Transactional
    void updatePaymentConfiguration();
}
