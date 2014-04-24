/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath("/currencies/{0}")
public class CurrencyId extends Id {

    public CurrencyId() {}

    public CurrencyId(long value) {
        super(value);
    }
}

