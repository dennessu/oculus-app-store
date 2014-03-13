/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db;

import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.def.*;
import com.junbo.ewallet.db.entity.hibernate.WalletEntity;
import com.junbo.ewallet.db.entity.hibernate.WalletLotEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;

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

    @Test
    public void testDebit() {
        WalletEntity wallet = walletDao.insert(buildAWallet());
        WalletLotEntity lot1 = walletLotDao.insert(buildALot(wallet.getId(), WalletLotType.CASH));
        WalletLotEntity lot2 = walletLotDao.insert(buildALot(wallet.getId(), WalletLotType.PROMOTION));
        walletLotDao.debit(wallet.getId(), new BigDecimal(17));
        Assert.assertEquals(walletLotDao.get(lot1.getId()).getRemainingAmount(), new BigDecimal(3));
        Assert.assertEquals(walletLotDao.get(lot2.getId()).getRemainingAmount(), BigDecimal.ZERO);
    }

    @Test
    public void testExpiredWalletLot() {
        WalletEntity wallet = walletDao.insert(buildAWallet());
        WalletLotEntity lot1 = walletLotDao.insert(buildALot(wallet.getId(), WalletLotType.CASH));
        WalletLotEntity lot2 = buildALot(wallet.getId(), WalletLotType.PROMOTION);
        lot2.setExpirationDate(new Date(new Date().getTime() - 2000));
        walletLotDao.insert(lot2);
        try{
            walletLotDao.debit(wallet.getId(), new BigDecimal(17));
        } catch (Exception e){
            Assert.assertEquals(e.getClass(), NotEnoughMoneyException.class);
            Assert.assertEquals(walletLotDao.get(lot1.getId()).getRemainingAmount(), new BigDecimal(10));
        }
    }

    private WalletEntity buildAWallet() {
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setUserId(idGenerator.nextId());
        walletEntity.setType(WalletType.SV);
        walletEntity.setStatus(Status.ACTIVE);
        walletEntity.setCurrency(Currency.USD);
        walletEntity.setBalance(BigDecimal.ZERO);
        return walletEntity;
    }

    private WalletLotEntity buildALot(Long walletId, WalletLotType type) {
        WalletLotEntity lot = new WalletLotEntity();
        lot.setTotalAmount(new BigDecimal(10));
        lot.setRemainingAmount(new BigDecimal(10));
        lot.setType(type);
        lot.setWalletId(walletId);
        return lot;
    }
}
