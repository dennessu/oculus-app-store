/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by liangfu on 3/7/14.
 */
@IdResourcePath(value = "/wallets/{0}",
                resourceType = "wallets",
                regex = "/wallets/(?<id>[0-9A-Za-z]+)")
public class WalletId extends Id {

    public WalletId() {}
    public WalletId(long value) {
        super(value);
    }
}
