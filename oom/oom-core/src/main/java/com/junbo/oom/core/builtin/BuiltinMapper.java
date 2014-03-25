/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core.builtin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Java doc.
 */
@org.springframework.stereotype.Component
public class BuiltinMapper {

    public Long fromShortToLong(Short source) {
        if (source == null) {
            return null;
        }

        return source.longValue();
    }

    public Short fromLongToShort(Long source) {
        if (source == null) {
            return null;
        }

        return source.shortValue();
    }

    public String fromStringToString(String source) {
        return source;
    }

    public Long fromLongToLong(Long source) {
        return source;
    }

    public Date fromDateToDate(Date source) {
        return source;
    }

    public BigDecimal fromBigDecimalToBigDecimal(BigDecimal source) {
        return source;
    }

    public Integer fromIntegerToInteger(Integer i) {
        return i;
    }

    public Double fromDoubleToDouble(Double d) {
        return d;
    }

    public Boolean fromBooleanToBoolean(Boolean source) {
        return source;
    }

    public UUID fromUUIDToUUID(UUID source) {
        return source;
    }
}
