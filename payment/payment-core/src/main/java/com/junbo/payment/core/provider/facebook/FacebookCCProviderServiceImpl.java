/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.facebook;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.clientproxy.PersonalInfoFacade;
import com.junbo.payment.clientproxy.facebook.*;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.db.repo.facade.PaymentInstrumentRepositoryFacade;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.internal.FacebookPaymentAccountMapping;
import com.junbo.payment.spec.model.Address;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Facebook Payment Service Impl.
 */
public class FacebookCCProviderServiceImpl extends AbstractPaymentProviderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookCCProviderServiceImpl.class);
    private static final String PROVIDER_NAME = "FacebookCC";
    private static final String TEST_ENV = "test";

    private String oculusAppId;
    private String env;
    private FacebookPaymentUtils facebookPaymentUtils;
    private FacebookPaymentApi facebookPaymentApi;
    private PersonalInfoFacade personalInfoFacade;
    private PaymentInstrumentRepositoryFacade piRepository;
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
                if(env.equalsIgnoreCase(TEST_ENV)){
                    fbPaymentAccount.setEnv(TEST_ENV);
                }
                return facebookPaymentApi.createAccount(s, oculusAppId, fbPaymentAccount).then(new Promise.Func<FacebookPaymentAccount, Promise<PaymentInstrument>>() {
                    @Override
                    public Promise<PaymentInstrument> apply(FacebookPaymentAccount fbPaymentAccount) {
                        String fbAccount = fbPaymentAccount.getId();
                        FacebookPaymentAccountMapping fbPaymentAccountMapping = new FacebookPaymentAccountMapping();
                        fbPaymentAccountMapping.setUserId(request.getUserId());
                        fbPaymentAccountMapping.setFbPaymentAccountId(fbAccount);
                        createFBPaymentAccountIfNotExist(fbPaymentAccountMapping);
                        return addCreditCard(accessToken, fbAccount, request, tokens);
                    }
                });
            }
        });
    }

    private Promise<PaymentInstrument> addCreditCard(String accessToken, String fbAccount,
                                                     final PaymentInstrument request, final String[] tokens) {
        FacebookCreditCard fbCreditCard = new FacebookCreditCard();
        fbCreditCard.setCcNumber(request.getAccountNumber());
        fbCreditCard.setCvv(request.getTypeSpecificDetails().getEncryptedCvmCode());
        fbCreditCard.setCardHolderName(request.getAccountName());
        fbCreditCard.setExpiryMonth(tokens[1]);
        fbCreditCard.setExpiryYear(tokens[0]);
        // Billing address
        Address address = null;
        if(request.getBillingAddressId() != null){
            address = personalInfoFacade.getBillingAddress(request.getBillingAddressId()).get();
            address.setId(request.getBillingAddressId());
        }
        if(address != null){
            fbCreditCard.setBillingAddress(getFacebookAddress(address, request));
        }
        return facebookPaymentApi.addCreditCard(accessToken, fbAccount, fbCreditCard).then(new Promise.Func<FacebookCreditCard, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(FacebookCreditCard facebookCreditCard) {
                //TODO: check the return result
                request.setExternalToken(facebookCreditCard.getId());
                request.getTypeSpecificDetails().setIssuerIdentificationNumber(facebookCreditCard.getFirst6());
                request.getTypeSpecificDetails().setExpireDate(facebookCreditCard.getExpiryYear() + "-" + facebookCreditCard.getExpiryMonth());
                request.setAccountNumber(facebookCreditCard.getLast4());
                return Promise.pure(request);
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
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                String fbPaymentAccount = getFacebookPaymentAccount(pi.getUserId());
                if(CommonUtil.isNullOrEmpty(fbPaymentAccount)){
                    LOGGER.error("not able to find facebook payment account for user:" + pi.getUserId());
                    throw AppServerExceptions.INSTANCE.invalidProviderAccount("").exception();
                }
                FacebookPayment fbPayment = new FacebookPayment();
                fbPayment.setCredential(piToken);
                fbPayment.setAction(FacebookPaymentActionType.authorize);
                fbPayment.setAmount(paymentRequest.getChargeInfo().getAmount());
                fbPayment.setCurrency(paymentRequest.getChargeInfo().getCurrency());
                fbPayment.setItemType(FacebookItemType.open_graph_product);
                FacebookItemDescription description = new FacebookItemDescription();
                description.setId("https://someog.com");
                description.setQuantity(1);
                description.setTitle(paymentRequest.getChargeInfo().getBusinessDescriptor());
                fbPayment.setItemDescription(description);
                fbPayment.setPayerIp(paymentRequest.getChargeInfo().getIpAddress());
                return facebookPaymentApi.addPayment(s, fbPaymentAccount, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(FacebookPayment fbPayment) {
                        //TODO: check the return result
                        paymentRequest.setExternalToken(fbPayment.getId());
                        paymentRequest.setStatus(PaymentStatus.AUTHORIZED.toString());
                        return Promise.pure(paymentRequest);
                    }
                });
            }
        });
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
                return facebookPaymentApi.modifyPayment(s, transactionId, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(FacebookPayment fbPayment) {
                        //TODO: check the return result
                        paymentRequest.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                        return Promise.pure(paymentRequest);
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
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                String fbPaymentAccount = getFacebookPaymentAccount(pi.getUserId());
                if(CommonUtil.isNullOrEmpty(fbPaymentAccount)){
                    LOGGER.error("not able to find facebook payment account for user:" + pi.getUserId());
                    throw AppServerExceptions.INSTANCE.invalidProviderAccount("").exception();
                }
                FacebookPayment fbPayment = new FacebookPayment();
                fbPayment.setCredential(piToken);
                fbPayment.setAction(FacebookPaymentActionType.charge);
                fbPayment.setAmount(paymentRequest.getChargeInfo().getAmount());
                fbPayment.setCurrency(paymentRequest.getChargeInfo().getCurrency());
                fbPayment.setItemType(FacebookItemType.open_graph_product);
                FacebookItemDescription description = new FacebookItemDescription();
                description.setId("https://someog.com");
                description.setQuantity(1);
                description.setTitle(paymentRequest.getChargeInfo().getBusinessDescriptor());
                fbPayment.setItemDescription(description);
                fbPayment.setPayerIp(paymentRequest.getChargeInfo().getIpAddress());
                return facebookPaymentApi.addPayment(s, fbPaymentAccount, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(FacebookPayment fbPayment) {
                        //TODO: check the return result
                        paymentRequest.setExternalToken(fbPayment.getId());
                        paymentRequest.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                        return Promise.pure(paymentRequest);
                    }
                });
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> reverse(final String transactionId, final PaymentTransaction paymentRequest) {
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                FacebookPayment fbPayment = new FacebookPayment();
                fbPayment.setAction(FacebookPaymentActionType.cancel);
                return facebookPaymentApi.modifyPayment(s, transactionId, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(FacebookPayment fbPayment) {
                        //TODO: check the return result
                        paymentRequest.setStatus(PaymentStatus.REVERSED.toString());
                        return Promise.pure(paymentRequest);
                    }
                });
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> refund(final String transactionId, final PaymentTransaction paymentRequest) {
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(String s) {
                FacebookPayment fbPayment = new FacebookPayment();
                fbPayment.setAction(FacebookPaymentActionType.refund);
                if(paymentRequest.getChargeInfo() != null){
                    fbPayment.setAmount(paymentRequest.getChargeInfo().getAmount());
                    fbPayment.setCurrency(paymentRequest.getChargeInfo().getCurrency());
                    fbPayment.setRefundReason(paymentRequest.getChargeInfo().getBusinessDescriptor());
                }
                return facebookPaymentApi.modifyPayment(s, transactionId, fbPayment).then(new Promise.Func<FacebookPayment, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(FacebookPayment fbPayment) {
                        //TODO: check the return result
                        paymentRequest.setStatus(PaymentStatus.REFUNDED.toString());
                        return Promise.pure(paymentRequest);
                    }
                });
            }
        });
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(PaymentTransaction paymentRequest) {
        return null;
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

    protected String getFacebookPaymentAccount(final Long userId){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<String>() {
            public String doInTransaction(TransactionStatus txnStatus) {
                return piRepository.getFacebookPaymentAccount(userId);
            }
        });
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
    public void setFacebookPaymentApi(FacebookPaymentApi facebookPaymentApi) {
        this.facebookPaymentApi = facebookPaymentApi;
    }
    @Required
    public void setPiRepository(PaymentInstrumentRepositoryFacade piRepository) {
        this.piRepository = piRepository;
    }
}
