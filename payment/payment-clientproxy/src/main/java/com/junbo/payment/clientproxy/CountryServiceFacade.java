/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.langur.core.promise.Promise;


/**
 * country service facade.
 */
public interface CountryServiceFacade {
    Promise<CurrencyId> getDefaultCurrency(String country);
}
