/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy;

import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by xmchen on 14-6-4.
 */
public interface CurrencyFacade {
    Promise<Currency> getCurrency(String currencyId);
}
