/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.adyen;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.model.WebPaymentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * adyen provider service implementation.
 */
public class AdyenProviderServiceImpl extends AbstractPaymentProviderService implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenProviderServiceImpl.class);
    private static final String PROVIDER_NAME = "Adyen";
    private static final int SHIP_DELAY = 1;
    private static final int VALID_HOURS = 3;
    private String redirectURL;
    private String merchantAccount;
    private String skinCode;
    private String skinSecret;
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void clonePIResult(PaymentInstrument source, PaymentInstrument target) {

    }

    @Override
    public void cloneTransactionResult(PaymentTransaction source, PaymentTransaction target) {
        if(!CommonUtil.isNullOrEmpty(source.getStatus())){
            target.setStatus(source.getStatus());
        }
        target.setExternalToken(source.getExternalToken());
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        return Promise.pure(request);
    }

    @Override
    public Promise<Response> delete(PaymentInstrument pi) {
        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentInstrument> getByInstrumentToken(String token) {
        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("authorize").exception();
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("authorize").exception();
    }

    @Override
    public Promise<PaymentTransaction> charge(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        StringBuffer strRequest = new StringBuffer();
        StringBuffer strToSign = new StringBuffer();
        strToSign.append(paymentRequest.getChargeInfo().getAmount());
        strRequest.append("paymentAmount=" + paymentRequest.getChargeInfo().getAmount());
        strToSign.append(paymentRequest.getChargeInfo().getCurrency());
        strRequest.append("&currencyCode=" + paymentRequest.getChargeInfo().getCurrency());
        //shipBeforeDate:
        Calendar calShip = Calendar.getInstance();
        calShip.add(Calendar.DAY_OF_MONTH, SHIP_DELAY);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        strToSign.append(format.format(calShip.getTime()));
        strRequest.append("&shipBeforeDate=" + format.format(calShip.getTime()));
        //merchantReference
        strToSign.append(CommonUtil.encode(paymentRequest.getId()));
        strRequest.append("&merchantReference=" + CommonUtil.encode(paymentRequest.getId()));
        //skinCode
        strToSign.append(skinCode);
        strRequest.append("&skinCode=" + skinCode);
        //merchantAccount
        strToSign.append(merchantAccount);
        strRequest.append("&merchantAccount=" + merchantAccount);
        //sessionValidity
        Calendar calValid = Calendar.getInstance();
        calValid.add(Calendar.HOUR_OF_DAY, VALID_HOURS);
        ISO8601DateFormat format2 = new ISO8601DateFormat();
        strToSign.append(format2.format(calValid.getTime()));
        strRequest.append("&sessionValidity=" + format2.format(calValid.getTime()));
        //"shopperReference"
        strToSign.append(nullToEmpty(paymentRequest.getUserId().toString()));
        strRequest.append("&shopperReference=" + nullToEmpty(paymentRequest.getUserId().toString()));
        String merchantSig = CommonUtil.calHMCASHA1(strToSign.toString(), skinSecret);
        strRequest.append("&merchantSig=" + merchantSig);
        paymentRequest.setWebPaymentInfo(new WebPaymentInfo());
        paymentRequest.getWebPaymentInfo().setRedirectURL(redirectURL + "?" + strRequest.toString());
        paymentRequest.setStatus(PaymentStatus.UNCONFIRMED.toString());
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> confirm(final String transactionId, final PaymentTransaction paymentRequest){
        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("authorize").exception();
    }

    @Override
    public Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("authorize").exception();
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("getByBillingRefId").exception();
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(PaymentTransaction request) {
        return Promise.pure(null);
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getMerchantAccount() {
        return merchantAccount;
    }

    public void setMerchantAccount(String merchantAccount) {
        this.merchantAccount = merchantAccount;
    }

    public String getSkinCode() {
        return skinCode;
    }

    public void setSkinCode(String skinCode) {
        this.skinCode = skinCode;
    }

    private String nullToEmpty(String value){
        return value == null ? "" : value;
    }

    public String getSkinSecret() {
        return skinSecret;
    }

    public void setSkinSecret(String skinSecret) {
        this.skinSecret = skinSecret;
    }
}
