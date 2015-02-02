/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.ewallet;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.WalletId;
import com.junbo.ewallet.spec.def.WalletType;
import com.junbo.ewallet.spec.model.*;
import com.junbo.ewallet.spec.resource.WalletResource;
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions;
import com.junbo.identity.spec.v1.resource.CurrencyResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

/**
 * e-wallet provider service implementation.
 */
public class EWalletProviderServiceImpl extends AbstractPaymentProviderService {
    private static final String PROVIDER_NAME = "Wallet";
    private static final Logger LOGGER = LoggerFactory.getLogger(EWalletProviderServiceImpl.class);

    private WalletResource walletClient;
    private CurrencyResource currencyClient;
    public void setWalletClient(WalletResource walletClient) {
        this.walletClient = walletClient;
    }

    public void setCurrencyClient(CurrencyResource currencyClient) {
        this.currencyClient = currencyClient;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void clonePIResult(PaymentInstrument source, PaymentInstrument target) {
        if(source != null && !CommonUtil.isNullOrEmpty(source.getExternalToken())){
            target.setExternalToken(source.getExternalToken());
        }
        if(source != null && source.getTypeSpecificDetails() != null){
            if(target.getTypeSpecificDetails() == null){
                target.setTypeSpecificDetails(new TypeSpecificDetails());
            }
            target.getTypeSpecificDetails().setStoredValueBalance(source.getTypeSpecificDetails().getStoredValueBalance());
            target.getTypeSpecificDetails().setStoredValueCurrency(source.getTypeSpecificDetails().getStoredValueCurrency());
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
        validateWallet(request);
        Wallet wallet = new Wallet();
        wallet.setUserId(request.getUserId());
        wallet.setCurrency(request.getTypeSpecificDetails().getStoredValueCurrency());
        String walletType = request.getTypeSpecificDetails().getWalletType();
        wallet.setType(CommonUtil.isNullOrEmpty(walletType) ? WalletType.STORED_VALUE.toString() : walletType);
        return walletClient.postWallet(wallet).then(new Promise.Func<Wallet, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(Wallet wallet) {
                request.setExternalToken(wallet.getId().toString());
                return Promise.pure(request);
            }
        });
    }

    @Override
    public Promise<Response> delete(PaymentInstrument pi) {
        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentInstrument> getByInstrumentToken(final String token) {
        if(CommonUtil.isNullOrEmpty(token)){
            LOGGER.error("invalid external token: " + token);
            throw AppServerExceptions.INSTANCE.invalidPI().exception();
        }
        WalletId walletId = null;
        try{
            walletId = new WalletId(Long.parseLong(token));
        }catch (Exception ex){
            LOGGER.error("invalid external token: " + token, ex);
            throw AppServerExceptions.INSTANCE.invalidPI().exception();
        }
        return walletClient.getWallet(walletId).then(new Promise.Func<Wallet, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(Wallet wallet) {
                if (wallet == null) {
                    LOGGER.error("no such wallet:" + token);
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "No such wallet").exception();
                }
                final TypeSpecificDetails detail = new TypeSpecificDetails();
                detail.setStoredValueBalance(wallet.getBalance());
                detail.setStoredValueCurrency(wallet.getCurrency());
                PaymentInstrument pi = new PaymentInstrument();
                pi.setTypeSpecificDetails(detail);
                return Promise.pure(pi);
            }
        });

    }

    @Override
    public Promise<PaymentTransaction> credit(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        CreditRequest request = new CreditRequest();
        request.setTrackingUuid(UUID.randomUUID());
        request.setUserId(pi.getUserId());
        request.setCurrency(paymentRequest.getChargeInfo().getCurrency());
        request.setAmount(paymentRequest.getChargeInfo().getAmount());
        return walletClient.credit(request).then(new Promise.Func<Transaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Transaction transaction) {
                if (transaction == null || transaction.getId() == null) {
                    LOGGER.error("No credit wallet transaction for user:" + pi.getUserId());
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "No transaction happens").exception();
                }
                paymentRequest.setStatus(PaymentStatus.SETTLED.toString());
                return Promise.pure(paymentRequest);
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented(getProviderName() + "_authorize").exception();
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented(getProviderName() + "_authorize").exception();
    }

    @Override
    public Promise<PaymentTransaction> charge(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        DebitRequest debitRequest = new DebitRequest();
        debitRequest.setTrackingUuid(UUID.randomUUID());
        debitRequest.setAmount(paymentRequest.getChargeInfo().getAmount());
        if(pi.getTypeSpecificDetails() == null || pi.getTypeSpecificDetails().getStoredValueBalance() == null
                || pi.getTypeSpecificDetails().getStoredValueBalance().compareTo(debitRequest.getAmount()) < 0){
            throw AppClientExceptions.INSTANCE.insufficientBalance().exception();
        }
        return walletClient.debit(new WalletId(Long.parseLong(pi.getExternalToken())), debitRequest).then(new Promise.Func<Transaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Transaction transaction) {
                if (transaction == null || transaction.getId() == null) {
                    LOGGER.error("No debit wallet transaction:" + pi.getExternalToken());
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "No transaction happens").exception();
                }
                paymentRequest.setExternalToken(transaction.getId().toString());
                paymentRequest.setStatus(PaymentStatus.SETTLED.toString());
                return Promise.pure(paymentRequest);
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented(getProviderName() + "_authorize").exception();
    }

    @Override
    public Promise<PaymentTransaction> refund(String transactionId, final PaymentTransaction request) {
        if(request.getChargeInfo() == null || request.getChargeInfo().getAmount() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("amount").exception();
        }
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setTrackingUuid(UUID.randomUUID());
        refundRequest.setAmount(request.getChargeInfo().getAmount());
        refundRequest.setCurrency(request.getChargeInfo().getCurrency());
        Long walletTransactionId = null;
        try{
            walletTransactionId = Long.parseLong(transactionId);
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.noExternalTokenFoundForPayment(transactionId).exception();
        }
        if(walletTransactionId == null){
            throw AppServerExceptions.INSTANCE.noExternalTokenFoundForPayment(transactionId).exception();
        }
        return walletClient.refund(walletTransactionId, refundRequest).then(new Promise.Func<Transaction, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(Transaction transaction) {
                        if (transaction == null || transaction.getId() == null) {
                            throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, "No transaction happens").exception();
                        }
                        request.setStatus(PaymentStatus.REFUNDED.toString());
                        return Promise.pure(request);
                    }
                });
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented(
                getProviderName() + "_getByBillingRefId").exception();
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(PaymentTransaction request) {
        return Promise.pure(null);
    }

    private void validateWallet(PaymentInstrument request){
        if(request.getTypeSpecificDetails() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("currency").exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getTypeSpecificDetails().getStoredValueCurrency())){
            throw AppCommonErrors.INSTANCE.fieldRequired("currency").exception();
        }
        String strCur = request.getTypeSpecificDetails().getStoredValueCurrency();
        Currency currency = currencyClient.get(new CurrencyId(strCur), new CurrencyGetOptions()).get();
        if(currency == null){
            throw AppClientExceptions.INSTANCE.currencyNotFound(strCur).exception();
        }
    }
}
