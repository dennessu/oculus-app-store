/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.internal;

import com.junbo.payment.common.CommonUtil;

import java.util.Map;

/**
 * Callback Params Factory.
 */
public final class CallbackParamsFactory {
    public static CallbackParams getCallbackParams(Map<String, String> notifies){
        if(notifies.containsKey("provider")){
            String providerName = notifies.get("provider");
            if(providerName.equalsIgnoreCase(AdyenCallbackParams.provider)){
                return CommonUtil.parseJson(CommonUtil.toJson(notifies, null), AdyenCallbackParams.class);
            }else if(providerName.equalsIgnoreCase(PayPalCallbackParams.provider)){
                return CommonUtil.parseJson(CommonUtil.toJson(notifies, null), PayPalCallbackParams.class);
            }
            return null;
        }
        return null;
    }
}
