/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Java doc for PreorderId.
 */
@IdResourcePath("/pre-orders/{0}")
public class PreorderId extends Id {
    public PreorderId() {}

    public PreorderId(long value) {
        super(value);
    }
}