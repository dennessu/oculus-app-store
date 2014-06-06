/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.entity.def;

import com.junbo.common.id.Id;

/**
 * for cloudant.
 */
public class WalletId extends Id {
    public WalletId() {
    }

    public WalletId(Long value) {
        super(value);
    }
}
