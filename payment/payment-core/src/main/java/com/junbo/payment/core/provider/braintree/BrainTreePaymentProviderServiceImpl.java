/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.braintree;

import com.braintreegateway.*;
import com.braintreegateway.exceptions.DownForMaintenanceException;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.ws.rs.core.Response;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


/**
 * brain tree sdk implementation.
 */
public class BrainTreePaymentProviderServiceImpl implements PaymentProviderService, InitializingBean {
    private static final String PROVIDER_NAME = "BrainTree";
    private static final Logger LOGGER = LoggerFactory.getLogger(BrainTreePaymentProviderServiceImpl.class);
    private static BraintreeGateway gateway;
    private String environment;
    private String merchantId;
    private String publicKey;
    private String privateKey;
    private String companyName;

    public void afterPropertiesSet(){
        Environment env = null;
        try{
            env = Environment.valueOf(environment);
        }catch(Exception ex){
            LOGGER.error("not able to get the right environment:" + environment);
            throw AppServerExceptions.INSTANCE.invalidProviderRequest(PROVIDER_NAME).exception();
        }
        gateway = new BraintreeGateway(env, merchantId, publicKey, privateKey);
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        String expireDate = request.getCreditCardRequest().getExpireDate();
        String[] tokens = expireDate.split("-");
        if(tokens == null || tokens.length < 2){
            throw AppClientExceptions.INSTANCE.invalidExpireDateFormat(expireDate).exception();
        }
        CreditCardRequest ccRequest = new CreditCardRequest()
                .customerId(getOrCreateCustomerId(request.getId().getUserId().toString()))
                .number(request.getAccountNum())
                .expirationMonth(String.valueOf(tokens[1]))
                .expirationYear(String.valueOf(tokens[0]))
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
        LOGGER.info("add credit card for customer:" + request.getId().getUserId().toString());
        try {
            result = gateway.creditCard().create(ccRequest);
        }catch (Exception ex){
            handleProviderException(ex, "Add", "User", request.getId().getUserId().toString());
        }
        if(result.isSuccess()){
            request.setAccountNum(result.getTarget().getMaskedNumber());
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
    public Promise<Response> delete(String token) {
        Result<CreditCard> result = null;
        LOGGER.info("delete credit card :" + token);
        try{
            result = gateway.creditCard().delete(token);
        }catch(Exception ex){
            handleProviderException(ex, "Delete", "token", token);
        }
        if(!result.isSuccess()){
            handleProviderError(result);
        }
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<PaymentTransaction> authorize(String piToken, PaymentTransaction paymentRequest) {
        TransactionRequest request = getTransactionRequest(piToken, paymentRequest);
        Result<Transaction> result = null;
        LOGGER.info("authorize credit card :" + piToken);
        try{
            result = gateway.transaction().sale(request);
        }catch (Exception ex){
            handleProviderException(ex, "Authorize", "order", paymentRequest.getId().toString());
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
        LOGGER.info("capture transaction :" + transactionId);
        try{
            if(request.getChargeInfo() == null || request.getChargeInfo().getAmount() == null){
                result = gateway.transaction().submitForSettlement(transactionId);
            }else{
                //Partial Settle
                result = gateway.transaction().submitForSettlement(transactionId, request.getChargeInfo().getAmount());
            }
        }catch (Exception ex){
            handleProviderException(ex, "Capture", "transaction", transactionId);
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
        LOGGER.info("charge credit card :" + piToken);
        try{
            result = gateway.transaction().sale(request);
        }catch (Exception ex){
            handleProviderException(ex, "Charge", "order", paymentRequest.getId().toString());
        }
        if(result.isSuccess()){
            paymentRequest.setExternalToken(result.getTarget().getId());
        }else{
            handleProviderError(result);
        }
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction paymentRequest) {
        Result<Transaction> result = null;
        LOGGER.info("reverse transaction :" + transactionId);
        try{
            result = gateway.transaction().voidTransaction(transactionId);
        }catch(Exception ex){
            handleProviderException(ex, "Void", "transaction", transactionId);
        }
        if (result.isSuccess()) {
            // transaction successfully voided
        } else {
            handleProviderError(result);
        }
        return Promise.pure(paymentRequest);
    }

    private <T> void handleProviderError(Result<T> result) {
        StringBuffer sbErrorCodes = new StringBuffer();
        for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
            LOGGER.error("gateway validations errors message: " + error.getMessage());
            sbErrorCodes.append(error.getCode()).append("&");
        }
        LOGGER.error("gateway validations errors with codes: " + sbErrorCodes.toString());
        throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, sbErrorCodes.toString()).exception();
    }

    private void handleProviderException(Exception ex, String action, String source, String sourceValue){
        if(ex instanceof DownForMaintenanceException){
            LOGGER.error("gateway internal timeout exception: " + ex.toString() +
            ".Provider:" + PROVIDER_NAME + " take action:" + action + " for:" + source + "of " + sourceValue);
            throw AppServerExceptions.INSTANCE.providerGatewayTimeout(PROVIDER_NAME).exception();
        }else if(ex instanceof SocketTimeoutException){
            LOGGER.error("provider:" + PROVIDER_NAME + " gateway timeout exception: " + ex.toString());
            throw AppServerExceptions.INSTANCE.providerGatewayTimeout(PROVIDER_NAME).exception();
        }else{
            LOGGER.error("provider:" + PROVIDER_NAME + " gateway exception: " + ex.toString());
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, ex.toString()).exception();
        }
    }

    @Override
    public Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request) {
        return Promise.pure(request);
    }

    @Override
    public List<PaymentTransaction> getByOrderId(String orderId) {
        ResourceCollection<Transaction> collection = null;
        try{
            TransactionSearchRequest request = new TransactionSearchRequest()
                    .orderId().is(orderId);
            collection = gateway.transaction().search(request);
        }catch(Exception ex){
            handleProviderException(ex, "Search", "order", orderId);
        }
        if(collection == null || collection.getMaximumSize() == 0){
            return null;
        }
        List<PaymentTransaction> results = new ArrayList<PaymentTransaction>();
        for(Transaction transaction : collection){
            PaymentTransaction result = new PaymentTransaction();
            result.setStatus(PaymentUtil.mapPaymentStatus(PaymentStatus.BrainTreeStatus.valueOf(
                    transaction.getStatus().toString())).toString());
            //TODO: need add transaction.getSettlementBatchId(); for the batch job processing
            results.add(result);
        }
        return results;
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(String token) {
        Transaction transaction = null;
        try{
            transaction = gateway.transaction().find(token);
        }catch(Exception ex){
            handleProviderException(ex, "Search", "token", token);
        }
        if(transaction == null){
            return null;
        }
        PaymentTransaction result = new PaymentTransaction();
        result.setStatus(PaymentUtil.mapPaymentStatus(PaymentStatus.BrainTreeStatus.valueOf(
                transaction.getStatus().toString())).toString());
        //TODO: need add transaction.getSettlementBatchId(); for the batch job processing
        return Promise.pure(result);
    }

    private TransactionRequest getTransactionRequest(String piToken, PaymentTransaction paymentRequest) {
        TransactionRequest request = new TransactionRequest()
                .amount(paymentRequest.getChargeInfo().getAmount())
                //use payment_id as order id
                .orderId(paymentRequest.getBillingRefId())
                .paymentMethodToken(piToken)
                .descriptor()
                    .name(paymentRequest.getChargeInfo().getBusinessDescriptor())
                    .done();
        if(paymentRequest.getMerchantAccount() != null && !paymentRequest.getMerchantAccount().isEmpty()){
            request.merchantAccountId(paymentRequest.getMerchantAccount());
        }
        return request;
    }

    private String getOrCreateCustomerId(String customerId){
        Customer customer = null;
        try{
            customer = gateway.customer().find(customerId);
        }catch(Exception ex){
            //Ignore Not Found exception
        }
        if(customer != null) {
            return customerId;
        }
        CustomerRequest request = new CustomerRequest()
                .id(customerId)
                .company(companyName);
        Result<Customer> dummyCustomer = null;
        try{
            dummyCustomer = gateway.customer().create(request);
        }catch (Exception ex){
            LOGGER.error("gateway exception: " + ex.getMessage() + " while create customer:" + customerId);
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, ex.getMessage()).exception();
        }
        if(dummyCustomer.isSuccess()){
            return customerId;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
