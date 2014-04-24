/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * GameRatingId.
 */
@IdResourcePath("/age-ratings/{0}")
public class AgeRatingId extends Id {

    public AgeRatingId() {}
    public AgeRatingId(long value) {
        super(value);
    }
}
