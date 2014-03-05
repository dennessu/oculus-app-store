/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.billing.db.balance.*;
import com.junbo.billing.db.dao.*;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.BalanceItem;
import com.junbo.billing.spec.model.DiscountItem;
import com.junbo.billing.spec.model.TaxItem;
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
    private ModelMapper modelMapper;

    @Override
    public Balance saveBalance(Balance balance) {

        BalanceEntity balanceEntity = modelMapper.toBalanceEntity(balance, new MappingContext());
        //todo: set real key generated ID
        balanceEntity.setBalanceId(new Random().nextLong());
        balanceEntity.setRequestorId("GOD");
        balanceEntity.setCreatedBy("Billing");
        balanceEntity.setCreatedDate(new Date());
        balanceEntityDao.insert(balanceEntity);

        for(BalanceItem item : balance.getBalanceItems()) {
            BalanceItemEntity balanceItemEntity = modelMapper.toBalanceItemEntity(item, new MappingContext());
            //todo: set real key generated ID
            balanceItemEntity.setBalanceItemId(new Random().nextLong());
            balanceItemEntity.setBalanceId(balanceEntity.getBalanceId());
            balanceItemEntity.setCreatedDate(new Date());
            balanceItemEntity.setCreatedBy("Billing");
            balanceItemEntityDao.insert(balanceItemEntity);

            for(TaxItem tax : item.getTaxItems()) {
                TaxItemEntity taxItemEntity = modelMapper.toTaxItemEntity(tax, new MappingContext());
                //todo: set real key generated ID
                taxItemEntity.setTaxItemId(new Random().nextLong());
                taxItemEntity.setBalanceItemId(balanceItemEntity.getBalanceItemId());
                taxItemEntity.setCreatedDate(new Date());
                taxItemEntity.setCreatedBy("Billing");
                taxItemEntityDao.insert(taxItemEntity);
            }
            for(DiscountItem discount : item.getDiscountItems()) {
                DiscountItemEntity discountItemEntity =
                        modelMapper.toDiscountItemEntity(discount, new MappingContext());
                //todo: set real key generated ID
                discountItemEntity.setDiscountItemId(new Random().nextLong());
                discountItemEntity.setBalanceItemId(balanceItemEntity.getBalanceItemId());
                discountItemEntity.setCreatedDate(new Date());
                discountItemEntity.setCreatedBy("Billing");
                discountItemEntityDao.insert(discountItemEntity);
            }
        }
        // persist the order balance link
        OrderBalanceLinkEntity orderBalanceLinkEntity = new OrderBalanceLinkEntity();
        //todo: set real key generated ID
        orderBalanceLinkEntity.setLinkId(new Random().nextLong());
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
