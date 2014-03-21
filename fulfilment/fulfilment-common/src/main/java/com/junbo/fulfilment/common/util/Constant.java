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

    // entitlement definition
    public static final String ENTITLEMENT_DEF_ID = "ENTITLEMENT_DEF_ID";
    public static final String ENTITLEMENT_TAG = "ENTITLEMENT_TAG";
    public static final String ENTITLEMENT_GROUP = "ENTITLEMENT_GROUP";
    public static final String ENTITLEMENT_TYPE = "ENTITLEMENT_TYPE";
    public static final String ENTITLEMENT_DEVELOPER = "ENTITLEMENT_DEVELOPER";

    // event name
    public static final String EVENT_PURCHASE = "PURCHASE";

    // action name
    public static final String ACTION_GRANT_ENTITLEMENT = "GRANT_ENTITLEMENT";
    public static final String ACTION_DELIVER_PHYSICAL_GOODS = "DELIVER_PHYSICAL_GOODS";
    public static final String ACTION_CREDIT_WALLET = "CREDIT_WALLET";

    private Constant() {
    }
}
