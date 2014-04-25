/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.paypal;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.Item;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.model.WebPaymentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import urn.ebay.api.PayPalAPI.*;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.*;

import javax.ws.rs.core.Response;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.junbo.common.util.PromiseFacade;

/**
 * PayPal .
 */
public class PayPalProviderServiceImpl extends AbstractPaymentProviderService implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayPalProviderServiceImpl.class);
    private static final String PROVIDER_NAME = "PayPal";
    private static final PaymentActionCodeType ACTION = PaymentActionCodeType.fromValue("Sale");
    private static PayPalAPIInterfaceServiceService service;
    private String apiVersion;
    private String redirectURL;
    private String mode;
    private String userName;
    private String password;
    private String signature;
    private static final String REDIRECT_URL_PATH = "?cmd=_express-checkout&token=";

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", mode);
        sdkConfig.put("acct1.UserName", userName);
        sdkConfig.put("acct1.Password", password);
        sdkConfig.put("acct1.Signature",signature);
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
        if(source.getWebPaymentInfo() != null){
            if(target.getWebPaymentInfo() == null){
                target.setWebPaymentInfo(new WebPaymentInfo());
            }
            target.getWebPaymentInfo().setToken(source.getWebPaymentInfo().getToken());
            target.getWebPaymentInfo().setRedirectURL(source.getWebPaymentInfo().getRedirectURL());
        }
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
    public Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("authorize").exception();
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("capture").exception();
    }

    @Override
    public Promise<PaymentTransaction> charge(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                CurrencyCodeType currency = CurrencyCodeType.fromValue(paymentRequest.getChargeInfo().getCurrency());
                PaymentDetailsType paymentDetails = new PaymentDetailsType();
                paymentDetails.setPaymentAction(ACTION);
                if(paymentRequest.getChargeInfo().getItems() != null){
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
                }
                BasicAmountType orderTotal = new BasicAmountType();
                orderTotal.setCurrencyID(currency);
                orderTotal.setValue(paymentRequest.getChargeInfo().getAmount().toString());
                paymentDetails.setOrderTotal(orderTotal);
                List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();
                paymentDetailsList.add(paymentDetails);

                SetExpressCheckoutRequestDetailsType requestDetails = new SetExpressCheckoutRequestDetailsType();
                requestDetails.setReturnURL(getFullReturnUrl(paymentRequest));
                requestDetails.setCancelURL(paymentRequest.getWebPaymentInfo().getCancelURL());

                requestDetails.setPaymentDetails(paymentDetailsList);

                SetExpressCheckoutRequestType setRequest = new SetExpressCheckoutRequestType(requestDetails);
                setRequest.setVersion(apiVersion);

                SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
                setExpressCheckoutReq.setSetExpressCheckoutRequest(setRequest);

                SetExpressCheckoutResponseType setResponse = null;
                try {
                    setResponse = service.setExpressCheckout(setExpressCheckoutReq);
                } catch (Exception ex){
                    handleException(ex);
                }
                if(isSuccessAck(setResponse.getAck())){
                    paymentRequest.getWebPaymentInfo().setToken(setResponse.getToken());
                    paymentRequest.getWebPaymentInfo().setRedirectURL(redirectURL + REDIRECT_URL_PATH + setResponse.getToken());
                    paymentRequest.setStatus(PaymentStatus.UNCONFIRMED.toString());
                }else{
                    handleErrorResponse(setResponse);
                }
                return paymentRequest;
            }
        });
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
    public Promise<PaymentTransaction> getByTransactionToken(final String token) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest =
                        new GetExpressCheckoutDetailsRequestType(token);
                getExpressCheckoutDetailsRequest.setVersion(apiVersion);

                GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
                getExpressCheckoutDetailsReq.setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);
                GetExpressCheckoutDetailsResponseType responseType = null;
                try {
                    responseType = service.getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
                } catch (Exception ex){
                    handleException(ex);
                }
                PaymentTransaction transaction = new PaymentTransaction();
                if(isSuccessAck(responseType.getAck())){
                    transaction.setStatus(PaymentUtil.mapPayPalPaymentStatus(responseType
                            .getGetExpressCheckoutDetailsResponseDetails().getCheckoutStatus()).toString());
                }else{
                    handleErrorResponse(responseType);
                }
                return transaction;
            }
        });
    }

    private Promise<PaymentTransaction> handleErrorResponse(AbstractResponseType responseType) {
        StringBuffer sb = new StringBuffer();
        if(responseType.getErrors() != null){
            for(ErrorType error : responseType.getErrors()){
                sb.append(error.getLongMessage() + "&");
            }
        }
        LOGGER.error("paypal get error ack:" + responseType.getAck());
        LOGGER.error("paypal get error message:" + sb.toString());
        throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, sb.toString()).exception();
    }

    @Override
    public Promise<PaymentTransaction> confirm(final String transactionId, final PaymentTransaction paymentRequest){
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
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
                doExpressCheckoutPaymentRequestDetails.setToken(paymentRequest.getWebPaymentInfo().getToken());
                doExpressCheckoutPaymentRequestDetails.setPayerID(paymentRequest.getWebPaymentInfo().getPayerId());
                doExpressCheckoutPaymentRequestDetails.setPaymentDetails(paymentDetails);

                DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest =
                        new DoExpressCheckoutPaymentRequestType(doExpressCheckoutPaymentRequestDetails);
                doExpressCheckoutPaymentRequest.setVersion(apiVersion);

                DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
                doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);
                DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse = null;
                try {
                    doExpressCheckoutPaymentResponse = service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
                } catch (Exception ex){
                    handleException(ex);
                }
                if(isSuccessAck(doExpressCheckoutPaymentResponse.getAck())){
                    paymentRequest.setExternalToken(doExpressCheckoutPaymentResponse
                            .getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().get(0).getTransactionID());
                    paymentRequest.setStatus(PaymentStatus.SETTLED.toString());
                }else{
                    handleErrorResponse(doExpressCheckoutPaymentResponse);
                }
                return paymentRequest;
            }
        });
    }

    private void handleException(Exception e){
        if(e instanceof SocketTimeoutException){
            LOGGER.error("provider:" + PROVIDER_NAME + " gateway timeout exception: " + e.toString());
            throw AppServerExceptions.INSTANCE.providerGatewayTimeout(PROVIDER_NAME).exception();
        }else{
            LOGGER.error("provider:" + PROVIDER_NAME + " gateway exception: " + e.toString());
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
        }
    }

    private boolean isSuccessAck(AckCodeType ack){
        return ack.equals(AckCodeType.SUCCESS) || ack.equals(AckCodeType.SUCCESSWITHWARNING);
    }

    private String getFullReturnUrl(PaymentTransaction transaction){
        String returnURL = transaction.getWebPaymentInfo().getReturnURL();
        String transactionDetail = "paymentId=" + CommonUtil.encode(transaction.getId()) +
                "&billingId=" + CommonUtil.encode(transaction.getBillingRefId());
        if(returnURL.contains("?")){
            return returnURL + "&" + transactionDetail;
        }else{
            return returnURL + "?" + transactionDetail;
        }
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

}
