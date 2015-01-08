/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.serializer;

import com.junbo.common.shuffle.Oculus40Id;

/**
 * OrderIdSerializer.
 */
public class OrderIdSerializer extends ResourceIdSerializer {
    @Override
    protected String encode(Object value) {
        return Oculus40Id.encode((Long) value);
    }
}
