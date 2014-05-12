/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.util;

/**
 * Constant.
 */
public final class Constant {
    public static final String SYSTEM_INTERNAL = "system internal";

    // constant number
    public static final int UNIQUE_RESULT = 0;

    // offer action properties
    public static final String ENTITLEMENT_DEF_ID = "ENTITLEMENT_DEF_ID";
    public static final String ITEM_ID = "ITEM_ID";
    public static final String STORED_VALUE_CURRENCY = "STORED_VALUE_CURRENCY";
    public static final String STORED_VALUE_AMOUNT = "STORED_VALUE_AMOUNT";
    public static final String USE_COUNT = "USE_COUNT";

    // event name
    public static final String EVENT_PURCHASE = "PURCHASE";

    // action name
    public static final String ACTION_GRANT_ENTITLEMENT = "GRANT_ENTITLEMENT";
    public static final String ACTION_DELIVER_PHYSICAL_GOODS = "DELIVER_PHYSICAL_GOODS";
    public static final String ACTION_CREDIT_WALLET = "CREDIT_WALLET";

    private Constant() {
    }
}
