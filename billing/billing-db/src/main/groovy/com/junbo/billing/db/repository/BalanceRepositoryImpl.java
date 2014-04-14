/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.billing.db.entity.*;
import com.junbo.billing.spec.model.*;
import com.junbo.common.id.*;
import com.junbo.oom.core.MappingContext;
import com.junbo.billing.db.dao.*;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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
    private BalanceEventEntityDao balanceEventEntityDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IdGeneratorFacade idGenerator;

    @Override
    public Balance saveBalance(Balance balance) {

        BalanceEntity balanceEntity = modelMapper.toBalanceEntity(balance, new MappingContext());

        balanceEntity.setBalanceId(idGenerator.nextId(BalanceId.class, balanceEntity.getUserId()));
        balanceEntity.setRequestorId("GOD");
        balanceEntity.setCreatedBy("Billing");
        balanceEntity.setCreatedTime(new Date());
        balanceEntityDao.insert(balanceEntity);

        for(BalanceItem item : balance.getBalanceItems()) {
            BalanceItemEntity balanceItemEntity = modelMapper.toBalanceItemEntity(item, new MappingContext());

            balanceItemEntity.setBalanceItemId(idGenerator.nextId(Id.class, balanceEntity.getUserId()));
            balanceItemEntity.setBalanceId(balanceEntity.getBalanceId());
            balanceItemEntity.setCreatedTime(new Date());
            balanceItemEntity.setCreatedBy("Billing");
            balanceItemEntityDao.insert(balanceItemEntity);

            for(TaxItem tax : item.getTaxItems()) {
                TaxItemEntity taxItemEntity = modelMapper.toTaxItemEntity(tax, new MappingContext());

                taxItemEntity.setTaxItemId(idGenerator.nextId(Id.class, balanceEntity.getUserId()));
                taxItemEntity.setBalanceItemId(balanceItemEntity.getBalanceItemId());
                taxItemEntity.setCreatedTime(new Date());
                taxItemEntity.setCreatedBy("Billing");
                taxItemEntityDao.insert(taxItemEntity);
            }
            for(DiscountItem discount : item.getDiscountItems()) {
                DiscountItemEntity discountItemEntity =
                        modelMapper.toDiscountItemEntity(discount, new MappingContext());

                discountItemEntity.setDiscountItemId(
                        idGenerator.nextId(Id.class, balanceEntity.getUserId()));
                discountItemEntity.setBalanceItemId(balanceItemEntity.getBalanceItemId());
                discountItemEntity.setCreatedTime(new Date());
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

        orderBalanceLinkEntity.setLinkId(idGenerator.nextId(Id.class, balanceEntity.getUserId()));
        orderBalanceLinkEntity.setBalanceId(balanceEntity.getBalanceId());
        orderBalanceLinkEntity.setOrderId(balance.getOrderId().getValue());
        orderBalanceLinkEntity.setCreatedTime(new Date());
        orderBalanceLinkEntity.setCreatedBy("Billing");
        orderBalanceLinkEntityDao.insert(orderBalanceLinkEntity);

        // create balance event
        saveBalanceEventEntity(balanceEntity);

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
                    taxItemEntityDao.findByBalanceItemId(balanceItem.getBalanceItemId());
            for(TaxItemEntity taxItemEntity : taxItemEntities) {
                TaxItem taxItem = modelMapper.toTaxItem(taxItemEntity, new MappingContext());
                balanceItem.addTaxItem(taxItem);
            }

            List<DiscountItemEntity> discountItemEntities =
                    discountItemEntityDao.findByBalanceItemId(balanceItem.getBalanceItemId());
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
        List<Balance> balances = new ArrayList<>();

        List<OrderBalanceLinkEntity> orderBalanceLinkEntities = orderBalanceLinkEntityDao.findByOrderId(orderId);
        for(OrderBalanceLinkEntity orderBalanceLinkEntity : orderBalanceLinkEntities) {
            Balance balance = getBalance(orderBalanceLinkEntity.getBalanceId());
            balances.add(balance);
        }

        return balances;
    }

    @Override
    public Balance getBalanceByUuid(UUID uuid) {
        List<BalanceEntity> balanceEntities = balanceEntityDao.getByTrackingUuid(uuid);
        if(balanceEntities != null && balanceEntities.size() > 0) {
            Long balanceId = balanceEntities.get(0).getBalanceId();
            return getBalance(balanceId);
        }
        return null;
    }

    @Override
    public Balance updateBalance(Balance balance) {
        BalanceEntity balanceEntity = modelMapper.toBalanceEntity(balance, new MappingContext());
        BalanceEntity savedEntity = balanceEntityDao.get(balanceEntity.getBalanceId());

        savedEntity.setTypeId(balanceEntity.getTypeId());
        savedEntity.setStatusId(balanceEntity.getStatusId());
        savedEntity.setModifiedTime(new Date());
        savedEntity.setModifiedBy("BILLING");
        balanceEntityDao.update(savedEntity);

        for(Transaction transaction : balance.getTransactions()) {
            if (transaction.getTransactionId() == null) {
                transaction.setBalanceId(new BalanceId(balanceEntity.getBalanceId()));
                transactionRepository.saveTransaction(transaction);
            }
            else {
                transactionRepository.updateTransaction(transaction);
            }
        }

        // create balance event
        saveBalanceEventEntity(savedEntity);

        return getBalance(balanceEntity.getBalanceId());
    }

    @Override
    public List<BalanceId>  fetchAsyncChargeBalanceIds(Integer count) {
        List<BalanceEntity> balanceEntities = balanceEntityDao.getAsyncChargeInitBalances(count);
        List<BalanceId> ids = new ArrayList<>();
        if(balanceEntities != null) {
            for(BalanceEntity entity : balanceEntities) {
                BalanceId id = new BalanceId(entity.getBalanceId());
                ids.add(id);
            }
        }
        return ids;
    }

    @Override
    public List<Balance> getBalancesByOrderItemId(List<Long> orderItemIds) {
        List<Balance> balances = new ArrayList<>();
        List<Long> balanceIds = new ArrayList<>();

        List<BalanceItemEntity> balanceItemEntities = balanceItemEntityDao.findByOrderItemId(orderItemIds);
        for (BalanceItemEntity balanceItemEntity : balanceItemEntities) {
            if (!balanceIds.contains(balanceItemEntity.getBalanceId())) {
                Balance balance = getBalance(balanceItemEntity.getBalanceId());
                balances.add(balance);
                balanceIds.add(balanceItemEntity.getBalanceId());
            }
        }

        return balances;
    }

    private void saveBalanceEventEntity(BalanceEntity balanceEntity) {
        // create balance event
        BalanceEventEntity balanceEventEntity = new BalanceEventEntity();
        balanceEventEntity.setEventId(idGenerator.nextId(Id.class, balanceEntity.getBalanceId()));
        balanceEventEntity.setBalanceId(balanceEntity.getBalanceId());
        balanceEventEntity.setActionTypeId(balanceEntity.getTypeId());
        balanceEventEntity.setStatusId(balanceEntity.getStatusId());
        balanceEventEntity.setEventDate(new Date());
        balanceEventEntityDao.insert(balanceEventEntity);
    }
}
