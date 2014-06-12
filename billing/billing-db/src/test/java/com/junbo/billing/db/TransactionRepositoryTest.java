/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import com.junbo.billing.db.repo.facade.TransactionRepositoryFacade;
import com.junbo.billing.spec.enums.TransactionStatus;
import com.junbo.billing.spec.enums.TransactionType;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.PaymentInstrumentId;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xmchen on 14-3-12.
 */
public class TransactionRepositoryTest extends BaseTest {

    @Autowired
    private TransactionRepositoryFacade transactionRepositoryFacade;


    @Test
    public void testSaveGet() {
        Long balanceId = idGenerator.nextId(generateUserId());

        Transaction transaction = generateTransaction(balanceId);
        transaction = transactionRepositoryFacade.saveTransaction(transaction);
        Transaction returned = transactionRepositoryFacade.getTransaction(transaction.getId().getValue());

        Assert.assertNotNull(returned.getId(), "Entity id should not be null.");
        Assert.assertEquals(returned.getPaymentRefId(), transaction.getPaymentRefId(),
                "Entity inserted and get should be same.");
    }

    private Transaction generateTransaction(Long balanceId) {
        Transaction transaction = new Transaction();
        transaction.setBalanceId(new BalanceId(balanceId));
        transaction.setPiId(new PaymentInstrumentId(idGenerator.nextId(balanceId)));
        transaction.setCurrency("USD");
        transaction.setAmount(new BigDecimal("12.34"));
        transaction.setPaymentRefId("1234567890");
        transaction.setStatus(TransactionStatus.SUCCESS.name());
        transaction.setType(TransactionType.CHARGE.name());
        transaction.setCreatedBy(balanceId);
        transaction.setCreatedTime(new Date());
        return transaction;
    }

}
