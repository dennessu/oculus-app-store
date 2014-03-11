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
import com.junbo.payment.spec.enums.PaymentStatus;

/**
 * payment related utility.
 */
public final class PaymentUtil {
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

    public static PaymentStatus getPaymentStatus(String status){
        try{
            return PaymentStatus.valueOf(status);
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.invalidPaymentStatus(status).exception();
        }
    }

    public static PaymentStatus mapPaymentStatus(PaymentStatus.BrainTreeStatus brainTreeStatus){
        switch (brainTreeStatus){
            case FAILED:
            case GATEWAY_REJECTED:
            case PROCESSOR_DECLINED:
                return PaymentStatus.AUTH_DECLINED;
            case SUBMITTED_FOR_SETTLEMENT:
                return PaymentStatus.SETTLEMENT_SUBMITTED;
            case VOIDED:
                return PaymentStatus.REVERSED;
            default:
            {
                try{
                    return PaymentStatus.valueOf(brainTreeStatus.toString());
                }catch(Exception ex){
                    return PaymentStatus.UNRECOGNIZED;
                }
            }
        }
    }
}
