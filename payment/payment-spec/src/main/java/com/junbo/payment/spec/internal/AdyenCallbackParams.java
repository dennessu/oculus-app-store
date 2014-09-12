/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.internal;

import com.junbo.payment.common.CommonUtil;

/**
 * Adyen Callback Params.
 */
public class AdyenCallbackParams implements CallbackParams{
    public static final String provider = "Adyen";
    private String authResult;
    private String pspReference;
    private String merchantReference;
    private String skinCode;
    private String merchantReturnData;
    private String merchantSig;

    public String getAuthResult() {
        return authResult;
    }

    public void setAuthResult(String authResult) {
        this.authResult = authResult;
    }

    public String getPspReference() {
        return pspReference;
    }

    public void setPspReference(String pspReference) {
        this.pspReference = pspReference;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

    public String getSkinCode() {
        return skinCode;
    }

    public void setSkinCode(String skinCode) {
        this.skinCode = skinCode;
    }

    public String getMerchantReturnData() {
        return merchantReturnData;
    }

    public void setMerchantReturnData(String merchantReturnData) {
        this.merchantReturnData = merchantReturnData;
    }

    public String getMerchantSig() {
        return merchantSig;
    }

    public void setMerchantSig(String merchantSig) {
        this.merchantSig = merchantSig;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public Long getPaymentId() {
        return CommonUtil.decode(merchantReference);
    }
}
