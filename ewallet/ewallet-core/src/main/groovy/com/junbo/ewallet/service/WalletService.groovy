package com.junbo.ewallet.service

import com.junbo.ewallet.spec.model.CreditRequest
import com.junbo.ewallet.spec.model.DebitRequest
import com.junbo.ewallet.spec.model.Transaction
import com.junbo.ewallet.spec.model.Wallet
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

    Wallet credit(Long walletId, CreditRequest creditRequest)

    Wallet debit(Long walletId, DebitRequest debitRequest)

    List<Transaction> getTransactions(Long walletId)

    Wallet getByTrackingUuid(UUID trackingUuid)
}