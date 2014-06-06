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
public class WalletLotId extends Id {
    public WalletLotId() {
    }

    public WalletLotId(Long value) {
        super(value);
    }
}
