/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.payment;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentInstrument;

/**
 * Created by chriszhu on 2/11/14.
 */
public interface PaymentFacade {
    Promise<PaymentInstrument> getPaymentInstrument(Long paymentInstrumentId);
}
