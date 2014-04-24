/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by xmchen on 14-2-20.
 */
@IdResourcePath("/balances/{0}")
public class BalanceId extends Id {
    public BalanceId() {}

    public BalanceId(Long value) {
        super(value);
    }
}
