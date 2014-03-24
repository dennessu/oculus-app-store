/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.clientproxy.model

import com.junbo.catalog.spec.model.offer.Offer
import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 3/21/14.
 */
@CompileStatic
class OrderOffer {
    Offer catalogOffer
    List<OrderOfferItem> orderOfferItems
}
