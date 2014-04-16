/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath("/tos/{0}")
public class TosId extends Id {

    public TosId() {}
    public TosId(long value) {
        super(value);
    }
}
