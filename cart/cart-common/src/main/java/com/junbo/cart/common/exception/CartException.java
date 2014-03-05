/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.common.exception;

/**
 * Created by fzhang@wan-san.com on 14-1-23.
 */
public class CartException extends RuntimeException {

    private static final long serialVersionUID = 1547506918207479472L;

    public CartException(String message) {
        super(message);
    }

    public CartException(String message, Throwable cause) {
        super(message, cause);
    }

}
