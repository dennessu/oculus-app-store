/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider;

import com.junbo.payment.spec.enums.PIType;

/**
 * provider routing service.
 */
public interface ProviderRoutingService {
    PaymentProviderService getPaymentProvider(PIType piType);
    void updatePaymentConfiguration();
}
