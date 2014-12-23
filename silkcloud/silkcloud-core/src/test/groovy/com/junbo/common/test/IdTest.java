/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.test;

import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.IdFormatter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;

/**
 * Java doc for IdTest.
 */
public class IdTest {
    private static final long OCULUS_40_MAXIMUM = 0x7FFFFFFFFFL;
    private static final long OCULUS_48_MAXIMUM = 0xFFFFFFFFFFFFL;

    private Random rand = new Random();

    @Test
    public void testBVT() throws Exception {
        for(int i = 0; i< 1000; i++) {
            UserId userId = new UserId((rand.nextLong() & OCULUS_48_MAXIMUM));
            System.out.println("original UserId:" + userId.getValue());
            String encodedUserId = IdFormatter.encodeId(userId);
            System.out.println("encode UserId:" + encodedUserId);
            Long decodeUserId = (IdFormatter.decodeId(UserId.class, encodedUserId));
            System.out.println("decoded UserId:" + decodeUserId);
            Assert.assertEquals(userId.getValue(), decodeUserId);

            OrderId orderId = new OrderId((rand.nextLong() & OCULUS_40_MAXIMUM));
            System.out.println("original OrderId:" + orderId.getValue());
            String encodedOrderId = IdFormatter.encodeId(orderId);
            System.out.println("encode OrderId:" + encodedOrderId);
            Long decodeOrderId = (IdFormatter.decodeId(OrderId.class, encodedOrderId));
            System.out.println("decoded OrderId:" + decodeOrderId);
            Assert.assertEquals(orderId.getValue(), decodeOrderId);

        }
    }

    @Test
    public void decode() throws Exception {
        Long decodeUserId = (IdFormatter.decodeId(UserId.class, "6B54FDB4BC9F"));
        System.out.println("decoded UserId:" + decodeUserId);
    }

    @Test
    public void encode() throws Exception {
        UserId userId = new UserId(453345280L);
        String encodedUserId = IdFormatter.encodeId(userId);
        System.out.println("encode UserId:" + encodedUserId);
    }
}
