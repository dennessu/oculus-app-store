/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.deserializer;

import com.junbo.common.shuffle.Oculus40Id;

/**
 * OrderIdDeserializer.
 */
public class OrderIdDeserializer extends ResourceIdDeserializer {
    @Override
    protected Long decode(String id) {
        return Oculus40Id.unShuffle(Oculus40Id.deFormat(id));
    }
}
