/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.catalog;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.clientproxy.model.OrderOffer;

import java.util.Date;

/**
 * Interface for catalog facade.
 */
public interface CatalogFacade {

    Promise<OrderOffer> getOffer(Long offerId);

    Promise<OrderOffer> getOffer(Long offerId, Date honoredTime);
}
