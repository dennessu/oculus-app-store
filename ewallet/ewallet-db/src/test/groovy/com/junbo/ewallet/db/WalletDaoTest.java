/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db;

import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.spec.def.Currency;
import com.junbo.ewallet.spec.def.Status;
import com.junbo.ewallet.spec.def.WalletType;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Test for WalletDao.
 */
public class WalletDaoTest extends BaseTest {
    @Autowired
    private WalletDao walletDao;
    @Autowired
    private WalletLotDao walletLotDao;

    @Test
    public void testCreateWallet() {
        WalletEntity walletEntity = buildAWallet();
        WalletEntity insertedWallet = walletDao.insert(walletEntity);
        Assert.assertNotNull(insertedWallet.getId());
        Assert.assertEquals(walletEntity.getBalance(), insertedWallet.getBalance());
    }

    @Test
    public void testUpdateWallet() {
        WalletEntity walletEntity = buildAWallet();
        WalletEntity insertedWallet = walletDao.insert(walletEntity);
        insertedWallet.setStatus(Status.LOCKED);
        WalletEntity updatedWallet = walletDao.update(insertedWallet);
        Assert.assertEquals(updatedWallet.getStatus(), Status.LOCKED);
    }

    private WalletEntity buildAWallet() {
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setUserId(idGenerator.nextId());
        walletEntity.setType(WalletType.STORED_VALUE);
        walletEntity.setStatus(Status.ACTIVE);
        walletEntity.setCurrency(Currency.USD.toString());
        walletEntity.setBalance(BigDecimal.ZERO);
        return walletEntity;
    }
}
