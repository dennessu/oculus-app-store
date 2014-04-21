/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.NotSupportedException;

/**
 * Enum registry test.
 */
public class EnumRegistryTest {
    @Test
    public void testResolve() {
        FulfilmentStatus status = EnumRegistry.resolve(100, FulfilmentStatus.class);
        Assert.assertEquals(status, FulfilmentStatus.PENDING);

        status = EnumRegistry.resolve(200, FulfilmentStatus.class);
        Assert.assertEquals(status, FulfilmentStatus.SUCCEED);

        status = EnumRegistry.resolve(-999, FulfilmentStatus.class);
        Assert.assertEquals(status, FulfilmentStatus.FAILED);

        status = EnumRegistry.resolve(77777777, FulfilmentStatus.class);
        Assert.assertEquals(status, null);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(EnumRegistry.getId(FulfilmentStatus.PENDING), 100);
        Assert.assertEquals(EnumRegistry.getId(FulfilmentStatus.SUCCEED), 200);
    }

    @Test
    public void testResolveNotExistId() {
        Assert.assertNull(EnumRegistry.resolve(77777777, FulfilmentStatus.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNotIdentifiableEnumResolve() {
        EnumRegistry.resolve(100, NotIdentifiableEnum.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNotIdentifiableEnumGetId() {
        EnumRegistry.getId(NotIdentifiableEnum.PENDING);
    }
}

enum FulfilmentStatus implements Identifiable<Integer> {
    PENDING(100),
    SUCCEED(200),
    FAILED(-999);

    private Integer id;

    FulfilmentStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        throw new NotSupportedException("enum FulfilmentStatus not settable");
    }
}

enum NotIdentifiableEnum {
    PENDING(100),
    SUCCEED(200),
    FAILED(-999);

    private Integer id;

    NotIdentifiableEnum(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
