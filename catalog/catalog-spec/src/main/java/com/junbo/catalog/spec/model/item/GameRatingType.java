/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Game rating type.
 */
public class GameRatingType {
    public static final String OCULUS = "OCULUS";
    public static final String ESRB = "ESRB";
    public static final String GRB = "GRB";
    public static final String CERO = "CERO";
    public static final Set<String> ALL_TYPES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(OCULUS, ESRB, GRB, CERO)));

    private GameRatingType() {}
}
