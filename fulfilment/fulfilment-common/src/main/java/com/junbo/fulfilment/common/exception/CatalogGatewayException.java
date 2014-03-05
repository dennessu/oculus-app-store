/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.exception;

/**
 * CatalogGatewayException.
 */
public class CatalogGatewayException extends FulfilmentException {
    public CatalogGatewayException(String message) {
        super(message);
    }

    public CatalogGatewayException(String message, Exception ex) {
        super(message, ex);
    }
}
