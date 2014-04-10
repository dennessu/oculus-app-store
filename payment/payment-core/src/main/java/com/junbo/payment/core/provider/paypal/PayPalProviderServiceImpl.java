/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.paypal;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.spec.model.Item;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.paypal.exception.*;
import com.paypal.sdk.exceptions.OAuthException;
import org.springframework.beans.factory.InitializingBean;
import org.xml.sax.SAXException;
import urn.ebay.api.PayPalAPI.*;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.*;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PayPal .
 */
public class PayPalProviderServiceImpl extends AbstractPaymentProviderService implements InitializingBean {
    private static final String PROVIDER_NAME = "PayPal";
    private static final String API_VERSION = "104.0";
    private static final PaymentActionCodeType ACTION = PaymentActionCodeType.fromValue("Sale");
    private static PayPalAPIInterfaceServiceService service;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", "sandbox");
        sdkConfig.put("acct1.UserName", "jb-us-seller_api1.paypal.com");
        sdkConfig.put("acct1.Password", "WX4WTU3S8MY44S7F");
        sdkConfig.put("acct1.Signature","AFcWxV21C7fd0v3bYYYRCpSSRl31A7yDhhsPUU2XhtMoZXsWHFxu-RWy");
        service = new PayPalAPIInterfaceServiceService(sdkConfig);
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
        target.setBillingRefId(source.getExternalToken());
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        return null;
    }

    @Override
    public Promise<Response> delete(PaymentInstrument pi) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("authorize").exception();
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("capture").exception();
    }

    @Override
    public Promise<PaymentTransaction> charge(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        CurrencyCodeType currency = CurrencyCodeType.fromValue(paymentRequest.getChargeInfo().getCurrency());
        PaymentDetailsType paymentDetails = new PaymentDetailsType();
        paymentDetails.setPaymentAction(ACTION);
        List<PaymentDetailsItemType> lineItems = new ArrayList<PaymentDetailsItemType>();
        for(Item item : paymentRequest.getChargeInfo().getItems()){
            PaymentDetailsItemType payPalItem = new PaymentDetailsItemType();
            BasicAmountType amt = new BasicAmountType();
            amt.setCurrencyID(currency);
            amt.setValue(item.getAmount());
            payPalItem.setQuantity(item.getQuantity());
            payPalItem.setName(item.getName());
            payPalItem.setAmount(amt);
            lineItems.add(payPalItem);
        }
        paymentDetails.setPaymentDetailsItem(lineItems);
        BasicAmountType orderTotal = new BasicAmountType();
        orderTotal.setCurrencyID(currency);
        orderTotal.setValue(paymentRequest.getChargeInfo().getAmount().toString());
        paymentDetails.setOrderTotal(orderTotal);
        List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();
        paymentDetailsList.add(paymentDetails);

        SetExpressCheckoutRequestDetailsType requestDetails = new SetExpressCheckoutRequestDetailsType();
        requestDetails.setReturnURL(paymentRequest.getWebPaymentInfo().getReturnURL());
        requestDetails.setCancelURL(paymentRequest.getWebPaymentInfo().getCancelURL());

        requestDetails.setPaymentDetails(paymentDetailsList);

        SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(requestDetails);
        setExpressCheckoutRequest.setVersion(API_VERSION);

        SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
        setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

        SetExpressCheckoutResponseType setExpressCheckoutResponse = null;
        try {
            setExpressCheckoutResponse = service.setExpressCheckout(setExpressCheckoutReq);
            paymentRequest.setExternalToken(setExpressCheckoutResponse.getToken());
        } catch (Exception ex){
            handleException(ex);
        }
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("reverse").exception();
    }

    @Override
    public Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("refund").exception();
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("getByBillingRefId").exception();
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(String token) {
        GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest =
                new GetExpressCheckoutDetailsRequestType(token);
        getExpressCheckoutDetailsRequest.setVersion(API_VERSION);

        GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
        getExpressCheckoutDetailsReq.setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);
        GetExpressCheckoutDetailsResponseType responseType = null;
        try {
            responseType = service.getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
        } catch (Exception ex){
            handleException(ex);
        }
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setStatus(PaymentUtil.mapPayPalPaymentStatus(
                responseType.getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus()).toString());
        return Promise.pure(transaction);
    }

    @Override
    public Promise<PaymentTransaction> confirm(String transactionId, PaymentTransaction paymentRequest){
        PaymentDetailsType paymentDetail = new PaymentDetailsType();
        paymentDetail.setNotifyURL("http://replaceIpnUrl.com");
        BasicAmountType orderTotal = new BasicAmountType();
        orderTotal.setValue(paymentRequest.getChargeInfo().getAmount().toString());
        orderTotal.setCurrencyID(CurrencyCodeType.fromValue(paymentRequest.getChargeInfo().getCurrency()));
        paymentDetail.setOrderTotal(orderTotal);
        paymentDetail.setPaymentAction(ACTION);
        List<PaymentDetailsType> paymentDetails = new ArrayList<PaymentDetailsType>();
        paymentDetails.add(paymentDetail);

        DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails =
                new DoExpressCheckoutPaymentRequestDetailsType();
        doExpressCheckoutPaymentRequestDetails.setToken("EC-5KT838080T2886727");
        doExpressCheckoutPaymentRequestDetails.setPayerID("CCZA9BJT9NKTS");
        doExpressCheckoutPaymentRequestDetails.setPaymentDetails(paymentDetails);

        DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest =
                new DoExpressCheckoutPaymentRequestType(doExpressCheckoutPaymentRequestDetails);
        doExpressCheckoutPaymentRequest.setVersion(API_VERSION);

        DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
        doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);

        try {
            DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse =
                    service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
        } catch (Exception ex){
            handleException(ex);
        }
        return Promise.pure(paymentRequest);
    }

    private void handleException(Exception e){
        if(e instanceof SSLConfigurationException) {
            e.printStackTrace();
        } else if (e instanceof InvalidCredentialException) {
            e.printStackTrace();
        } else if (e instanceof IOException) {
            e.printStackTrace();
        } else if (e instanceof HttpErrorException) {
            e.printStackTrace();
        } else if (e instanceof InvalidResponseDataException) {
            e.printStackTrace();
        } else if (e instanceof ClientActionRequiredException) {
            e.printStackTrace();
        } else if (e instanceof MissingCredentialException) {
            e.printStackTrace();
        } else if (e instanceof InterruptedException) {
            e.printStackTrace();
        } else if (e instanceof OAuthException) {
            e.printStackTrace();
        } else if (e instanceof ParserConfigurationException) {
            e.printStackTrace();
        } else if (e instanceof SAXException) {
            e.printStackTrace();
        }
    }
}
