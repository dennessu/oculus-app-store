/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.util;


import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
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
            return PIType.valueOf(piType.toUpperCase());
        }catch (Exception ex){
            throw AppClientExceptions.INSTANCE.invalidPIType(piType).exception();
        }
    }

    public static CreditCardType getCreditCardType(String ccType){
        try{
            return CreditCardType.valueOf(ccType.toUpperCase());
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.invalidCreditCardType(ccType).exception();
        }
    }

    public static PaymentStatus getPaymentStatus(String status){
        try{
            return PaymentStatus.valueOf(status.toUpperCase());
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.invalidPaymentStatus(status).exception();
        }
    }

    private enum PayPalStatus{
        PaymentActionNotInitiated,
        PaymentActionFailed,
        PaymentActionInProgress,
        PaymentCompleted,
        PaymentActionCompleted
    }

    public static PaymentStatus mapBraintreePaymentStatus(PaymentStatus.BrainTreeStatus brainTreeStatus){
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
                    return PaymentStatus.valueOf(brainTreeStatus.toString().toUpperCase());
                }catch(Exception ex){
                    return PaymentStatus.UNRECOGNIZED;
                }
            }
        }
    }

    public static PaymentStatus mapPayPalPaymentStatus(String checkoutStatus){
        if(checkoutStatus.equalsIgnoreCase(PayPalStatus.PaymentActionNotInitiated.toString())){
            return PaymentStatus.UNCONFIRMED;
        }else if(checkoutStatus.equalsIgnoreCase(PayPalStatus.PaymentActionCompleted.toString())
                || checkoutStatus.equalsIgnoreCase(PayPalStatus.PaymentCompleted.toString())){
            return PaymentStatus.SETTLED;
        }else if(checkoutStatus.equalsIgnoreCase(PayPalStatus.PaymentActionInProgress.toString())){
            return PaymentStatus.SETTLING;
        }else if(checkoutStatus.equalsIgnoreCase(PayPalStatus.PaymentActionFailed.toString())){
            return PaymentStatus.SETTLE_DECLINED;
        }else{
            return PaymentStatus.UNRECOGNIZED;
        }
    }
}
