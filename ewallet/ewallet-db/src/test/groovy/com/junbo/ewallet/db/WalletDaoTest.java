/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db;

import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.def.Currency;
import com.junbo.ewallet.db.entity.def.Status;
import com.junbo.ewallet.db.entity.def.WalletLotType;
import com.junbo.ewallet.db.entity.def.WalletType;
import com.junbo.ewallet.db.entity.hibernate.WalletEntity;
import com.junbo.ewallet.db.entity.hibernate.WalletLotEntity;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;

/**
 * Test for WalletDao.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
public class WalletDaoTest extends AbstractTransactionalTestNGSpringContextTests{
    @Override
    @Autowired
    @Qualifier("ewalletDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }
    @Autowired
    @Qualifier("idGenerator")
    private IdGenerator idGenerator;
    @Autowired
    private WalletDao walletDao;
    @Autowired
    private WalletLotDao walletLotDao;

    @Test
    public void testCreateWallet(){
        WalletEntity walletEntity = buildAWallet();
        WalletEntity insertedWallet = walletDao.insert(walletEntity);
        Assert.assertNotNull(insertedWallet.getId());
        Assert.assertEquals(walletEntity.getBalance(), insertedWallet.getBalance());
    }

    @Test
    public void testUpdateWallet(){
        WalletEntity walletEntity = buildAWallet();
        WalletEntity insertedWallet = walletDao.insert(walletEntity);
        insertedWallet.setStatus(Status.LOCKED);
        WalletEntity updatedWallet = walletDao.update(insertedWallet);
        Assert.assertEquals(updatedWallet.getStatus(), Status.LOCKED);
    }

    @Test
    public void testDebit(){
        WalletEntity wallet = walletDao.insert(buildAWallet());
        WalletLotEntity lot1 = walletLotDao.insert(buildALot(wallet.getId(), WalletLotType.CASH));
        WalletLotEntity lot2 = walletLotDao.insert(buildALot(wallet.getId(), WalletLotType.PROMOTION));
        walletLotDao.debit(wallet.getId(), new BigDecimal(15));
        Assert.assertEquals(walletLotDao.get(lot1.getId()).getRemainingAmount(), new BigDecimal(5));
        Assert.assertEquals(walletLotDao.get(lot2.getId()).getRemainingAmount(), BigDecimal.ZERO);
    }

    private WalletEntity buildAWallet(){
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setUserId(idGenerator.nextId());
        walletEntity.setType(WalletType.SV);
        walletEntity.setStatus(Status.ACTIVE);
        walletEntity.setCurrency(Currency.USD);
        walletEntity.setBalance(BigDecimal.ZERO);
        return walletEntity;
    }

    private WalletLotEntity buildALot(Long walletId, WalletLotType type){
        WalletLotEntity lot = new WalletLotEntity();
        lot.setTotalAmount(new BigDecimal(10));
        lot.setRemainingAmount(new BigDecimal(10));
        lot.setType(type);
        lot.setWalletId(walletId);
        return lot;
    }
}
