/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.catalog;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.clientproxy.model.OrderOfferRevision;

import java.util.Date;

/**
 * Interface for catalog facade.
 */
public interface CatalogFacade {

    Promise<OrderOfferRevision> getOfferRevision(Long offerId);

    Promise<OrderOfferRevision> getOfferRevision(Long offerId, Date honoredTime);
}
