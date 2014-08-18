/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import com.junbo.payment.common.CommonUtil;

/**
 * payment call back property.
 */
public class PaymentCallbackParams {
    //paypal
    private String token;
    private String payerID;
    //adyen
    private String authResult;
    private String pspReference;
    private String merchantReference;
    private String skinCode;
    private String merchantReturnData;
    private String merchantSig;

    public String getPayerID() {
        return payerID;
    }

    public void setPayerID(String payerID) {
        this.payerID = payerID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPspReference() {
        return pspReference;
    }

    public void setPspReference(String pspReference) {
        this.pspReference = pspReference;
    }

    public String getAuthResult() {
        return authResult;
    }

    public void setAuthResult(String authResult) {
        this.authResult = authResult;
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

    public void urlDecode(){
        token = CommonUtil.urlDecode(token);
        payerID = CommonUtil.urlDecode(payerID);
        authResult = CommonUtil.urlDecode(authResult);
        pspReference = CommonUtil.urlDecode(pspReference);
        merchantReference = CommonUtil.urlDecode(merchantReference);
        skinCode = CommonUtil.urlDecode(skinCode);
        merchantReturnData = CommonUtil.urlDecode(merchantReturnData);
        merchantSig = CommonUtil.urlDecode(merchantSig);
    }

}
