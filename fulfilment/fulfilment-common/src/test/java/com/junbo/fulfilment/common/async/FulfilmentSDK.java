/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.async;

/**
 * FulfilmentSDK.
 */
public class FulfilmentSDK {
    public static void ship(String shippingInfo, int sleep) {
        hung(sleep);

        System.out.println("shipping...");
    }

    public static Long query(String shippingId, int sleep) {
        hung(sleep);

        System.out.println("querying...");
        return 123456L;
    }

    private static void hung(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
