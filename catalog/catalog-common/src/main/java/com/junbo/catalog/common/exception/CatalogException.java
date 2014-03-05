/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.exception;

/**
 * Base class for catalog service exception.
 */
public class CatalogException extends RuntimeException {
    public CatalogException(String message) {
        super(message);
    }
}
