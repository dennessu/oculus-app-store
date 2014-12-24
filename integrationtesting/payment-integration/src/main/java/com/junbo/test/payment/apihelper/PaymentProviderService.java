/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.apihelper;

/**
 * Created by Cloud on 14-12-23.
 */
public interface PaymentProviderService {
    String getToken(String cardNumber, String csc) throws Exception;

    String getToken(String cardNumber, String csc, int expectedResponseCode) throws Exception;
}
