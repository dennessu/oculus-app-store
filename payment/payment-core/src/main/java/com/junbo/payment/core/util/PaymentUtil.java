/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.util;


import com.junbo.payment.core.exception.AppClientExceptions;
import com.junbo.payment.core.exception.AppServerExceptions;
import com.junbo.payment.spec.enums.CreditCardType;
import com.junbo.payment.spec.enums.PIType;

/**
 * payment related utility.
 */
public class PaymentUtil {
    private PaymentUtil(){

    }

    public static PIType getPIType(String piType){
        try{
            return PIType.valueOf(piType);
        }catch (Exception ex){
            throw AppClientExceptions.INSTANCE.invalidPIType(piType).exception();
        }
    }

    public static CreditCardType getCreditCardType(String ccType){
        try{
            return CreditCardType.valueOf(ccType);
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.invalidCreditCardType(ccType).exception();
        }
    }
}
