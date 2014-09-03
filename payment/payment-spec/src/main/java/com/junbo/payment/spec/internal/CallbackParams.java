/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.internal;

/**
 * Callback Params.
 */
public interface CallbackParams {
    String getProvider();
    Long getPaymentId();
}
