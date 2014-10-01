/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import com.junbo.common.id.Id;
import com.junbo.common.id.CloudantId;
import com.junbo.common.id.OrderId;
import com.junbo.common.shuffle.Oculus40Id;
import com.junbo.common.shuffle.Oculus48Id;

/**
 * Created by liangfu on 3/7/14.
 */
public class IdFormatter {
    private IdFormatter() {
    }

    public static Long decodeId(Class cls, String formattedId) {
        if(cls == OrderId.class) {
            Oculus40Id.validateEncodedValue(formattedId);
            Long value = Oculus40Id.deFormat(formattedId);
            return Oculus40Id.unShuffle(value);
        }
        else if(Id.class.isAssignableFrom(cls)){
            Oculus48Id.validateEncodedValue(formattedId);
            Long value = Oculus48Id.deFormat(formattedId);
            return Oculus48Id.unShuffle(value);
        }
        else {
            throw new RuntimeException("unsupported class " + cls);
        }
    }

    public static String encodeId(Id id) {
        if(id instanceof OrderId) {
            Oculus40Id.validateRawValue(id.getValue());
            Long value = Oculus40Id.shuffle(id.getValue());
            return Oculus40Id.format(value);
        }
        else {
            Oculus48Id.validateRawValue(id.getValue());
            Long value = Oculus48Id.shuffle(id.getValue());
            return Oculus48Id.format(value);
        }
    }

    public static String encodeId(CloudantId id) {
        return id.getValue();
    }
}
