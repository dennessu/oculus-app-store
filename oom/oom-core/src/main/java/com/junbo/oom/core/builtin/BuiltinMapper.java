/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core.builtin;

import java.math.BigDecimal;
import java.util.Date;

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


    public Long fromShortToLong(Short source, Short alternativeSource) {
        if (source == null) {

            if (alternativeSource == null) {
                return null;
            }

            return alternativeSource.longValue();
        }

        return source.longValue();
    }

    public Short fromLongToShort(Long source, Long alternativeSource) {
        if (source == null) {

            if (alternativeSource == null) {
                return null;
            }

            return alternativeSource.shortValue();
        }

        return source.shortValue();
    }

    public String fromStringToString(String source, String alternativeSource) {
        return source == null ? alternativeSource : source;
    }

    public Long fromLongToLong(Long source, Long alternativeSource) {
        return source == null ? alternativeSource : source;
    }

    public Date fromDateToDate(Date source, Date alternativeSource) {
        return source == null ? alternativeSource : source;
    }

    public BigDecimal fromBigDecimalToBigDecimal(BigDecimal source, BigDecimal alternativeSource) {
        return source == null ? alternativeSource : source;
    }

    public Integer fromIntegerToInteger(Integer i, Integer i1) {
        return i == null ? i1 : i;
    }

    public Double fromDoubleToDouble(Double d, Double d1) {
        return d == null ? d1 : d;
    }

    public Boolean fromBooleanToBoolean(Boolean source, Boolean alternativeSource) {
        return source == null ? alternativeSource : source;
    }
}
