/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.exception;

/**
 * Exception indicates entity cannot be found.
 */
public class NotFoundException extends CatalogException{
    public NotFoundException(String message) {
        super(message);
    }
}
