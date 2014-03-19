/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.offer.Offer;

/**
 * MegaGateway.
 */
public interface MegaGateway {
    Long createOffer(Offer offer);

    Long updateOffer(Offer offer);
}
