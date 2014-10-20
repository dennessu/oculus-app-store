/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.order;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Order;
import com.junbo.store.spec.model.ApiContext;

import java.util.List;

/**
 * The OrderFacade class.
 */
public interface OrderFacade {

    Promise<Order> freePurchaseOrder(UserId userId, List<OfferId> offerIdList, ApiContext apiContext);

    Promise<Order> createTentativeOrder(UserId userId, List<OfferId> offerIdList, CurrencyId currencyId, PaymentInstrumentId paymentInstrumentId, ApiContext apiContext);
}
