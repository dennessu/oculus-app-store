/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.adyen;

import com.adyen.services.common.Amount;
import com.adyen.services.payment.*;
import com.adyen.services.payment.Recurring;
import com.adyen.services.recurring.*;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.junbo.common.util.PromiseFacade;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.CountryServiceFacade;
import com.junbo.payment.clientproxy.CurrencyServiceFacade;
import com.junbo.payment.clientproxy.PersonalInfoFacade;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.db.repository.PaymentInstrumentRepository;
import com.junbo.payment.db.repository.PaymentRepository;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentProperties;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.model.WebPaymentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import javax.xml.rpc.Stub;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * adyen provider service implementation.
 */
public class AdyenProviderServiceImpl extends AbstractPaymentProviderService implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenProviderServiceImpl.class);
    private static final String PROVIDER_NAME = "Adyen";
    protected static final String CONFIRMED_STATUS = "AUTHORISED";
    protected static final String RECURRING = "RECURRING";
    protected static final String CANCEL_STATE = "[cancel-received]";
    protected static final String REFUND_STATE = "[refund-received]";
    protected static final String CAPTURE_STATE = "[capture-received]";
    private static final String AUTH_USER = "javax.xml.rpc.security.auth.username";
    private static final String AUTH_PWD = "javax.xml.rpc.security.auth.password";
    private static final int SHIP_DELAY = 1;
    private static final int VALID_HOURS = 3;
    private String redirectURL;
    private String paymentURL;
    private String recurringURL;
    private String merchantAccount;
    private String skinCode;
    private String skinSecret;
    private String authUser;
    private String authPassword;
    protected PaymentPortType service;
    protected RecurringPortType recurService;
    @Autowired
    protected PaymentInstrumentRepository paymentInstrumentRepository;
    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    protected CountryServiceFacade countryResource;
    @Autowired
    protected CurrencyServiceFacade currencyResource;
    @Autowired
    protected PersonalInfoFacade personalInfoFacade;

    @Override
    public void afterPropertiesSet() throws Exception {
        service = new PaymentLocator().getPaymentHttpPort(
                new java.net.URL(paymentURL));
        recurService = new RecurringLocator().getRecurringHttpPort(
                new java.net.URL(recurringURL));
        //Basic HTTP Authentication:
        ((Stub)service)._setProperty(AUTH_USER,authUser);
        ((Stub)service)._setProperty(AUTH_PWD,authPassword);
        ((Stub)recurService)._setProperty(AUTH_USER,authUser);
        ((Stub)recurService)._setProperty(AUTH_PWD,authPassword);
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
        if(!CommonUtil.isNullOrEmpty(source.getExternalToken())){
            target.setExternalToken(source.getExternalToken());
        }
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
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                if(CommonUtil.isNullOrEmpty(pi.getExternalToken())){
                    String strRequest = getRedirectInfo(pi, paymentRequest);
                    paymentRequest.setWebPaymentInfo(new WebPaymentInfo());
                    paymentRequest.getWebPaymentInfo().setRedirectURL(redirectURL + "?" + strRequest);
                    paymentRequest.setStatus(PaymentStatus.UNCONFIRMED.toString());
                    return paymentRequest;
                }else{
                    PaymentResult adyenResult = doReferenceCharge(pi, paymentRequest);
                    paymentRequest.setWebPaymentInfo(null);
                    paymentRequest.setStatus(PaymentStatus.SETTLED.toString());
                    paymentRequest.setExternalToken(adyenResult.getPspReference());
                    return paymentRequest;
                }
            }
        });
    }

    protected RecurringDetail getRecurringReference(Long shopperRef) {
        RecurringDetailsRequest request = new RecurringDetailsRequest();
        request.setMerchantAccount(merchantAccount);
        request.setRecurring(new Recurring(RECURRING, null));
        request.setShopperReference(nullToEmpty(shopperRef.toString()));
        RecurringDetailsResult result = null;
        try {
            result = recurService.listRecurringDetails(request);
        } catch (RemoteException e) {
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
        }
        if(result != null && result.getDetails() != null && result.getDetails().length > 0){
            RecurringDetail[] details = result.getDetails();
            return details[0];
        }
        return null;
    }

    private String getRedirectInfo(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        StringBuffer strRequest = new StringBuffer();
        StringBuffer strToSign = new StringBuffer();
        String currency = paymentRequest.getChargeInfo().getCurrency();
        long amount = paymentRequest.getChargeInfo().getAmount().longValue()
                * currencyResource.getNumberAfterDecimal(currency).get();
        strToSign.append(amount);
        strRequest.append("paymentAmount=" + amount);
        strToSign.append(currency);
        strRequest.append("&currencyCode=" + currency);
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
        //shopperEmail
        //TODO: get user email
        String email = paymentRequest.getUserInfo().getEmail();
        strToSign.append(email);
        strRequest.append("&shopperEmail=" + email);
        //"shopperReference": user piid other than userId, as we only get recurring token for each PI
        String shopperReference = nullToEmpty(pi.getId().toString());
        strToSign.append(shopperReference);
        strRequest.append("&shopperReference=" + shopperReference);
        //recurringContract
        strToSign.append(RECURRING);
        strRequest.append("&recurringContract=" + RECURRING);
         //signature
        String merchantSig = CommonUtil.calHMCASHA1(strToSign.toString(), skinSecret);
        strRequest.append("&merchantSig=" + merchantSig);
        if(!CommonUtil.isNullOrEmpty(paymentRequest.getChargeInfo().getCountry())){
            strRequest.append("&countryCode=" + paymentRequest.getChargeInfo().getCountry());
        }
        if(!CommonUtil.isNullOrEmpty(paymentRequest.getChargeInfo().getIpAddress())){
            strRequest.append("&shopperIP=" + paymentRequest.getChargeInfo().getIpAddress());
        }
        return strRequest.toString();
    }

    protected PaymentResult doReferenceCharge(PaymentInstrument pi, PaymentTransaction paymentRequest){
        PaymentRequest request = new PaymentRequest();
        request.setMerchantAccount(merchantAccount);
        request.setSelectedRecurringDetailReference(pi.getExternalToken());
        request.setRecurring(new Recurring(RECURRING, null));
        String currencyCode = paymentRequest.getChargeInfo().getCurrency();
        request.setAmount(new Amount(currencyCode,
                paymentRequest.getChargeInfo().getAmount().longValue()
                        * currencyResource.getNumberAfterDecimal(currencyCode).get()));
        request.setReference(CommonUtil.encode(paymentRequest.getId()));
        request.setShopperEmail(paymentRequest.getUserInfo().getEmail());
        request.setShopperReference(nullToEmpty(paymentRequest.getPaymentInstrumentId().toString()));
        request.setShopperInteraction("ContAuth");
        request.setShopperIP(paymentRequest.getChargeInfo().getIpAddress());
        PaymentResult result = null;
        try {
            result = service.authorise(request);
        } catch (RemoteException e) {
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
        }
        if(result != null && result.getResultCode().equals("Authorised")){
            return result;
        }
        throw AppServerExceptions.INSTANCE.providerProcessError(
                PROVIDER_NAME, result == null ? "No Result" : result.getRefusalReason()).exception();
    }

    @Override
    public Promise<PaymentTransaction> confirm(final String transactionId, final PaymentTransaction paymentRequest){
        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentTransaction> reverse(final String transactionId, final PaymentTransaction paymentRequest) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                ModificationRequest refundReq = new ModificationRequest();
                refundReq.setMerchantAccount(merchantAccount);
                refundReq.setOriginalReference(transactionId);
                ModificationResult cancelResult = null;
                try{
                    cancelResult = service.cancel(refundReq);
                } catch (RemoteException e) {
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
                }
                if(cancelResult != null){
                    if(!cancelResult.getResponse().equalsIgnoreCase(CANCEL_STATE)){
                        LOGGER.error("reverse failed for:" + transactionId + cancelResult.getResponse());
                        throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "Invalid Response").exception();
                    }
                    paymentRequest.setStatus(PaymentStatus.REVERSED.toString());
                }else{
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "No Response").exception();
                }
                return paymentRequest;
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> refund(final String transactionId, final PaymentTransaction request) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                if(request.getChargeInfo() == null || request.getChargeInfo().getAmount() == null){
                    throw AppClientExceptions.INSTANCE.missingAmount().exception();
                }
                ModificationRequest refundReq = new ModificationRequest();
                refundReq.setMerchantAccount(merchantAccount);
                refundReq.setOriginalReference(transactionId);
                String currency = request.getChargeInfo().getCurrency();
                refundReq.setModificationAmount(new Amount(currency,
                        request.getChargeInfo().getAmount().longValue()
                                * currencyResource.getNumberAfterDecimal(currency).get()));
                ModificationResult refundResult = null;
                try{
                    refundResult = service.refund(refundReq);
                } catch (RemoteException e) {
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
                }
                if(refundResult != null){
                    if(!refundResult.getResponse().equalsIgnoreCase(REFUND_STATE)){
                        LOGGER.error("reverse failed for:" + transactionId + refundResult.getResponse());
                        throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "Invalid Response").exception();
                    }
                    request.setStatus(PaymentStatus.REFUNDED.toString());
                }else{
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "No Response").exception();
                }
                return request;
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> confirmNotify(PaymentTransaction payment, PaymentProperties properties){
        if(!CommonUtil.isNullOrEmpty(properties.getPspReference())
                && !CommonUtil.isNullOrEmpty(properties.getAuthResult())
                && properties.getAuthResult().equalsIgnoreCase(CONFIRMED_STATUS)){
            paymentRepository.updatePayment(payment.getId(), PaymentUtil.getPaymentStatus(
                    PaymentStatus.SETTLED.toString()), properties.getPspReference());
            payment.setStatus(PaymentStatus.SETTLED.toString());
            payment.setExternalToken(properties.getPspReference());
        }
        //get the recurring info and save it back as pi external token:
        RecurringDetail recurringReference = getRecurringReference(payment.getPaymentInstrumentId());
        updateExternalToken(payment.getPaymentInstrumentId(), recurringReference);
        //TODO: send back ACK [accepted] to adyen for notifications
        return Promise.pure(payment);
    }

    protected void updateExternalToken(Long piId, RecurringDetail recurringReference) {
        if(recurringReference != null){
            String label = recurringReference.getVariant();
            String accountNum = null;
            if(recurringReference.getBank() != null){
                accountNum = recurringReference.getBank().getBankAccountNumber();
                if(CommonUtil.isNullOrEmpty(label)){
                    label = "BankAccount";
                }
            }else if(recurringReference.getCard() != null){
                accountNum =recurringReference.getCard().getNumber();
                if(CommonUtil.isNullOrEmpty(label)){
                    label = "CreditCard";
                }
            }else if(recurringReference.getElv() != null){
                accountNum = recurringReference.getElv().getBankAccountNumber();
                if(CommonUtil.isNullOrEmpty(label)){
                    label = "Elv";
                }
            }
            paymentInstrumentRepository.updateExternalInfo(piId,
                    recurringReference.getRecurringDetailReference(), label, accountNum);
        }
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

    public String getPaymentURL() {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL) {
        this.paymentURL = paymentURL;
    }

    public String getRecurringURL() {
        return recurringURL;
    }

    public void setRecurringURL(String recurringURL) {
        this.recurringURL = recurringURL;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }
}
