/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.billing.spec.model.*;
import com.junbo.common.id.BalanceId;
import com.junbo.oom.core.MappingContext;
import com.junbo.billing.db.balance.*;
import com.junbo.billing.db.dao.*;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by xmchen on 14-2-19.
 */
public class BalanceRepositoryImpl implements BalanceRepository {

    @Autowired
    private BalanceEntityDao balanceEntityDao;

    @Autowired
    private BalanceItemEntityDao balanceItemEntityDao;

    @Autowired
    private DiscountItemEntityDao discountItemEntityDao;

    @Autowired
    private TaxItemEntityDao taxItemEntityDao;

    @Autowired
    private OrderBalanceLinkEntityDao orderBalanceLinkEntityDao;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IdGenerator idGenerator;

    @Override
    public Balance saveBalance(Balance balance) {

        BalanceEntity balanceEntity = modelMapper.toBalanceEntity(balance, new MappingContext());

        balanceEntity.setBalanceId(idGenerator.nextId(balanceEntity.getUserId()));
        balanceEntity.setRequestorId("GOD");
        balanceEntity.setCreatedBy("Billing");
        balanceEntity.setCreatedDate(new Date());
        balanceEntityDao.insert(balanceEntity);

        for(BalanceItem item : balance.getBalanceItems()) {
            BalanceItemEntity balanceItemEntity = modelMapper.toBalanceItemEntity(item, new MappingContext());

            balanceItemEntity.setBalanceItemId(idGenerator.nextId(balanceEntity.getUserId()));
            balanceItemEntity.setBalanceId(balanceEntity.getBalanceId());
            balanceItemEntity.setCreatedDate(new Date());
            balanceItemEntity.setCreatedBy("Billing");
            balanceItemEntityDao.insert(balanceItemEntity);

            for(TaxItem tax : item.getTaxItems()) {
                TaxItemEntity taxItemEntity = modelMapper.toTaxItemEntity(tax, new MappingContext());

                taxItemEntity.setTaxItemId(idGenerator.nextId(balanceEntity.getUserId()));
                taxItemEntity.setBalanceItemId(balanceItemEntity.getBalanceItemId());
                taxItemEntity.setCreatedDate(new Date());
                taxItemEntity.setCreatedBy("Billing");
                taxItemEntityDao.insert(taxItemEntity);
            }
            for(DiscountItem discount : item.getDiscountItems()) {
                DiscountItemEntity discountItemEntity =
                        modelMapper.toDiscountItemEntity(discount, new MappingContext());

                discountItemEntity.setDiscountItemId(idGenerator.nextId(balanceEntity.getUserId()));
                discountItemEntity.setBalanceItemId(balanceItemEntity.getBalanceItemId());
                discountItemEntity.setCreatedDate(new Date());
                discountItemEntity.setCreatedBy("Billing");
                discountItemEntityDao.insert(discountItemEntity);
            }
        }

        for(Transaction transaction : balance.getTransactions()) {
            transaction.setBalanceId(new BalanceId(balanceEntity.getBalanceId()));
            transactionRepository.saveTransaction(transaction);
        }

        // persist the order balance link
        OrderBalanceLinkEntity orderBalanceLinkEntity = new OrderBalanceLinkEntity();

        orderBalanceLinkEntity.setLinkId(idGenerator.nextId(balanceEntity.getUserId()));
        orderBalanceLinkEntity.setBalanceId(balanceEntity.getBalanceId());
        orderBalanceLinkEntity.setOrderId(balance.getOrderId().getValue());
        orderBalanceLinkEntity.setCreatedDate(new Date());
        orderBalanceLinkEntity.setCreatedBy("Billing");
        orderBalanceLinkEntityDao.insert(orderBalanceLinkEntity);

        balanceEntityDao.flush();
        balanceItemEntityDao.flush();
        taxItemEntityDao.flush();
        discountItemEntityDao.flush();
        orderBalanceLinkEntityDao.flush();

        return getBalance(balanceEntity.getBalanceId());
    }

    @Override
    public Balance getBalance(Long balanceId) {

        BalanceEntity balanceEntity = balanceEntityDao.get(balanceId);
        Balance balance = modelMapper.toBalance(balanceEntity, new MappingContext());

        List<BalanceItemEntity> itemEntities = balanceItemEntityDao.findByBalanceId(balanceId);
        for(BalanceItemEntity itemEntity : itemEntities) {
            BalanceItem balanceItem = modelMapper.toBalanceItem(itemEntity, new MappingContext());
            balance.addBalanceItem(balanceItem);

            List<TaxItemEntity> taxItemEntities =
                    taxItemEntityDao.findByBalanceItemId(balanceItem.getBalanceItemId().getValue());
            for(TaxItemEntity taxItemEntity : taxItemEntities) {
                TaxItem taxItem = modelMapper.toTaxItem(taxItemEntity, new MappingContext());
                balanceItem.addTaxItem(taxItem);
            }

            List<DiscountItemEntity> discountItemEntities =
                    discountItemEntityDao.findByBalanceItemId(balanceItem.getBalanceItemId().getValue());
            for(DiscountItemEntity discountItemEntity : discountItemEntities) {
                DiscountItem discountItem = modelMapper.toDiscountItem(discountItemEntity, new MappingContext());
                balanceItem.addDiscountItem(discountItem);
            }
        }
        List<Transaction> transactions = transactionRepository.getTransactions(balanceId);
        for(Transaction transaction : transactions) {
            balance.addTransaction(transaction);
        }

        return balance;
    }

    @Override
    public List<Balance> getBalances(Long orderId) {
        List<Balance> balances = new ArrayList<Balance>();

        List<OrderBalanceLinkEntity> orderBalanceLinkEntities = orderBalanceLinkEntityDao.findByOrderId(orderId);
        for(OrderBalanceLinkEntity orderBalanceLinkEntity : orderBalanceLinkEntities) {
            Balance balance = getBalance(orderBalanceLinkEntity.getBalanceId());
            balances.add(balance);
        }

        return balances;
    }
}
