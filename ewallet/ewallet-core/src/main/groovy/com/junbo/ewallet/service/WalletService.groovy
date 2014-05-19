package com.junbo.ewallet.service

import com.junbo.common.id.UserId
import com.junbo.ewallet.spec.model.*
import groovy.transform.CompileStatic

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

/**
 * interface of wallet service.
 */
@CompileStatic
interface WalletService {
    Wallet get(Long walletId)

    Wallet add(Wallet wallet)

    Wallet update(Long walletId, Wallet wallet)

    Transaction credit(CreditRequest creditRequest)

    Transaction debit(Long walletId, DebitRequest debitRequest)

    List<Transaction> getTransactions(Long walletId)

    Wallet getWalletByTrackingUuid(Long shardMasterId, UUID trackingUuid)

    Transaction getTransactionByTrackingUuid(Long shardMasterId, UUID trackingUuid)

    List<Wallet> getWallets(UserId userId)

    Transaction refund(Long transactionId, RefundRequest refundRequest)
}