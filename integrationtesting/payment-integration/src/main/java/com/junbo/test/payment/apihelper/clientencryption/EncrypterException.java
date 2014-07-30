/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.apihelper.clientencryption;

/**
 * Created by weiyu_000 on 7/30/14.
 */
public class EncrypterException extends Exception {

    private static final long serialVersionUID = 2699577096011945290L;

    /**
     * Wrapping exception for all JCE encryption related exceptions.
     *
     * @param message
     * @param cause
     */
    public EncrypterException(String message, Throwable cause) {
        super(message, cause);
    }

}

