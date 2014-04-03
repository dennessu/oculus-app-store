/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db;

import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.def.*;
import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.db.entity.WalletLotEntity;
import com.junbo.ewallet.spec.def.Currency;
import com.junbo.ewallet.spec.def.Status;
import com.junbo.ewallet.spec.def.WalletLotType;
import com.junbo.ewallet.spec.def.WalletType;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

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
        Random random = new Random();
        WalletEntity wallet = walletDao.insert(buildAWallet());
        WalletLotEntity lot1 = walletLotDao.insert(buildALot(wallet.getId(), WalletLotType.CASH), random.nextLong());
        WalletLotEntity lot2 = walletLotDao.insert(buildALot(wallet.getId(), WalletLotType.PROMOTION), random.nextLong());
        walletLotDao.debit(wallet.getId(), new BigDecimal(17), random.nextLong());
        Assert.assertEquals(walletLotDao.get(lot1.getId()).getRemainingAmount(), new BigDecimal(3));
        Assert.assertEquals(walletLotDao.get(lot2.getId()).getRemainingAmount(), BigDecimal.ZERO);
    }

    @Test
    public void testExpiredWalletLot() {
        Random random = new Random();
        WalletEntity wallet = walletDao.insert(buildAWallet());
        WalletLotEntity lot1 = walletLotDao.insert(buildALot(wallet.getId(), WalletLotType.CASH), random.nextLong());
        WalletLotEntity lot2 = buildALot(wallet.getId(), WalletLotType.PROMOTION);
        lot2.setExpirationDate(new Date(new Date().getTime() - 2000));
        walletLotDao.insert(lot2, random.nextLong());
        try{
            walletLotDao.debit(wallet.getId(), new BigDecimal(17), random.nextLong());
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
        walletEntity.setCurrency(Currency.USD.toString());
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
