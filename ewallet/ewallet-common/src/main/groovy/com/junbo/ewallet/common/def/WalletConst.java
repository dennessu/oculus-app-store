/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.common.def;

import java.util.Date;

/**
 * Wallet Consts.
 */
public class WalletConst {
    public static final int ACTIVE = 1;
    public static final int LOCKED = 2;
    public static final int EXPIRED = 3;

    private static final Long NEVER_EXPIRE_LONG = 253402271999000L;
    public static final Date NEVER_EXPIRE = new Date(NEVER_EXPIRE_LONG);
}
