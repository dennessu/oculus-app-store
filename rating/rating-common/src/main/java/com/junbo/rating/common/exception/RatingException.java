/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.common.exception;

/**
 * Created by lizwu on 2/7/14.
 */
public class RatingException extends RuntimeException{
    public RatingException() {
    }

    public RatingException(String message) {
        super(message);
    }
}
