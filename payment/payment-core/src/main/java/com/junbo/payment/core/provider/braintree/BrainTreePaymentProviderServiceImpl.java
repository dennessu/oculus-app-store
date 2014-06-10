/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.braintree;

import com.braintreegateway.*;
import com.braintreegateway.exceptions.DownForMaintenanceException;
import com.junbo.common.util.PromiseFacade;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.PersonalInfoFacade;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.*;
import com.junbo.payment.spec.model.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * brain tree sdk implementation.
 */
public class BrainTreePaymentProviderServiceImpl extends AbstractPaymentProviderService implements InitializingBean {
    @Autowired
    private PersonalInfoFacade personalInfoFacade;
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
    public void clonePIResult(PaymentInstrument source, PaymentInstrument target) {
        target.setAccountNum(source.getAccountNum());
        target.setExternalToken(source.getExternalToken());
        target.getTypeSpecificDetails().setCreditCardType(source.getTypeSpecificDetails().getCreditCardType());
        target.getTypeSpecificDetails().setCommercial(source.getTypeSpecificDetails().getCommercial());
        target.getTypeSpecificDetails().setDebit(source.getTypeSpecificDetails().getDebit());
        target.getTypeSpecificDetails().setPrepaid(source.getTypeSpecificDetails().getPrepaid());
        target.getTypeSpecificDetails().setIssueCountry(source.getTypeSpecificDetails().getIssueCountry());
    }

    @Override
    public void cloneTransactionResult(PaymentTransaction source, PaymentTransaction target) {
        target.setExternalToken(source.getExternalToken());
        if(!CommonUtil.isNullOrEmpty(source.getStatus())){
            target.setStatus(source.getStatus());
        }
    }

    @Override
    public Promise<PaymentInstrument> add(final PaymentInstrument request) {
        return personalInfoFacade.getBillingAddress(request.getBillingAddressId())
            .then(new Promise.Func<com.junbo.payment.spec.model.Address, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(Address address) {
                request.setAddress(address);
                return PromiseFacade.PAYMENT.decorate(new Callable<PaymentInstrument>() {
                    @Override
                    public PaymentInstrument call() throws Exception {
                        String expireDate = request.getTypeSpecificDetails().getExpireDate();
                        String[] tokens = expireDate.split("-");
                        if (tokens == null || tokens.length < 2) {
                            throw AppClientExceptions.INSTANCE.invalidExpireDateFormat(expireDate).exception();
                        }
                        CreditCardRequest ccRequest = new CreditCardRequest()
                                .customerId(getOrCreateCustomerId(request.getUserInfo()))
                                .number(request.getAccountNum())
                                .expirationMonth(String.valueOf(tokens[1]))
                                .expirationYear(String.valueOf(tokens[0]))
                                .cardholderName(request.getAccountName())
                                .cvv(request.getTypeSpecificDetails().getEncryptedCvmCode())
                                .options()
                                .failOnDuplicatePaymentMethod(false)
                                .verifyCard(request.getLastValidatedTime() != null)
                                .done();
                        //Add billing Address
                        if (request.getAddress() != null) {
                            ccRequest.billingAddress()
                                    .postalCode(request.getAddress().getPostalCode())
                                    .streetAddress(request.getAddress().getAddressLine1())
                                    .locality(request.getAddress().getCity())
                                    .region(request.getAddress().getState())
                                    .countryCodeAlpha2(request.getAddress().getCountry())
                                    .done();
                        }
                        Result<CreditCard> result = null;
                        LOGGER.info("add credit card for customer:" + request.getUserId().toString());
                        try {
                            result = gateway.creditCard().create(ccRequest);
                        } catch (Exception ex) {
                            handleProviderException(ex, "Add", "User", request.getUserId().toString());
                        }
                        if (result.isSuccess()) {
                            request.setAccountNum(result.getTarget().getMaskedNumber());
                            request.setExternalToken(result.getTarget().getToken());
                            request.getTypeSpecificDetails().setCreditCardType(
                                    PaymentUtil.getCreditCardType(result.getTarget().getCardType()).toString());
                            request.getTypeSpecificDetails().setCommercial(
                                    CommonUtil.toBool(result.getTarget().getCommercial().toString()));
                            request.getTypeSpecificDetails().setDebit(
                                    CommonUtil.toBool(result.getTarget().getDebit().toString()));
                            request.getTypeSpecificDetails().setPrepaid(
                                    CommonUtil.toBool(result.getTarget().getPrepaid().toString()));
                            String issueCountry = result.getTarget().getCountryOfIssuance();
                            if (issueCountry.equalsIgnoreCase("Unknown")) {
                                request.getTypeSpecificDetails().setIssueCountry(null);
                            } else {
                                request.getTypeSpecificDetails().setIssueCountry(issueCountry);
                            }
                        } else {
                            handleProviderError(result);
                        }
                        return request;
                    }
                });
            }
        });
    }

    @Override
    public Promise<Response> delete(final PaymentInstrument pi) {
        return PromiseFacade.PAYMENT.decorate(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                String token = pi.getExternalToken();
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
                return Response.status(204).build();
            }
        });
    }

    @Override
    public Promise<PaymentInstrument> getByInstrumentToken(String token) {
        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentTransaction> authorize(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                String piToken = pi.getExternalToken();
                if(CommonUtil.isNullOrEmpty(piToken)){
                    throw AppServerExceptions.INSTANCE.noExternalTokenFoundForPayment(pi.getId().toString()).exception();
                }
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
                    paymentRequest.setStatus(PaymentStatus.AUTHORIZED.toString());
                }else{
                    handleProviderError(result);
                }
                return paymentRequest;
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> capture(final String transactionId, final PaymentTransaction request) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
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
                    request.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                } else {
                    handleProviderError(result);
                }
                return request;
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> charge(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                String piToken = pi.getExternalToken();
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
                    paymentRequest.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                }else{
                    handleProviderError(result);
                }
                return paymentRequest;
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> reverse(final String transactionId, final PaymentTransaction paymentRequest) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
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
                return paymentRequest;
            }
        });
    }

    private <T> void handleProviderError(Result<T> result) {
        StringBuffer sbErrorCodes = new StringBuffer();
        for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
            LOGGER.error("gateway validations errors message: " + error.getMessage());
            sbErrorCodes.append(error.getCode()).append("&");
        }
        if(result.getTransaction() != null){
            Transaction resultTrx = result.getTransaction();
            Transaction.GatewayRejectionReason rejectReason = resultTrx.getGatewayRejectionReason();
            if(rejectReason != null){
                sbErrorCodes.append("Risk:" + resultTrx.getGatewayRejectionReason().toString()).append("&");
            }
            String avsError = result.getTransaction().getAvsErrorResponseCode();
            if(!CommonUtil.isNullOrEmpty(avsError)){
                sbErrorCodes.append("Avs:" + avsError);
            }
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
    public Promise<PaymentTransaction> refund(final String transactionId, final PaymentTransaction request) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                Result<Transaction> result = null;
                LOGGER.info("refund transaction :" + transactionId);
                try{
                    if(request.getChargeInfo() == null || request.getChargeInfo().getAmount() == null){
                        result = gateway.transaction().refund(transactionId);
                    }else{
                        //Partial Refund
                        result = gateway.transaction().refund(transactionId, request.getChargeInfo().getAmount());
                    }
                }catch (Exception ex){
                    handleProviderException(ex, "Refund", "transaction", transactionId);
                }
                if (result.isSuccess()) {
                    // transaction successfully submitted for settlement
                    request.setStatus(PaymentStatus.REFUNDED.toString());
                } else {
                    handleProviderError(result);
                }
                return request;
            }
        });

    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
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
            result.setStatus(PaymentUtil.mapBraintreePaymentStatus(PaymentStatus.BrainTreeStatus.valueOf(
                    transaction.getStatus().toString())).toString());
            //TODO: need add transaction.getSettlementBatchId(); for the batch job processing
            results.add(result);
        }
        return results;
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(final PaymentTransaction request) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                if(request.getExternalToken() == null){
                    return null;
                }
                String token = request.getExternalToken();
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
                result.setStatus(PaymentUtil.mapBraintreePaymentStatus(PaymentStatus.BrainTreeStatus.valueOf(
                        transaction.getStatus().toString())).toString());
                //TODO: need add transaction.getSettlementBatchId(); for the batch job processing
                return result;
            }
        });
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

    private String getOrCreateCustomerId(UserInfo userInfo){
        String customerId = userInfo.getUserId().toString();
        Customer customer = null;
        try{
            customer = gateway.customer().find(customerId);
        }catch(Exception ex){
            //Ignore Not Found exception
        }
        if(customer != null) {
            return userInfo.getUserId().toString();
        }
        CustomerRequest request = new CustomerRequest()
                .id(customerId)
                .company(companyName)
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .email(userInfo.getEmail());
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
