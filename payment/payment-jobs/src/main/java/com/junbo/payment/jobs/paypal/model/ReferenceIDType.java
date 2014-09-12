/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.paypal.model;

import com.junbo.payment.common.CommonUtil;

/**
 * Paypal Reference ID Type.
 */
public enum ReferenceIDType {
    ODR, //OrderID
    TXN, //TransactionID
    SUB, //SubscriptionID
    PAP, //PreapprovedPaymentID
    NONE;//Not matched

    public static ReferenceIDType getReferenceIDType(String value){
        if(CommonUtil.isNullOrEmpty(value)){
            return ReferenceIDType.NONE;
        }
        return ReferenceIDType.valueOf(value);
    }
}
