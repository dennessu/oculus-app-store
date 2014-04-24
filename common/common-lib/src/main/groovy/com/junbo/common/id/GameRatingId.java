/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * GameRatingId.
 */
@IdResourcePath("/game-ratings/{0}")
public class GameRatingId extends Id {

    public GameRatingId() {}
    public GameRatingId(long value) {
        super(value);
    }
}
