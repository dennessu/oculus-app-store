/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.adyen;

import com.adyen.services.common.Amount;
import com.adyen.services.payment.*;
import com.adyen.services.recurring.RecurringDetail;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.util.PromiseFacade;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.PersonalInfoFacade;
import com.junbo.payment.clientproxy.adyen.AdyenApi;
import com.junbo.payment.clientproxy.adyen.proxy.AdyenApiClientProxy;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.Address;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import com.junbo.sharding.IdGenerator;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * adyen credit card provider service implementation.
 */
public class AdyenCCProivderServiceImpl extends AdyenProviderServiceImpl{
    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenCCProivderServiceImpl.class);
    private static final String PROVIDER_NAME = "AdyenCC";
    private static final String CARD_BIN = "cardBin";
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;
    @Autowired
    @Qualifier("adyenRestClient")
    private AdyenApi adyenRestClient;
    private PersonalInfoFacade personalInfoFacade;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    @Override
    public Promise<PaymentInstrument> add(final PaymentInstrument request) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentInstrument>() {
            @Override
            public PaymentInstrument call() throws Exception {
                Address addressDetail = personalInfoFacade.getBillingAddress(request.getBillingAddressId()).get();
                if(addressDetail == null){
                    throw AppClientExceptions.INSTANCE.billingAddressNotFound(request.getBillingAddressId().toString()).exception();
                }
                String defaultCountry = addressDetail.getCountry();
                CurrencyId defaultCurrency = countryResource.getDefaultCurrency(defaultCountry).get();
                long minAuthAmount = currencyResource.getMinAuthAmount(defaultCurrency).get();
                Long piId = null;
                if(request.getId() == null){
                    piId = idGenerator.nextId(request.getUserId());
                }
                request.setId(piId);
                PaymentRequest adyenRequest = new PaymentRequest();
                adyenRequest.setMerchantAccount(getMerchantAccount());
                adyenRequest.setRecurring(new Recurring(RECURRING, null));
                adyenRequest.setAmount(new Amount(defaultCurrency.getValue(), minAuthAmount));
                //no payment transaction when Add PI so just use the PIID
                adyenRequest.setReference(CommonUtil.encode(piId));
                adyenRequest.setShopperEmail(request.getUserInfo().getEmail());
                adyenRequest.setShopperReference(piId.toString());
                adyenRequest.setShopperInteraction("ContAuth");
                AnyType2AnyTypeMapEntry encryptedInfo = new AnyType2AnyTypeMapEntry();
                encryptedInfo.setKey("card.encrypted.json");
                //encrypted account number
                encryptedInfo.setValue(request.getAccountNumber());
                adyenRequest.setAdditionalData((AnyType2AnyTypeMapEntry[]) Arrays.asList(encryptedInfo).toArray());
                // Billing address
                Address address = null;
                if(request.getBillingAddressId() != null){
                    address = personalInfoFacade.getBillingAddress(request.getBillingAddressId()).get();
                    address.setId(request.getBillingAddressId());
                }
                if(address != null){
                    Card card = new Card();
                    com.adyen.services.common.Address billingAddress = new com.adyen.services.common.Address();
                    billingAddress.setCity(address.getCity());
                    billingAddress.setCountry(address.getCountry());
                    billingAddress.setPostalCode(address.getPostalCode());
                    billingAddress.setStateOrProvince(address.getState());
                    billingAddress.setStreet(address.getAddressLine1());
                    card.setBillingAddress(billingAddress);
                    adyenRequest.setCard(card);
                }
                PaymentResult result = null;
                try {
                    //TODO: enable SDK if Adyen fixed
                    //result = service.authorise(adyenRequest);
                    //Rest call
                    MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
                    String authCode = Base64.encodeBase64String((getAuthUser() + ":" + getAuthPassword()).getBytes());
                    headers.putSingle("Authorization", "Basic " + authCode);
                    headers.putSingle("Accept", "text/html");
                    ((AdyenApiClientProxy)adyenRestClient).setHeaders(headers);
                    StringBuffer sbReq = getRawRequest(defaultCurrency, minAuthAmount, piId, request, address);
                    String restResponse = adyenRestClient.authorise(sbReq.toString()).get();
                    if(CommonUtil.isNullOrEmpty(restResponse)){
                        LOGGER.error("empty response from Adyen.");
                        throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "no response").exception();
                    }
                    result = getPaymentResult(restResponse);
                } catch (Exception e) {
                    LOGGER.error("error calling adyen:", e);
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
                }
                if(result != null && result.getResultCode().equalsIgnoreCase(CONFIRMED_STATUS)){
                    RecurringDetail recurringDetail = getRecurringReference(piId);
                    if(recurringDetail != null && recurringDetail.getCard() != null){
                        request.setExternalToken(recurringDetail.getRecurringDetailReference());
                        AnyType2AnyTypeMapEntry[] additionalData = result.getAdditionalData();
                        String cardBin = "";
                        if(additionalData != null && additionalData.length > 0){
                            for(AnyType2AnyTypeMapEntry data : additionalData){
                                if(data.getKey().equals(CARD_BIN)){
                                    cardBin = data.getValue().toString();
                                    break;
                                }
                            }
                        }
                        request.setAccountNumber(recurringDetail.getCard().getNumber());
                        String cardHolder = recurringDetail.getCard().getHolderName();
                        if(CommonUtil.isNullOrEmpty(cardHolder)){
                            request.setAccountName(cardHolder);
                        }
                        if(request.getTypeSpecificDetails() == null){
                            request.setTypeSpecificDetails(new TypeSpecificDetails());
                        }
                        request.getTypeSpecificDetails().setIssuerIdentificationNumber(cardBin);
                        request.getTypeSpecificDetails().setCreditCardType(recurringDetail.getVariant());
                        request.getTypeSpecificDetails().setExpireDate(recurringDetail.getCard().getExpiryYear()
                                + "-" + recurringDetail.getCard().getExpiryMonth());
                    }else{
                        LOGGER.error("no recurring info available for PI:" + piId);
                        throw AppServerExceptions.INSTANCE.providerProcessError(
                                PROVIDER_NAME, "Invalid Card to add").exception();
                    }
                }else{
                    LOGGER.error("error response from Adyen.");
                    throw AppServerExceptions.INSTANCE.providerProcessError(
                        PROVIDER_NAME, result == null ? "No Result" : result.getRefusalReason()).exception();
                }
                return request;
            }
        });
    }

    private PaymentResult getPaymentResult(String restResponse) {
        PaymentResult result = null;
        String[] resultTokens = restResponse.split("&");
        Map<String, String> resultMap = new HashMap<>();
        for(String resultField : resultTokens){
            String[] fieldTokens = resultField.split("=");
            if(fieldTokens == null || fieldTokens.length != 2){
                LOGGER.error("error parse result:" + restResponse);
                throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "error response").exception();
            }
            resultMap.put(fieldTokens[0], fieldTokens[1]);
        }
        if(CONFIRMED_STATUS.equalsIgnoreCase(resultMap.get("paymentResult.resultCode"))){
            result = new PaymentResult();
            result.setResultCode(CONFIRMED_STATUS);
            AnyType2AnyTypeMapEntry additionalData = new AnyType2AnyTypeMapEntry();
            additionalData.setKey(CARD_BIN);
            additionalData.setValue(resultMap.get("paymentResult.additionalData.cardBin"));
            result.setAdditionalData((AnyType2AnyTypeMapEntry[])Arrays.asList(additionalData).toArray());
        }else{
            String refusedReason = "refused:";
            if(resultMap.get("paymentResult.refusalReason") != null){
                refusedReason += resultMap.get("paymentResult.refusalReason");
            }
            LOGGER.error("adyen refused:" + restResponse);
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, refusedReason).exception();
        }
        return result;
    }

    private StringBuffer getRawRequest(CurrencyId defaultCurrency, long minAuthAmount, Long piId,
                         PaymentInstrument request, Address address){
        StringBuffer sbReq = new StringBuffer();
        sbReq.append("action=Payment.authorise");
        sbReq.append("&paymentRequest.card.holderName=" + request.getAccountName());
        try {
            sbReq.append("&paymentRequest.amount.currency=" + CommonUtil.urlEncode(defaultCurrency.getValue()));
            sbReq.append("&paymentRequest.amount.value=" + minAuthAmount);
            sbReq.append("&paymentRequest.merchantAccount=" + CommonUtil.urlEncode(getMerchantAccount()));
            sbReq.append("&paymentRequest.reference=" + CommonUtil.encode(piId));
            sbReq.append("&paymentRequest.additionalData.card.encrypted.json=" + CommonUtil.urlEncode(request.getAccountNumber()));
            if(CommonUtil.isNullOrEmpty(request.getUserInfo().getEmail())){
                throw AppClientExceptions.INSTANCE.missingEmail().exception();
            }
            sbReq.append("&paymentRequest.shopperEmail=" + CommonUtil.urlEncode(request.getUserInfo().getEmail()));
            sbReq.append("&paymentRequest.shopperReference=" + piId.toString());
            sbReq.append("&paymentRequest.recurring.contract=" + RECURRING);
            sbReq.append("&paymentRequest.shopperInteraction=ContAuth");

            if(address != null){
                sbReq.append(getAddressRequest(address));
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("error encode the URL", e);
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "error encode").exception();
        }
        return sbReq;
    }

    private String getAddressRequest(Address address) throws UnsupportedEncodingException {
        StringBuffer sbReq = new StringBuffer();
        if(!CommonUtil.isNullOrEmpty(address.getCity())){
            sbReq.append("&paymentRequest.card.billingAddress.city=" + CommonUtil.urlEncode(address.getCity()));
        }else{
            throw AppClientExceptions.INSTANCE.invalidBillingAddressId(address.getId().toString(), "missing City").exception();
        }
        if(!CommonUtil.isNullOrEmpty(address.getAddressLine1())){
            sbReq.append("&paymentRequest.card.billingAddress.street=" + CommonUtil.urlEncode(address.getAddressLine1()));
        }else{
            throw AppClientExceptions.INSTANCE.invalidBillingAddressId(address.getId().toString(), "missing street").exception();
        }
        if(!CommonUtil.isNullOrEmpty(address.getPostalCode())){
            sbReq.append("&paymentRequest.card.billingAddress.postalCode=" + CommonUtil.urlEncode(address.getPostalCode()));
        }else{
            throw AppClientExceptions.INSTANCE.invalidBillingAddressId(address.getId().toString(), "missing postalCode").exception();
        }
        if(!CommonUtil.isNullOrEmpty(address.getState())){
            sbReq.append("&paymentRequest.card.billingAddress.stateOrProvince=" + CommonUtil.urlEncode(address.getState()));
        }else{
            throw AppClientExceptions.INSTANCE.invalidBillingAddressId(address.getId().toString(), "missing subCountry").exception();
        }
        if(!CommonUtil.isNullOrEmpty(address.getCountry())){
            sbReq.append("&paymentRequest.card.billingAddress.country=" + CommonUtil.urlEncode(address.getCountry()));
        }else{
            throw AppClientExceptions.INSTANCE.invalidBillingAddressId(address.getId().toString(), "missing Country").exception();
        }
        if(!CommonUtil.isNullOrEmpty(address.getUnitNumber())){
            sbReq.append("&paymentRequest.card.billingAddress.houseNumberOrName=" + CommonUtil.urlEncode(address.getUnitNumber()));
        }else{
            throw AppClientExceptions.INSTANCE.invalidBillingAddressId(address.getId().toString(), "missing UnitNumber").exception();
        }
        return sbReq.toString();
    }

    @Override
    public Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        PaymentResult adyenResult = doReferenceCharge(pi, paymentRequest);
        paymentRequest.setWebPaymentInfo(null);
        paymentRequest.setStatus(PaymentStatus.AUTHORIZED.toString());
        paymentRequest.setExternalToken(adyenResult.getPspReference());
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> capture(final String transactionId, final PaymentTransaction paymentRequest) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                //adyen need charge info when do the capture
                if(paymentRequest.getChargeInfo() == null){
                    throw AppCommonErrors.INSTANCE.fieldRequired("amount").exception();
                }
                if(paymentRequest.getChargeInfo().getAmount() == null){
                    throw AppCommonErrors.INSTANCE.fieldRequired("amount").exception();
                }
                if(CommonUtil.isNullOrEmpty(paymentRequest.getChargeInfo().getCurrency())){
                    throw AppCommonErrors.INSTANCE.fieldRequired("currency").exception();
                }
                ModificationRequest request = new ModificationRequest();
                request.setMerchantAccount(getMerchantAccount());
                String currency = paymentRequest.getChargeInfo().getCurrency();
                request.setModificationAmount(new Amount(currency,
                        paymentRequest.getChargeInfo().getAmount().multiply(
                           new BigDecimal(currencyResource.getNumberAfterDecimal(currency).get())).longValue()));
                request.setOriginalReference(transactionId);
                ModificationResult result = null;
                try {
                    result = service.capture(request);
                } catch (RemoteException e) {
                    LOGGER.error("error call adyen capture.", e);
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
                }
                if(result != null && result.getResponse().equals(CAPTURE_STATE)){
                    paymentRequest.setWebPaymentInfo(null);
                    paymentRequest.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                    //update external token in capture as Adyen has new ref and cancel/refund would use this one
                    paymentRequest.setExternalToken(result.getPspReference());
                    return paymentRequest;
                }
                return paymentRequest;
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> charge(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        return authorize(pi, paymentRequest)
                .then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                        return capture(paymentTransaction.getExternalToken(), paymentRequest);
                    }
                });
    }
    @Required
    public void setPersonalInfoFacade(PersonalInfoFacade personalInfoFacade) {
        this.personalInfoFacade = personalInfoFacade;
    }
}
