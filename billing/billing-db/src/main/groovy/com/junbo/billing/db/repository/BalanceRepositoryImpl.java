/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.billing.db.dao.impl.BalanceItemEventEntityDaoImpl;
import com.junbo.billing.db.entity.*;
import com.junbo.billing.spec.enums.EventActionType;
import com.junbo.billing.spec.model.*;
import com.junbo.common.id.BalanceId;
import com.junbo.oom.core.MappingContext;
import com.junbo.billing.db.dao.*;
import com.junbo.billing.db.mapper.ModelMapper;
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
    private BalanceItemEventEntityDaoImpl balanceItemEventEntityDao;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Balance saveBalance(Balance balance) {

        BalanceEntity balanceEntity = modelMapper.toBalanceEntity(balance, new MappingContext());

        balanceEntity.setRequestorId("GOD");
        balanceEntity.setCreatedBy("Billing");
        balanceEntity.setCreatedTime(new Date());
        balanceEntityDao.save(balanceEntity);

        for(BalanceItem item : balance.getBalanceItems()) {
            BalanceItemEntity balanceItemEntity = modelMapper.toBalanceItemEntity(item, new MappingContext());

            balanceItemEntity.setBalanceId(balanceEntity.getBalanceId());
            balanceItemEntity.setCreatedTime(new Date());
            balanceItemEntity.setCreatedBy("Billing");
            balanceItemEntityDao.save(balanceItemEntity);
            saveBalanceItemEventEntity(balanceItemEntity, EventActionType.CREATE);

            for(TaxItem tax : item.getTaxItems()) {
                TaxItemEntity taxItemEntity = modelMapper.toTaxItemEntity(tax, new MappingContext());

                taxItemEntity.setBalanceItemId(balanceItemEntity.getBalanceItemId());
                taxItemEntity.setCreatedTime(new Date());
                taxItemEntity.setCreatedBy("Billing");
                taxItemEntityDao.save(taxItemEntity);
            }
            for(DiscountItem discount : item.getDiscountItems()) {
                DiscountItemEntity discountItemEntity =
                        modelMapper.toDiscountItemEntity(discount, new MappingContext());

                discountItemEntity.setBalanceItemId(balanceItemEntity.getBalanceItemId());
                discountItemEntity.setCreatedTime(new Date());
                discountItemEntity.setCreatedBy("Billing");
                discountItemEntityDao.save(discountItemEntity);
            }
        }

        for(Transaction transaction : balance.getTransactions()) {
            transaction.setBalanceId(new BalanceId(balanceEntity.getBalanceId()));
            transactionRepository.saveTransaction(transaction);
        }

        // persist the order balance link
        OrderBalanceLinkEntity orderBalanceLinkEntity = new OrderBalanceLinkEntity();

        orderBalanceLinkEntity.setBalanceId(balanceEntity.getBalanceId());
        orderBalanceLinkEntity.setOrderId(balance.getOrderId().getValue());
        orderBalanceLinkEntity.setCreatedTime(new Date());
        orderBalanceLinkEntity.setCreatedBy("Billing");
        orderBalanceLinkEntityDao.save(orderBalanceLinkEntity);

        // create balance event
        saveBalanceEventEntity(balanceEntity, EventActionType.CREATE);

        Balance result = getBalance(balanceEntity.getBalanceId());

        return setBackNonPersistAttributes(result, balance);
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
    public Balance updateBalance(Balance balance, EventActionType eventActionType) {
        BalanceEntity balanceEntity = modelMapper.toBalanceEntity(balance, new MappingContext());
        BalanceEntity savedEntity = balanceEntityDao.get(balanceEntity.getBalanceId());

        savedEntity.setTypeId(balanceEntity.getTypeId());
        savedEntity.setStatusId(balanceEntity.getStatusId());
        savedEntity.setShippingAddressId(balanceEntity.getShippingAddressId());
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
        saveBalanceEventEntity(savedEntity, eventActionType);

        return setBackNonPersistAttributes(getBalance(balanceEntity.getBalanceId()), balance);
    }

    @Override
    public List<BalanceId> fetchAsyncChargeBalanceIds(Integer count) {
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
    public List<Balance> getRefundBalancesByOriginalId(Long balanceId) {
        List<BalanceEntity> balanceEntities = balanceEntityDao.getRefundBalancesByOriginalId(balanceId);
        List<Balance> results = new ArrayList<>();
        if (balanceEntities != null) {
            for (BalanceEntity entity : balanceEntities) {
                Balance balance = getBalance(entity.getBalanceId());
                if (balance != null) {
                    results.add(balance);
                }
            }
        }
        return results;
    }

    private void saveBalanceEventEntity(BalanceEntity balanceEntity, EventActionType eventActionType) {
        // create balance event
        BalanceEventEntity balanceEventEntity = new BalanceEventEntity();
        balanceEventEntity.setBalanceId(balanceEntity.getBalanceId());
        balanceEventEntity.setActionTypeId(eventActionType.getId());
        balanceEventEntity.setStatusId(balanceEntity.getStatusId());
        balanceEventEntity.setTotalAmount(balanceEntity.getTotalAmount());
        balanceEventEntity.setTaxAmount(balanceEntity.getTaxAmount());
        balanceEventEntity.setDiscountAmount(balanceEntity.getDiscountAmount());
        balanceEventEntity.setEventDate(new Date());
        balanceEventEntityDao.save(balanceEventEntity);
    }

    private void saveBalanceItemEventEntity(BalanceItemEntity balanceItemEntity, EventActionType eventActionType) {
        BalanceItemEventEntity balanceItemEventEntity = new BalanceItemEventEntity();
        balanceItemEventEntity.setBalanceItemId(balanceItemEntity.getBalanceItemId());
        balanceItemEventEntity.setAmount(balanceItemEntity.getAmount());
        balanceItemEventEntity.setTaxAmount(balanceItemEntity.getTaxAmount());
        balanceItemEventEntity.setDiscountAmount(balanceItemEntity.getDiscountAmount());
        balanceItemEventEntity.setActionTypeId(eventActionType.getId());
        balanceItemEventEntity.setEventDate(new Date());
    }

    private Balance setBackNonPersistAttributes(Balance saved, Balance origin) {
        // set back the attributes which are not saved in DB
        saved.setSuccessRedirectUrl(origin.getSuccessRedirectUrl());
        saved.setCancelRedirectUrl(origin.getCancelRedirectUrl());
        saved.setProviderConfirmUrl(origin.getProviderConfirmUrl());
        return saved;
    }
}
