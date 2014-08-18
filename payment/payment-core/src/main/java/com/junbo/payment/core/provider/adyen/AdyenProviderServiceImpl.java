/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.adyen;

import com.adyen.services.common.Amount;
import com.adyen.services.payment.*;
import com.adyen.services.recurring.RecurringDetail;
import com.adyen.services.recurring.RecurringDetailsRequest;
import com.adyen.services.recurring.RecurringDetailsResult;
import com.adyen.services.recurring.RecurringLocator;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.util.PromiseFacade;
import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.PaymentProvider;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.enums.Platform;
import com.junbo.payment.spec.model.PaymentCallbackParams;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.model.WebPaymentInfo;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.ws.rs.core.Response;
import javax.xml.rpc.Stub;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * adyen provider service implementation.
 */
public class AdyenProviderServiceImpl extends AbstractAdyenProviderServiceImpl implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenProviderServiceImpl.class);

    @Override
    public void afterPropertiesSet(){
        try{
            service = new PaymentLocator().getPaymentHttpPort(
                new java.net.URL(paymentURL));
            recurService = new RecurringLocator().getRecurringHttpPort(
                new java.net.URL(recurringURL));
        }catch (Exception ex){
            LOGGER.error("error set up adyen service");
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "setup service").exception();
        }
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
            public PaymentTransaction call(){
                if(CommonUtil.isNullOrEmpty(pi.getExternalToken())){
                    String strRequest = getRedirectInfo(pi, paymentRequest);
                    paymentRequest.setWebPaymentInfo(new WebPaymentInfo());
                    paymentRequest.getWebPaymentInfo().setRedirectURL(redirectURL + "?" + strRequest);
                    if(paymentRequest.getWebPaymentInfo() != null){
                        paymentRequest.getWebPaymentInfo().setPlatform(paymentRequest.getWebPaymentInfo().getPlatform());
                    }
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
            LOGGER.error("error get recurring reference: " + e.toString());
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
        }
        if(result != null && result.getDetails() != null && result.getDetails().length > 0){
            return result.getDetails()[0];
        }
        return null;
    }

    private String getRedirectInfo(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        StringBuffer strRequest = new StringBuffer();
        StringBuffer strToSign = new StringBuffer();
        String currency = paymentRequest.getChargeInfo().getCurrency();
        long amount = paymentRequest.getChargeInfo().getAmount()
                .multiply(new BigDecimal(currencyResource.getNumberAfterDecimal(currency).get())).longValue();
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
        String skinToUse = getSkinCode(paymentRequest);
        strToSign.append(skinToUse);
        strRequest.append("&skinCode=" + CommonUtil.urlEncode(skinToUse));
        //merchantAccount
        strToSign.append(merchantAccount);
        strRequest.append("&merchantAccount=" + CommonUtil.urlEncode(merchantAccount));
        //sessionValidity
        Calendar calValid = Calendar.getInstance();
        calValid.add(Calendar.HOUR_OF_DAY, VALID_HOURS);
        ISO8601DateFormat format2 = new ISO8601DateFormat();
        strToSign.append(format2.format(calValid.getTime()));
        strRequest.append("&sessionValidity=" + format2.format(calValid.getTime()));
        //shopperEmail
        String email = paymentRequest.getUserInfo().getEmail();
        strToSign.append(email);
        strRequest.append("&shopperEmail=" + CommonUtil.urlEncode(email));
        //"shopperReference": user piid other than userId, as we only get recurring token for each PI
        String shopperReference = nullToEmpty(pi.getId().toString());
        strToSign.append(shopperReference);
        strRequest.append("&shopperReference=" + CommonUtil.urlEncode(shopperReference));
        //recurringContract
        strToSign.append(RECURRING);
        strRequest.append("&recurringContract=" + RECURRING);
        //merchantReturnData: output the billing ref id(order id)
        String billingRefId = paymentRequest.getBillingRefId();
        strToSign.append(billingRefId);
        strRequest.append("&merchantReturnData=" + billingRefId);
         //signature
        String merchantSig = CommonUtil.calHMCASHA1(strToSign.toString(), skinSecret);
        strRequest.append("&merchantSig=" + CommonUtil.urlEncode(merchantSig));
        if(!CommonUtil.isNullOrEmpty(paymentRequest.getChargeInfo().getCountry())){
            strRequest.append("&countryCode=" + paymentRequest.getChargeInfo().getCountry());
        }
        if(!CommonUtil.isNullOrEmpty(paymentRequest.getChargeInfo().getIpAddress())){
            strRequest.append("&shopperIP=" + paymentRequest.getChargeInfo().getIpAddress());
        }
        return strRequest.toString();
    }

    private String getSkinCode(PaymentTransaction paymentTransaction){
        if(paymentTransaction == null || paymentTransaction.getWebPaymentInfo() == null){
            return skinCode;
        }
        Platform platform = paymentTransaction.getWebPaymentInfo().getPlatform();
        if(platform == null){
            return skinCode;
        }
        switch (platform){
            case PC:
                return skinCode;
            case Mobile:
                return mobileSkinCode;
            case OldMobile:
                return oldMobileSkinCode;
            default:
                throw AppServerExceptions.INSTANCE.invalidPlatform(platform.toString()).exception();
        }
    }

    protected PaymentResult doReferenceCharge(PaymentInstrument pi, PaymentTransaction paymentRequest){
        PaymentRequest request = new PaymentRequest();
        request.setMerchantAccount(merchantAccount);
        request.setSelectedRecurringDetailReference(pi.getExternalToken());
        request.setRecurring(new Recurring(RECURRING, null));
        String currencyCode = paymentRequest.getChargeInfo().getCurrency();
        request.setAmount(new Amount(currencyCode, paymentRequest.getChargeInfo().getAmount().multiply(
                     new BigDecimal(currencyResource.getNumberAfterDecimal(currencyCode).get())).longValue()));
        request.setReference(CommonUtil.encode(paymentRequest.getId()));
        request.setShopperEmail(paymentRequest.getUserInfo().getEmail());
        request.setShopperReference(nullToEmpty(paymentRequest.getPaymentInstrumentId().toString()));
        request.setShopperInteraction("ContAuth");
        request.setShopperIP(paymentRequest.getChargeInfo().getIpAddress());
        PaymentResult result = null;
        try {
            result = service.authorise(request);
        } catch (RemoteException e) {
            LOGGER.error("error call Adyen authorise API.");
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
        }
        if(result != null && result.getResultCode().equals("Authorised")){
            return result;
        }
        LOGGER.error("error get the Adyen authorise response.");
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
            public PaymentTransaction call() {
                ModificationRequest refundReq = new ModificationRequest();
                refundReq.setMerchantAccount(merchantAccount);
                refundReq.setOriginalReference(transactionId);
                ModificationResult cancelResult = null;
                refundReq.setReference(CommonUtil.encode(paymentRequest.getId()));
                try{
                    cancelResult = service.cancel(refundReq);
                } catch (RemoteException e) {
                    LOGGER.error("error call adyen cancel API.");
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
                }
                if(cancelResult != null){
                    if(!cancelResult.getResponse().equalsIgnoreCase(CANCEL_STATE)){
                        LOGGER.error("reverse failed for:" + transactionId + cancelResult.getResponse());
                        throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "Invalid Response").exception();
                    }
                    paymentRequest.setStatus(PaymentStatus.REVERSED.toString());
                }else{
                    LOGGER.error("error get adyen cancel response.");
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
                    throw AppCommonErrors.INSTANCE.fieldRequired("amount").exception();
                }
                ModificationRequest refundReq = new ModificationRequest();
                refundReq.setMerchantAccount(merchantAccount);
                refundReq.setOriginalReference(transactionId);
                String currency = request.getChargeInfo().getCurrency();
                refundReq.setModificationAmount(new Amount(currency,
                        request.getChargeInfo().getAmount().multiply(
                            new BigDecimal(currencyResource.getNumberAfterDecimal(currency).get())).longValue()));
                ModificationResult refundResult = null;
                refundReq.setReference(CommonUtil.encode(request.getId()));
                try{
                    refundResult = service.refund(refundReq);
                } catch (RemoteException e) {
                    LOGGER.error("error call adyen refund API.");
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
                }
                if(refundResult != null){
                    if(!refundResult.getResponse().equalsIgnoreCase(REFUND_STATE)){
                        LOGGER.error("reverse failed for:" + transactionId + refundResult.getResponse());
                        throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "Invalid Response").exception();
                    }
                    request.setStatus(PaymentStatus.REFUNDED.toString());
                }else{
                    LOGGER.error("error get adyen refund response.");
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "No Response").exception();
                }
                return request;
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> confirmNotify(PaymentTransaction payment, PaymentCallbackParams properties){
        //validate signature: authResult + pspReference + merchantReference + skinCode + merchantReturnData
        if(!CommonUtil.isNullOrEmpty(properties.getPspReference()) && !CommonUtil.isNullOrEmpty(properties.getAuthResult())){
            String strToSign = properties.getAuthResult() + properties.getPspReference() +
                    properties.getMerchantReference() + properties.getSkinCode() + properties.getMerchantReturnData();
            if(!CommonUtil.calHMCASHA1(strToSign, skinSecret).equals(properties.getMerchantSig())){
                LOGGER.error("Signature is not matched for:" + strToSign);
                throw AppServerExceptions.INSTANCE.errorCalculateHMCA().exception();
            }
        }else{
            LOGGER.error("invalid callback: Info is empty or not enough.");
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "invalid callback").exception();
        }
        if(properties.getAuthResult().equalsIgnoreCase(CONFIRMED_STATUS)){
            updatePayment(payment,PaymentUtil.getPaymentStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString()), properties.getPspReference());
            payment.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
            payment.setExternalToken(properties.getPspReference());
            //get the recurring info and save it back as pi external token:
            RecurringDetail recurringReference = getRecurringReference(payment.getPaymentInstrumentId());
            updatePIInfo(payment.getPaymentInstrumentId(), recurringReference);
        }else{
            updatePayment(payment,PaymentUtil.getPaymentStatus(PaymentStatus.UNCONFIRMED.toString()), properties.getPspReference());
            payment.setStatus(PaymentStatus.UNCONFIRMED.toString());
            payment.setExternalToken(properties.getPspReference());
        }
        return Promise.pure(payment);
    }

    protected void updatePIInfo(Long piId, RecurringDetail recurringReference) {
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
            updatePIInfo(piId, recurringReference.getRecurringDetailReference(), label, accountNum);
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

    @Override
    public Promise<PaymentTransaction> processNotify(String request){
        // Check user and password
        checkAuthorization();
        //TODO: save to DB and check redundant notification. Log it first
        LOGGER.info("receive notification from ayden:" + request);
        //
        try{
            process(request);
        }catch (Exception ex){
            LOGGER.error("process adyen notification error:" + ex.toString());
        }
        return Promise.pure(null);
    }

    private void process(String request) {
        //get results
        Map<String, String> notifies = new HashMap<>();
        String[] requests = request.split("&");
        for(String field : requests){
            String[] results = field.split("=");
            if(Arrays.asList(NOTIFY_FIELDS).contains(results[0])){
                notifies.put(results[0], results.length > 1 ? results[1] : null);
            }
        }
        AdyenNotifyRequest notify = CommonUtil.parseJson(CommonUtil.toJson(notifies, null), AdyenNotifyRequest.class);
        if(notify.getPspReference() == null){
            LOGGER.warn("no psp reference available");
            return ;
        }
        Long paymentId = CommonUtil.decode(notify.getMerchantReference());
        String merchantAccount = notify.getMerchantAccountCode();
        PaymentTransaction transaction = paymentRepositoryFacade.getByPaymentId(paymentId);
        if(transaction == null){
            //Credit Card Auth use PIID as reference so no transaction would be found:
            LOGGER.warn("cannot find payment transaction:" + paymentId);
            return ;
        }
        String externalToken = notify.getPspReference();
        if(notify.getSuccess().equalsIgnoreCase("true") && merchantAccount.equalsIgnoreCase(this.merchantAccount)
                && externalToken.equalsIgnoreCase(transaction.getExternalToken())){
            if(notify.getEventCode().equalsIgnoreCase(AdyenEventCode.AUTHORISATION.name())){
                //Ignore of Credit Card Auth as CC use API call directly
                if(!transaction.getPaymentProvider().equalsIgnoreCase(PaymentProvider.AdyenCC.toString())){
                    paymentRepositoryFacade.updatePayment(paymentId, PaymentUtil.getPaymentStatus(
                            PaymentStatus.SETTLEMENT_SUBMITTED.toString()), null);
                }
            }else if(notify.getEventCode().equalsIgnoreCase(AdyenEventCode.CANCELLATION.name())){
                paymentRepositoryFacade.updatePayment(paymentId, PaymentUtil.getPaymentStatus(
                        PaymentStatus.REVERSED.toString()), null);
            }else if(notify.getEventCode().equalsIgnoreCase(AdyenEventCode.REFUND.name())){
                paymentRepositoryFacade.updatePayment(paymentId, PaymentUtil.getPaymentStatus(
                        PaymentStatus.REFUNDED.toString()), null);
            }else if(notify.getEventCode().equalsIgnoreCase(AdyenEventCode.CAPTURE_FAILED.name())){
                paymentRepositoryFacade.updatePayment(paymentId, PaymentUtil.getPaymentStatus(
                        PaymentStatus.SETTLE_DECLINED.toString()), null);
            }else if(notify.getEventCode().equalsIgnoreCase(AdyenEventCode.REFUND_FAILED.name())){
                paymentRepositoryFacade.updatePayment(paymentId, PaymentUtil.getPaymentStatus(
                        PaymentStatus.REFUND_DECLINED.toString()), null);
            }
        }
    }

    private void checkAuthorization() {
        String authHeader = JunboHttpContext.getRequestHeaders().getFirst("authorization");
        if (authHeader != null){
            String encodedValue = authHeader.split(" ")[1];
            String decodedValue = new String(Base64.decodeBase64(encodedValue.getBytes()));
            if(!decodedValue.equals(notifyUser + ":" + notifyPassword)){
                LOGGER.error("invalid authorization to receive notification:" + authHeader);
                throw AppServerExceptions.INSTANCE.unAuthorized(authHeader).exception();
            }
        }else{
            LOGGER.error("missing authorize header");
            throw AppCommonErrors.INSTANCE.headerRequired("authorization").exception();
        }
    }
}
