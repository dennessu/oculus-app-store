/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.paypal.model;

import com.junbo.payment.common.CommonUtil;

/**
 * paypal Transaction Type.
 */
public enum TransactionType {
    CR,
    DR,
    NONE;

    public static TransactionType getTransactionType(String value){
        if(CommonUtil.isNullOrEmpty(value)){
            return TransactionType.NONE;
        }
        return TransactionType.valueOf(value);
    }
}
