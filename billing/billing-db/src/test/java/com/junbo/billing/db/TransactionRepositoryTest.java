/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import com.junbo.billing.db.repository.TransactionRepository;
import com.junbo.billing.spec.enums.TransactionStatus;
import com.junbo.billing.spec.enums.TransactionType;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.PaymentInstrumentId;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Created by xmchen on 14-3-12.
 */
public class TransactionRepositoryTest extends BaseTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void testSaveGet() {
        Long balanceId = idGeneratorFacade.nextId(BalanceId.class, generateUserId());
        Transaction transaction = generateTransaction(balanceId);
        transaction = transactionRepository.saveTransaction(transaction);
        Transaction returned = transactionRepository.getTransaction(transaction.getTransactionId().getValue());

        Assert.assertNotNull(returned.getTransactionId(), "Entity id should not be null.");
        Assert.assertEquals(returned.getPaymentRefId(), transaction.getPaymentRefId(),
                "Entity inserted and get should be same.");
    }

    private Transaction generateTransaction(Long balanceId) {
        Transaction transaction = new Transaction();
        transaction.setBalanceId(new BalanceId(balanceId));
        transaction.setPiId(new PaymentInstrumentId(idGeneratorFacade.nextId(PaymentInstrumentId.class, balanceId)));
        transaction.setCurrency("USD");
        transaction.setAmount(new BigDecimal("12.34"));
        transaction.setPaymentRefId("1234567890");
        transaction.setStatus(TransactionStatus.SUCCESS.name());
        transaction.setType(TransactionType.CHARGE.name());
        return transaction;
    }
}
