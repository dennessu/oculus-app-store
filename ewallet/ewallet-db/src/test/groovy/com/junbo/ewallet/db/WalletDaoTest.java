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
        Long id = walletDao.insert(walletEntity);
        WalletEntity insertedWallet = walletDao.get(id);
        Assert.assertNotNull(insertedWallet);
        Assert.assertEquals(walletEntity.getBalance(), insertedWallet.getBalance());
    }

    @Test
    public void testUpdateWallet() {
        WalletEntity walletEntity = buildAWallet();
        Long id = walletDao.insert(walletEntity);
        WalletEntity insertedWallet = walletDao.get(id);
        insertedWallet.setStatusId(Status.LOCKED.getId());
        walletDao.update(insertedWallet);
        WalletEntity updatedWallet = walletDao.get(id);
        Assert.assertEquals(updatedWallet.getStatusId(), Status.LOCKED.getId());
    }

    private WalletEntity buildAWallet() {
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setUserId(idGenerator.nextId());
        walletEntity.setTypeId(WalletType.STORED_VALUE.getId());
        walletEntity.setStatusId(Status.ACTIVE.getId());
        walletEntity.setCurrency(Currency.USD.toString());
        walletEntity.setBalance(BigDecimal.ZERO);
        return walletEntity;
    }
}
