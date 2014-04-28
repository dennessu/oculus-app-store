/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.ewallet;

import com.junbo.common.id.WalletId;
import com.junbo.ewallet.spec.def.WalletType;
import com.junbo.ewallet.spec.model.DebitRequest;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.ewallet.spec.model.Wallet;
import com.junbo.ewallet.spec.resource.WalletResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.core.util.ProxyExceptionResponse;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * e-wallet provider service implementation.
 */
public class EWalletProviderServiceImpl extends AbstractPaymentProviderService {
    private static final String PROVIDER_NAME = "Wallet";
    private static final Logger LOGGER = LoggerFactory.getLogger(EWalletProviderServiceImpl.class);
    @Autowired
    private WalletResource walletClient;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void clonePIResult(PaymentInstrument source, PaymentInstrument target) {
        target.setExternalToken(source.getExternalToken());
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
        validateWallet(request);
        Wallet wallet = new Wallet();
        wallet.setUserId(request.getUserId());
        wallet.setCurrency(request.getTypeSpecificDetails().getWalletCurrency());
        String walletType = request.getTypeSpecificDetails().getWalletType();
        wallet.setType(CommonUtil.isNullOrEmpty(walletType) ? WalletType.STORED_VALUE.toString() : walletType);
        return walletClient.postWallet(wallet).recover(new Promise.Func<Throwable, Promise<Wallet>>() {
            @Override
            public Promise<Wallet> apply(Throwable throwable) {
                ProxyExceptionResponse proxyResponse = new ProxyExceptionResponse(throwable);
                LOGGER.error("add declined by " + getProviderName() +
                        "; error detail: " + proxyResponse.getBody());
                throw AppServerExceptions.INSTANCE.providerProcessError(
                        PROVIDER_NAME, proxyResponse.getBody()).exception();
            }
        }).then(new Promise.Func<Wallet, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(Wallet wallet) {
                request.setExternalToken(wallet.getWalletId().toString());
                return Promise.pure(request);
            }
        });
    }

    @Override
    public Promise<Response> delete(PaymentInstrument pi) {
        return Promise.pure(null);
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
    public Promise<PaymentTransaction> charge(PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        DebitRequest debitRequest = new DebitRequest();
        debitRequest.setAmount(paymentRequest.getChargeInfo().getAmount());
        return walletClient.debit(new WalletId(Long.parseLong(pi.getExternalToken())), debitRequest).
                recover(new Promise.Func<Throwable, Promise<Transaction>>() {
                    @Override
                    public Promise<Transaction> apply(Throwable throwable) {
                        ProxyExceptionResponse proxyResponse = new ProxyExceptionResponse(throwable);
                        LOGGER.error("charge declined by " + getProviderName() +
                                "; error detail: " + proxyResponse.getBody());
                        throw AppServerExceptions.INSTANCE.providerProcessError(
                                PROVIDER_NAME, proxyResponse.getBody()).exception();
                    }
                }).then(new Promise.Func<Transaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Transaction transaction) {
                if (transaction == null ||
                        transaction.getTransactionId() == null) {
                    throw AppServerExceptions.INSTANCE.providerProcessError(
                            PROVIDER_NAME, "No transaction happens").exception();
                }
                paymentRequest.setExternalToken(transaction.getTransactionId().toString());
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
    public Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented(getProviderName() + "_refund").exception();
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented(
                getProviderName() + "_getByBillingRefId").exception();
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(String token) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented(
                getProviderName() + "_getByTransactionToken").exception();
    }

    private void validateWallet(PaymentInstrument request){
        if(request.getTypeSpecificDetails() == null){
            throw AppClientExceptions.INSTANCE.missingCurrency().exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getTypeSpecificDetails().getWalletCurrency())){
            throw AppClientExceptions.INSTANCE.missingCurrency().exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getTypeSpecificDetails().getWalletCurrency())){
            throw AppClientExceptions.INSTANCE.missingWalletType().exception();
        }
    }
}
