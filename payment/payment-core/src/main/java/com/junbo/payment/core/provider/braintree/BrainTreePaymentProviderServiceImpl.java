/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.braintree;

import com.braintreegateway.*;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.exception.AppServerExceptions;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.InitializingBean;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * brain tree sdk implementation.
 */
public class BrainTreePaymentProviderServiceImpl implements PaymentProviderService, InitializingBean {
    private static final String PROVIDER_NAME = "BrainTree";
    private static final String CUSTOMER_ID = "junbo_inc_bt_customerId";
    private static BraintreeGateway gateway;
    private String environment;
    private String merchantId;
    private String publicKey;
    private String privateKey;

    public void afterPropertiesSet(){
        Environment env = null;
        try{
            env = Environment.valueOf(environment);
        }catch(Exception ex){
            //TODO: handle exception
        }
        gateway = new BraintreeGateway(env, merchantId, publicKey, privateKey);
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        /*//test use only
        request.setAccountNum("1111");
        request.setStatus(PIStatus.ACTIVE.toString());
        request.getCreditCardRequest().setExternalToken("123");
        request.getCreditCardRequest().setType("VISA");
        request.getCreditCardRequest().setCommercial("UNKNOW");
        //test use only
         */
        Calendar expireDate = new GregorianCalendar();
        expireDate.setTime(request.getCreditCardRequest().getExpireDate());
        CreditCardRequest ccRequest = new CreditCardRequest()
                .customerId(getCustomerId())
                .number(request.getAccountNum())
                .expirationMonth(String.valueOf(expireDate.get(Calendar.MONTH)))
                .expirationYear(String.valueOf(expireDate.get(Calendar.YEAR)))
                .cardholderName(request.getAccountName())
                .cvv(request.getCreditCardRequest().getEncryptedCvmCode())
                .options()
                    .failOnDuplicatePaymentMethod(false)
                    .verifyCard(request.getIsValidated())
                    .done();
        //Add billing Address
        if(request.getAddress() != null){
            ccRequest.billingAddress()
                    .postalCode(request.getAddress().getPostalCode())
                    .streetAddress(request.getAddress().getAddressLine1())
                    .locality(request.getAddress().getCity())
                    .region(request.getAddress().getState())
                    .countryCodeAlpha2(request.getAddress().getCountry())
                    .done();
        }
        Result<CreditCard> result = null;
        try {
            result = gateway.creditCard().create(ccRequest);
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, ex.getMessage()).exception();
        }
        if(result.isSuccess()){
            request.setAccountNum(result.getTarget().getLast4());
            request.getCreditCardRequest().setExternalToken(result.getTarget().getToken());
            request.getCreditCardRequest().setType(
                    PaymentUtil.getCreditCardType(result.getTarget().getCardType()).toString());
            request.getCreditCardRequest().setCommercial(result.getTarget().getCommercial().toString());
            request.getCreditCardRequest().setDebit(result.getTarget().getDebit().toString());
            request.getCreditCardRequest().setPrepaid(result.getTarget().getPrepaid().toString());
            request.getCreditCardRequest().setIssueCountry(result.getTarget().getCountryOfIssuance());
        }else{
            handleProviderError(result);
        }
        return Promise.pure(request);
    }

    @Override
    public Promise<Void> delete(String token) {
        Result<CreditCard> result = null;
        try{
            result = gateway.creditCard().delete(token);
        }catch(Exception ex){
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, ex.getMessage()).exception();
        }
        if(!result.isSuccess()){
            handleProviderError(result);
        }
        return null;
    }

    @Override
    public Promise<PaymentTransaction> authorize(String piToken, PaymentTransaction paymentRequest) {
        TransactionRequest request = getTransactionRequest(piToken, paymentRequest);
        Result<Transaction> result = null;
        try{
            result = gateway.transaction().sale(request);
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, ex.getMessage()).exception();
        }
        if(result.isSuccess()){
            paymentRequest.setExternalToken(result.getTarget().getId());
        }else{
            handleProviderError(result);
        }
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction request) {
        Result<Transaction> result = null;
        try{
            if(request.getChargeInfo() == null || request.getChargeInfo().getAmount() == null){
                result = gateway.transaction().submitForSettlement(transactionId);
            }else{
                //Partial Settle
                result = gateway.transaction().submitForSettlement(transactionId, request.getChargeInfo().getAmount());
            }
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, ex.getMessage()).exception();
        }
        if (result.isSuccess()) {
            // transaction successfully submitted for settlement
        } else {
            handleProviderError(result);
        }
        return Promise.pure(request);
    }

    @Override
    public Promise<PaymentTransaction> charge(String piToken, PaymentTransaction paymentRequest) {
        TransactionRequest request = getTransactionRequest(piToken, paymentRequest);
        request.options()
                .submitForSettlement(true)
                .done();
        Result<Transaction> result = null;
        try{
            result = gateway.transaction().sale(request);
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, ex.getMessage()).exception();
        }
        if(result.isSuccess()){
            paymentRequest.setExternalToken(result.getTarget().getId());
        }else{
            handleProviderError(result);
        }
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<Void> reverse(String transactionId) {
        Result<Transaction> result = null;
        try{
            result = gateway.transaction().voidTransaction(transactionId);
        }catch(Exception ex){
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, ex.getMessage()).exception();
        }
        if (result.isSuccess()) {
            // transaction successfully voided
        } else {
            handleProviderError(result);
        }
        return null;
    }

    private <T> void handleProviderError(Result<T> result) {
        StringBuffer sbErrorCodes = new StringBuffer();
        for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
            sbErrorCodes.append(error.getCode() + "_");
        }
        throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, sbErrorCodes.toString()).exception();
    }

    @Override
    public void refund(String transactionId, PaymentTransaction request) {

    }

    private TransactionRequest getTransactionRequest(String piToken, PaymentTransaction paymentRequest) {
        TransactionRequest request = new TransactionRequest()
                .amount(paymentRequest.getChargeInfo().getAmount())
                //use payment_id as order id
                .orderId(paymentRequest.getPaymentId().toString())
                .paymentMethodToken(piToken)
                .descriptor()
                    .name(paymentRequest.getChargeInfo().getBusinessDescriptor())
                    .done();
        if(paymentRequest.getMerchantAccount() != null && !paymentRequest.getMerchantAccount().isEmpty()){
            request.merchantAccountId(paymentRequest.getMerchantAccount());
        }
        return request;
    }

    private String getCustomerId(){
        Customer customer = null;
        try{
            customer = gateway.customer().find(CUSTOMER_ID);
        }catch(Exception ex){
            //Ignore Not Found exception
        }
        if(customer != null) {
            return CUSTOMER_ID;
        }
        CustomerRequest request = new CustomerRequest()
                .id(CUSTOMER_ID)
                .firstName("Junbo")
                .lastName("Zhang")
                .company("Junbo Inc.");
        Result<Customer> dummyCustomer = null;
        try{
            dummyCustomer = gateway.customer().create(request);
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, ex.getMessage()).exception();
        }
        if(dummyCustomer.isSuccess()){
            return CUSTOMER_ID;
        }else{
            handleProviderError(dummyCustomer);
        }
        throw AppServerExceptions.INSTANCE.missingRequiredField("customer_id").exception();
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
