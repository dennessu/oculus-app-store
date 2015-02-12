/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.facebook;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.clientproxy.FacebookGatewayService;
import com.junbo.payment.clientproxy.PersonalInfoFacade;
import com.junbo.payment.clientproxy.facebook.*;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.db.repo.PaymentProviderIdMappingRepository;
import com.junbo.payment.db.repo.facade.PaymentInstrumentRepositoryFacade;
import com.junbo.payment.spec.enums.FacebookRefundReason;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.internal.FacebookPaymentAccountMapping;
import com.junbo.payment.spec.internal.FacebookPaymentType;
import com.junbo.payment.spec.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Facebook Payment Service Impl.
 */
public class FacebookCCProviderServiceImpl extends AbstractPaymentProviderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookCCProviderServiceImpl.class);
    private static final String Local_IP = "127.0.0.1";
    private static final String PROVIDER_NAME = "FacebookCC";
    private static final String TEST_ENV = "test";
    private static final String FB_VERIFY_TOKEN = "hub.verify_token";
    private static final String FB_RISK_RTU_FIELD = "fraud_status";
    private static final String FB_RISK_RTU_REJECT = "blocked_after_pend";
    private static final String FB_RISK_PENDING = "pending";

    private String oculusAppId;
    private String env;
    private String rtuVerifyToken;
    private String appSecret;
    private String needSignVerify;
    private FacebookPaymentUtils facebookPaymentUtils;
    private FacebookGatewayService facebookGatewayService;
    private PersonalInfoFacade personalInfoFacade;
    private PaymentInstrumentRepositoryFacade piRepository;
    private PaymentProviderIdMappingRepository paymentProviderIdMappingRepository;
    @Autowired
    protected PlatformTransactionManager transactionManager;
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void clonePIResult(PaymentInstrument source, PaymentInstrument target) {
        target.setAccountNumber(source.getAccountNumber());
        if(source != null && !CommonUtil.isNullOrEmpty(source.getExternalToken())){
            target.setExternalToken(source.getExternalToken());
        }
        if(source != null && source.getTypeSpecificDetails() != null){
            if(target.getTypeSpecificDetails() == null){
                target.setTypeSpecificDetails(new TypeSpecificDetails());
            }
            target.getTypeSpecificDetails().setIssuerIdentificationNumber(source.getTypeSpecificDetails().getIssuerIdentificationNumber());
            target.getTypeSpecificDetails().setExpireDate(source.getTypeSpecificDetails().getExpireDate());
        }
    }

    @Override
    public void cloneTransactionResult(PaymentTransaction source, PaymentTransaction target) {
        if(!CommonUtil.isNullOrEmpty(source.getExternalToken())){
            target.setExternalToken(source.getExternalToken());
        }
        if(!CommonUtil.isNullOrEmpty(source.getStatus())){
            target.setStatus(source.getStatus());
        }
    }

    @Override
    public Promise<PaymentInstrument> add(final PaymentInstrument request) {
        validateFacebookCC(request);
        final String[] tokens = request.getTypeSpecificDetails().getExpireDate().split("-");
        if (tokens == null || tokens.length < 2) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("expire_date",
                    "only accept format: yyyy-MM or yyyy-MM-dd").exception();
        }
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(String s) {
                final String accessToken = s;
                String existingAccount = getFacebookPaymentAccount(request.getUserId());
                if(!CommonUtil.isNullOrEmpty(existingAccount)){
                    return addCreditCard(accessToken, existingAccount, request, tokens);
                }
                FacebookPaymentAccount fbPaymentAccount = new FacebookPaymentAccount();
                fbPaymentAccount.setPayerId(request.getUserId().toString());
                fbPaymentAccount.setPayerEmail(new FacebookEmail(request.getUserInfo().getEmail()));
                if(TEST_ENV.equalsIgnoreCase(env)){
                    fbPaymentAccount.setEnv(TEST_ENV);
                }
                return facebookGatewayService.createAccount(s, oculusAppId, fbPaymentAccount).then(new Promise.Func<FacebookPaymentAccount, Promise<PaymentInstrument>>() {
                    @Override
                    public Promise<PaymentInstrument> apply(FacebookPaymentAccount fbPaymentAccount) {
                        if(!CommonUtil.isNullOrEmpty(fbPaymentAccount.getId())){
                            String fbAccount = fbPaymentAccount.getId();
                            FacebookPaymentAccountMapping fbPaymentAccountMapping = new FacebookPaymentAccountMapping();
                            fbPaymentAccountMapping.setUserId(request.getUserId());
                            fbPaymentAccountMapping.setFbPaymentAccountId(fbAccount);
                            createFBPaymentAccountIfNotExist(fbPaymentAccountMapping);
                            return addCreditCard(accessToken, fbAccount, request, tokens);
                        }else if(fbPaymentAccount.getError() != null){
                            handlePaymentError(fbPaymentAccount.getError());
                            return Promise.pure(null);
                        }else{
                            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "unknow server error").exception();
                        }
                    }
                });
            }
        });
    }

    private Promise<PaymentInstrument> addCreditCard(final String accessToken, String fbAccount,
                                                     final PaymentInstrument request, final String[] tokens) {
        FacebookCreditCard fbCreditCard = new FacebookCreditCard();
        fbCreditCard.setToken(request.getAccountNumber());
        fbCreditCard.setCardHolderName(request.getAccountName());
        fbCreditCard.setExpiryMonth(tokens[1]);
        fbCreditCard.setExpiryYear(tokens[0]);
        fbCreditCard.setPayerEmail(new FacebookEmail(request.getUserInfo().getEmail()));
        String ipAddress = request.getIpAddress();
        fbCreditCard.setPayerIp(CommonUtil.isNullOrEmpty(ipAddress)? Local_IP : ipAddress);
        //Risk Feature
        if(request.getRiskFeature() != null){
            FacebookRiskFeature fbRiskFeature = new FacebookRiskFeature();
            fbRiskFeature.setBrowserName(request.getRiskFeature().getBrowserName());
            fbRiskFeature.setBrowserVersion(request.getRiskFeature().getBrowserVersion());
            fbRiskFeature.setCurrencyPurchasing(request.getRiskFeature().getCurrencyPurchasing());
            fbRiskFeature.setInstalledApps(request.getRiskFeature().getInstalledApps());
            fbRiskFeature.setPlatformName(request.getRiskFeature().getPlatformName());
            fbRiskFeature.setPlatformVersion(request.getRiskFeature().getPlatformVersion());
            fbRiskFeature.setSourceCountry(request.getRiskFeature().getSourceCountry());
            fbRiskFeature.setSourceDatr(request.getRiskFeature().getSourceDatr());
            fbRiskFeature.setTimeSinceUserAccountCreatedInDays(request.getRiskFeature().getTimeSinceUserAccountCreatedInDays());
            fbCreditCard.setRiskFeature(fbRiskFeature);
        }
        // Billing address
        Address address = null;
        if(request.getBillingAddressId() != null){
            address = personalInfoFacade.getBillingAddress(request.getBillingAddressId()).get();
            address.setId(request.getBillingAddressId());
        }
        if(address != null){
            fbCreditCard.setBillingAddress(getFacebookAddress(address, request));
        }
        /*
        //Test Use
        FacebookAddress fbAddress = new FacebookAddress();
        fbAddress.setZip("12345");
        fbAddress.setCountryCode("US");
        fbAddress.setCity("MENLO Park");
        fbAddress.setState("CA");
        fbCreditCard.setBillingAddress(fbAddress);
        */
        return facebookGatewayService.batchAddAndGetCreditCard(accessToken, fbAccount, fbCreditCard).then(new Promise.Func<FacebookCreditCard, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(FacebookCreditCard facebookCreditCard) {
                if(!CommonUtil.isNullOrEmpty(facebookCreditCard.getId())){
                    request.setExternalToken(facebookCreditCard.getId());
                    request.getTypeSpecificDetails().setIssuerIdentificationNumber(facebookCreditCard.getFirst6());
                    request.getTypeSpecificDetails().setExpireDate(facebookCreditCard.getExpiryYear() + "-" + facebookCreditCard.getExpiryMonth());
                    request.setAccountNumber(facebookCreditCard.getLast4());
                    request.getTypeSpecificDetails().setCreditCardType(facebookCreditCard.getCardType());
                    return Promise.pure(request);
                }else if(facebookCreditCard.getError() != null){
                    handlePaymentError(facebookCreditCard.getError());
                    return Promise.pure(null);
                }else{
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "unknown server error").exception();
                }
            }
        });
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
    public Promise<PaymentTransaction> authorize(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        final String piToken = pi.getExternalToken();
        if(CommonUtil.isNullOrEmpty(piToken)){
            LOGGER.error("not able to find external token for pi:" + pi.getId());
            throw AppServerExceptions.INSTANCE.noExternalTokenFoundForPayment(pi.getId().toString()).exception();
        }
        if(CommonUtil.isNullOrEmpty(paymentRequest.getChargeInfo().getPaymentType())){
            //TODO: should throw exception. Please uncommnt when billing finish the job
            //LOGGER.error("payment type needed for facebook.");
            //throw AppCommonErrors.INSTANCE.fieldRequired("payment_type").exception();
            paymentRequest.getChargeInfo().setPaymentType(FacebookPaymentType.digital.toString());
        }
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                String fbPaymentAccount = getFacebookPaymentAccount(pi.getUserId());
                if(CommonUtil.isNullOrEmpty(fbPaymentAccount)){
                    LOGGER.error("not able to find facebook payment account for user:" + pi.getUserId());
                    throw AppServerExceptions.INSTANCE.invalidProviderAccount("").exception();
                }
                FacebookPayment fbPayment = getFacebookPaymentRequest(paymentRequest, piToken, FacebookPaymentActionType.authorize);
                final String accessToken = s;
                return facebookGatewayService.batchAddAndGetPayment(accessToken, fbPaymentAccount, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(final FacebookPayment fbPayment) {
                        return handleFacebookPayment(fbPayment, paymentRequest, FacebookPaymentActionType.authorize);
                    }
                });
            }
        });
    }

    private Promise<PaymentTransaction> handleFacebookPayment(FacebookPayment fbPayment, PaymentTransaction paymentRequest, FacebookPaymentActionType actionType){
        if(!CommonUtil.isNullOrEmpty(fbPayment.getId())){
            paymentRequest.setExternalToken(fbPayment.getId());
            //TODO: we could remove the mapping now as the request_id would be fetched from the getPaymentDetail()
            /*
            PaymentProviderIdMapping mapping = new PaymentProviderIdMapping();
            mapping.setExternalId(fbPayment.getId());
            mapping.setPaymentId(paymentRequest.getId());
            paymentProviderIdMappingRepository.create(mapping).get();
            */
            switch (actionType){
                case authorize:
                    paymentRequest.setStatus(PaymentStatus.AUTHORIZED.toString());
                    break;
                case charge:
                    paymentRequest.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                    break;
                default:
                    throw AppServerExceptions.INSTANCE.serviceNotImplemented(actionType.toString()).exception();
            }
            //if risk pending the set the payment status as pending
            if(fbPayment.getActions() != null){
                for(FacebookPaymentAction action : fbPayment.getActions()){
                    if(!CommonUtil.isNullOrEmpty(action.getRequestId()) && action.getRisk() != null && action.getRisk().getFraudStatus().equals(FacebookFraudStatus.pending)){
                        paymentRequest.setStatus(PaymentStatus.RISK_PENDING.toString());
                        break;
                    }
                }
            }
            return Promise.pure(paymentRequest);
        }else if(fbPayment.getError() != null){
            handlePaymentError(fbPayment.getError());
            return Promise.pure(null);
        }else{
            LOGGER.error("error response:" + fbPayment.getError());
            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "unknow server error").exception();
        }
    }

    @Override
    public Promise<PaymentTransaction> capture(final String transactionId, final PaymentTransaction paymentRequest) {
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                FacebookPayment fbPayment = new FacebookPayment();
                fbPayment.setAction(FacebookPaymentActionType.capture);
                if(paymentRequest.getChargeInfo() != null){
                    fbPayment.setAmount(paymentRequest.getChargeInfo().getAmount());
                    fbPayment.setCurrency(paymentRequest.getChargeInfo().getCurrency());
                }
                return facebookGatewayService.modifyPayment(s, transactionId, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(FacebookPayment fbPayment) {
                        if(fbPayment.getSuccess()){
                            paymentRequest.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                            return Promise.pure(paymentRequest);
                        }else if(fbPayment.getError() != null){
                            handlePaymentError(fbPayment.getError());
                            return Promise.pure(null);
                        }else{
                            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "unknow server error").exception();
                        }
                    }
                });
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> charge(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        final String piToken = pi.getExternalToken();
        if(CommonUtil.isNullOrEmpty(piToken)){
            LOGGER.error("not able to find external token for pi:" + pi.getId());
            throw AppServerExceptions.INSTANCE.noExternalTokenFoundForPayment(pi.getId().toString()).exception();
        }
        if(CommonUtil.isNullOrEmpty(paymentRequest.getChargeInfo().getPaymentType())){
            //TODO: uncomment after billing finish the code
            //LOGGER.error("payment type needed for facebook.");
            //throw AppCommonErrors.INSTANCE.fieldRequired("payment_type").exception();
            paymentRequest.getChargeInfo().setPaymentType(FacebookPaymentType.digital.toString());
        }
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                String fbPaymentAccount = getFacebookPaymentAccount(pi.getUserId());
                if(CommonUtil.isNullOrEmpty(fbPaymentAccount)){
                    LOGGER.error("not able to find facebook payment account for user:" + pi.getUserId());
                    throw AppServerExceptions.INSTANCE.invalidProviderAccount("").exception();
                }
                FacebookPayment fbPayment = getFacebookPaymentRequest(paymentRequest, piToken, FacebookPaymentActionType.charge);
                final String accessToken = s;
                return facebookGatewayService.batchAddAndGetPayment(accessToken, fbPaymentAccount, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(final FacebookPayment fbPayment) {
                        return handleFacebookPayment(fbPayment, paymentRequest, FacebookPaymentActionType.charge);
                    }
                });
            }
        });
    }

    private FacebookPayment getFacebookPaymentRequest(PaymentTransaction paymentRequest, String piToken, FacebookPaymentActionType action) {
        FacebookPayment fbPayment = new FacebookPayment();
        fbPayment.setRequestId(paymentRequest.getId().toString() + "_" + paymentRequest.getBillingRefId());
        fbPayment.setCredentialId(piToken);
        fbPayment.setAction(action);
        fbPayment.setAmount(paymentRequest.getChargeInfo().getAmount());
        fbPayment.setCurrency(paymentRequest.getChargeInfo().getCurrency());
        fbPayment.setItemType(FacebookItemType.oculus_launch_v1);
        FacebookItemDescription description = new FacebookItemDescription(getPaymentEntity(paymentRequest.getMerchantAccount()),
                getPaymentType(paymentRequest.getChargeInfo().getPaymentType()));
        List<FacebookPaymentDescriptionItem> fbPaymentItems = new ArrayList<>();
        if(paymentRequest.getChargeInfo().getItems() != null){
            for(Item descItem : paymentRequest.getChargeInfo().getItems()){
                FacebookPaymentDescriptionItem fbItem = new FacebookPaymentDescriptionItem();
                fbItem.setOfferId(descItem.getId());
                fbItem.setOfferName(descItem.getName());
                fbPaymentItems.add(fbItem);
            }
        }
        FacebookPaymentDescriptionItem[] itemArrays = new FacebookPaymentDescriptionItem[fbPaymentItems.size()];
        description.setItems(fbPaymentItems.toArray(itemArrays));
        fbPayment.setItemDescription(description);

        String ipAddress = paymentRequest.getChargeInfo().getIpAddress();
        if(CommonUtil.isNullOrEmpty(ipAddress)){
            ipAddress = Local_IP;
        }
        fbPayment.setPayerIp(ipAddress);
        //Risk Feature
        fbPayment.setRiskFeature(getFBRiskFeature(paymentRequest));
        return fbPayment;
    }

    @Override
    public Promise<PaymentTransaction> reverse(final String transactionId, final PaymentTransaction paymentRequest) {
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                FacebookPayment fbPayment = new FacebookPayment();
                fbPayment.setAction(FacebookPaymentActionType.cancel);
                return facebookGatewayService.modifyPayment(s, transactionId, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(FacebookPayment fbPayment) {
                        if (fbPayment.getSuccess()) {
                            paymentRequest.setStatus(PaymentStatus.REVERSED.toString());
                            return Promise.pure(paymentRequest);
                        } else if (fbPayment.getError() != null) {
                            handlePaymentError(fbPayment.getError());
                            return Promise.pure(null);
                        } else {
                            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "unknow server error").exception();
                        }
                    }
                });
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> refund(final String transactionId, final PaymentTransaction paymentRequest) {
        FacebookRefundReason facebookRefundReason = FacebookRefundReason.not_specified;
        if(!CommonUtil.isNullOrEmpty(paymentRequest.getChargeInfo().getBusinessDescriptor())){
            try{
                facebookRefundReason = FacebookRefundReason.valueOf(paymentRequest.getChargeInfo().getBusinessDescriptor());
            }catch (Exception ex){
                LOGGER.error("invalid refund reason:" + paymentRequest.getChargeInfo().getBusinessDescriptor(), ex);
                throw AppCommonErrors.INSTANCE.fieldInvalid("refund_reason").exception();
            }
        }
        final FacebookRefundReason refundReason = facebookRefundReason;
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                FacebookPayment fbPayment = new FacebookPayment();
                fbPayment.setAction(FacebookPaymentActionType.refund);
                if(paymentRequest.getChargeInfo() != null){
                    fbPayment.setAmount(paymentRequest.getChargeInfo().getAmount());
                    fbPayment.setCurrency(paymentRequest.getChargeInfo().getCurrency());
                    fbPayment.setRefundReason(refundReason.toString());
                }
                return facebookGatewayService.modifyPayment(s, transactionId, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(FacebookPayment fbPayment) {
                        if(fbPayment.getSuccess()){
                            paymentRequest.setStatus(PaymentStatus.REFUNDED.toString());
                            return Promise.pure(paymentRequest);
                        }else if(fbPayment.getError() != null){
                            handlePaymentError(fbPayment.getError());
                            return Promise.pure(null);
                        }else{
                            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "unknow server error").exception();
                        }
                    }
                });
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> processNotify(final String rawRequest){
        if(CommonUtil.isNullOrEmpty(rawRequest)){
            throw AppCommonErrors.INSTANCE.fieldRequired("request").exception();
        }
        final String request = CommonUtil.urlDecode(rawRequest);
        //check it is a Get verify or a RTU post:
        if(rawRequest.contains(FB_VERIFY_TOKEN)){
            String[] requests = request.split("&");
            for(String field : requests){
                String[] results = field.split("=");
                if(results[0].equalsIgnoreCase(FB_VERIFY_TOKEN)){
                    String requestToken = results.length > 1 ? field.replace(results[0] + "=", "") : "";
                    if(requestToken.equalsIgnoreCase(rtuVerifyToken)){
                        return Promise.pure(null);
                    }
                }
            }
            LOGGER.error("verify token check failed:" + rawRequest);
            throw AppServerExceptions.INSTANCE.unAuthorized(request).exception();
        }
        //Post RTU
        if ("true".equalsIgnoreCase(needSignVerify) && !checkSign(request)) {
            LOGGER.error("verify sign failed:" + rawRequest);
            throw AppServerExceptions.INSTANCE.unAuthorized(request).exception();
        }
        final FacebookRTU facebookRTU = CommonUtil.parseJsonIgnoreUnknown(request, FacebookRTU.class);
        if(facebookRTU == null || !"payments".equals(facebookRTU.getObject()) || facebookRTU.getEntry() == null || facebookRTU.getEntry().length < 0){
            return Promise.pure(null);
        }
        if(!Arrays.asList(facebookRTU.getEntry()[0].getChanged_fields()).contains(FB_RISK_RTU_FIELD)){
            return Promise.pure(null);
        }
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                return facebookGatewayService.getPayment(s, facebookRTU.getEntry()[0].getId()).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(FacebookPayment facebookPayment) {
                        if(facebookPayment != null && facebookPayment.getActions() != null){
                            for(FacebookPaymentAction action : facebookPayment.getActions()){
                                if(!CommonUtil.isNullOrEmpty(action.getRequestId())){
                                    PaymentTransaction transaction = new PaymentTransaction();
                                    //request id is always "paymentid_billingrefid"
                                    transaction.setId(Long.parseLong(action.getRequestId().split("_")[0]));
                                    if(action.getRisk() != null && FacebookFraudStatus.blocked_after_pend.equals(action.getRisk().getFraudStatus())){
                                        transaction.setStatus(PaymentStatus.RISK_ASYNC_REJECT.toString());
                                    }else if(action.getRisk() != null && FacebookFraudStatus.released_after_pend.equals(action.getRisk().getFraudStatus())){
                                        transaction.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                                    }else{
                                        return Promise.pure(null);
                                    }
                                    return Promise.pure(transaction);
                                }
                            }
                        }
                        return Promise.pure(null);
                    }
                });
            }
        });
        //TODO: remove it as now it returns directly in the payment details
        /*
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                return facebookGatewayService.getPaymentRisk(s, facebookRTU.getEntry()[0].getId()).
                        then(new Promise.Func<FacebookRiskPayment, Promise<PaymentTransaction>>() {
                            @Override
                            public Promise<PaymentTransaction> apply(FacebookRiskPayment facebookRiskPayment) {
                                PaymentTransaction transaction = new PaymentTransaction();
                                PaymentProviderIdMapping mapping = paymentProviderIdMappingRepository.get(facebookRTU.getEntry()[0].getId()).get();
                                transaction.setId(mapping.getPaymentId());
                                if(facebookRiskPayment != null && FacebookFraudStatus.blocked_after_pend.name().equalsIgnoreCase(facebookRiskPayment.getFraud_status())){
                                    transaction.setStatus(PaymentStatus.RISK_ASYNC_REJECT.toString());
                                }else if(facebookRiskPayment != null && FacebookFraudStatus.released_after_pend.name().equalsIgnoreCase(facebookRiskPayment.getFraud_status())){
                                    transaction.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                                }else{
                                    return Promise.pure(null);
                                }
                                return Promise.pure(transaction);
                            }
                        });
            }
        });*/
    }

    private boolean checkSign(String request){
        String authHeader = JunboHttpContext.getRequestHeaders().getFirst("X-Hub-Signature");
        if (!CommonUtil.isNullOrEmpty(authHeader)){
            if(!authHeader.startsWith("sha1=")){
                LOGGER.error("invalid authorize header format:" + authHeader);
                throw AppCommonErrors.INSTANCE.headerRequired("authorization").exception();
            }
            String calculated = CommonUtil.calHMCASHA1Hex(request, appSecret);
            if(!authHeader.replace("sha1=", "").equals(calculated)){
                LOGGER.error("invalid authorize header:" + authHeader);
                throw AppCommonErrors.INSTANCE.headerRequired("authorization").exception();
            }
        }else{
            LOGGER.error("missing authorize header");
            throw AppCommonErrors.INSTANCE.headerRequired("authorization").exception();
        }
        return true;
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(PaymentTransaction paymentRequest) {
        return Promise.pure(null);
    }
    private void validateFacebookCC(PaymentInstrument request){
        if(request.getTypeSpecificDetails() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("expire_date").exception();
        }
        String expireDate = request.getTypeSpecificDetails().getExpireDate();
        if(CommonUtil.isNullOrEmpty(expireDate)){
            throw AppCommonErrors.INSTANCE.fieldRequired("expire_date").exception();
        }
    }

    private FacebookAddress getFacebookAddress(Address address, PaymentInstrument request) {
        FacebookAddress fbAddress = new FacebookAddress();
        fbAddress.setZip(address.getPostalCode());
        fbAddress.setCountryCode(address.getCountry());
        fbAddress.setCity(address.getCity());
        fbAddress.setState(address.getState());
        fbAddress.setStreet1(address.getAddressLine1());
        fbAddress.setStreet2(address.getAddressLine2());
        fbAddress.setStreet3(address.getAddressLine3());
        fbAddress.setFirstName(request.getUserInfo().getFirstName());
        fbAddress.setLastName(request.getUserInfo().getLastName());
        return fbAddress;
    }

    protected String createFBPaymentAccountIfNotExist(final FacebookPaymentAccountMapping model){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<String>() {
            public String doInTransaction(TransactionStatus txnStatus) {
                return piRepository.createFBPaymentAccountIfNotExist(model);
            }
        });
    }

    private void handlePaymentError(FacebookCCErrorDetail facebookCCErrorDetail){
        LOGGER.error("error response:" + facebookCCErrorDetail);
        if(facebookCCErrorDetail != null){
            if(FacebookErrorMapUtil.isClientError(facebookCCErrorDetail)){
                throw AppClientExceptions.INSTANCE.providerInvalidRequest(PROVIDER_NAME, facebookCCErrorDetail.getMessage()).exception();
            }else{
                throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, facebookCCErrorDetail.getMessage()).exception();
            }
        }
    }

    protected String getFacebookPaymentAccount(final Long userId){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<String>() {
            public String doInTransaction(TransactionStatus txnStatus) {
                return piRepository.getFacebookPaymentAccount(userId);
            }
        });
    }

    private FacebookPaymentType getPaymentType(String type){
        try{
            FacebookPaymentType paymentType = FacebookPaymentType.valueOf(type);
            return paymentType;
        }catch (Exception ex){
            throw AppCommonErrors.INSTANCE.fieldInvalid("payment_type").exception();
        }
    }

    private FacebookPaymentEntity getPaymentEntity(String merchantAccount){
        try{
            FacebookPaymentEntity paymentEntity = FacebookPaymentEntity.valueOf(merchantAccount);
            return paymentEntity;
        }catch (Exception ex){
            throw AppCommonErrors.INSTANCE.fieldInvalid("payment_entity").exception();
        }
    }

    private FacebookRiskFeature getFBRiskFeature(PaymentTransaction request){
        if(request.getRiskFeature() != null){
            FacebookRiskFeature fbRiskFeature = new FacebookRiskFeature();
            fbRiskFeature.setBrowserName(request.getRiskFeature().getBrowserName());
            fbRiskFeature.setBrowserVersion(request.getRiskFeature().getBrowserVersion());
            fbRiskFeature.setCurrencyPurchasing(request.getRiskFeature().getCurrencyPurchasing());
            fbRiskFeature.setInstalledApps(request.getRiskFeature().getInstalledApps());
            fbRiskFeature.setPlatformName(request.getRiskFeature().getPlatformName());
            fbRiskFeature.setPlatformVersion(request.getRiskFeature().getPlatformVersion());
            fbRiskFeature.setSourceCountry(request.getRiskFeature().getSourceCountry());
            fbRiskFeature.setSourceDatr(request.getRiskFeature().getSourceDatr());
            fbRiskFeature.setTimeSinceUserAccountCreatedInDays(request.getRiskFeature().getTimeSinceUserAccountCreatedInDays());
            return fbRiskFeature;
        }
        return null;
    }


    @Required
    public void setNeedSignVerify(String needSignVerify) {
        this.needSignVerify = needSignVerify;
    }

    @Required
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
    @Required
    public void setOculusAppId(String oculusAppId) {
        this.oculusAppId = oculusAppId;
    }
    public void setEnv(String env) {
        this.env = env;
    }
    @Required
    public void setFacebookPaymentUtils(FacebookPaymentUtils facebookPaymentUtils) {
        this.facebookPaymentUtils = facebookPaymentUtils;
    }
    @Required
    public void setPersonalInfoFacade(PersonalInfoFacade personalInfoFacade) {
        this.personalInfoFacade = personalInfoFacade;
    }
    @Required
    public void setFacebookGatewayService(FacebookGatewayService facebookGatewayService) {
        this.facebookGatewayService = facebookGatewayService;
    }
    @Required
    public void setPiRepository(PaymentInstrumentRepositoryFacade piRepository) {
        this.piRepository = piRepository;
    }
    public void setRtuVerifyToken(String rtuVerifyToken) {
        this.rtuVerifyToken = rtuVerifyToken;
    }
    @Required
    public void setPaymentProviderIdMappingRepository(PaymentProviderIdMappingRepository paymentProviderIdMappingRepository) {
        this.paymentProviderIdMappingRepository = paymentProviderIdMappingRepository;
    }
}
