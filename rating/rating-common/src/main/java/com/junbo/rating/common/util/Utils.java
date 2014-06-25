/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.common.util;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by lizwu on 2/6/14.
 */
public class Utils {
    private Utils() {
    }

    public static Date now() {
        return new Date();
    }

    public static Date maxDate() {
        return new Date(253373469595309L);
    }

    public static Date minDate() {
        return new Date(0);
    }

    public static Long parseDateTime(String time) {
        return DatatypeConverter.parseDateTime(time).getTimeInMillis();
    }

    public static BigDecimal rounding(BigDecimal value, int fractionDigits) {
        return value.setScale(fractionDigits, Constants.ROUNDING_MODE);
    }
}
