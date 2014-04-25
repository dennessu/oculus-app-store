/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath("/clients/{0}")
public class ClientId extends Id {

    public ClientId() {}

    public ClientId(long value) {
        super(value);
    }
}

