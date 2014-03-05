/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service;

import com.junbo.billing.spec.model.Currency;

import java.util.List;

/**
 * Created by xmchen on 14-2-13.
 */
public interface CurrencyService {

    Currency getCurrencyByName(String name);

    List<Currency> getCurrencies();

    Boolean exists(String name);
}
