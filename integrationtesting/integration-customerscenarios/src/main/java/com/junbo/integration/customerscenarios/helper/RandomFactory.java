/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.integration.customerscenarios.helper;

import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Created by Jason on 3/7/14.
 * @author Jason
 * 3/7/14
 */
public final class RandomFactory {

    private static final String TEST_NAMESPACE_NAME = "user";
    private static final Random RANDOMINT = new Random();

    private RandomFactory() {
    }

    public static String getRandomEmailAddress() {
        int rand1 = RANDOMINT.nextInt(100000);
        int rand2 = RANDOMINT.nextInt(100000);
        String email = rand1 + TEST_NAMESPACE_NAME + GregorianCalendar.getInstance().getTimeInMillis()
                + rand2 + "@ecommerce.com";
        return email;
    }

}
