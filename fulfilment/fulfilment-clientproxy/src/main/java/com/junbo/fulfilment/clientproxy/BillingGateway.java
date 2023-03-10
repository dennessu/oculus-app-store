/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.fulfilment.spec.fusion.ShippingAddress;

/**
 * BillingGateway.
 */
public interface BillingGateway {
    ShippingAddress getShippingAddress(Long userId, Long shippingAddressId);
}
