/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repo.facade.impl;

import com.junbo.billing.db.repo.*;
import com.junbo.billing.db.repo.facade.BalanceRepositoryFacade;
import com.junbo.billing.db.repo.facade.TransactionRepositoryFacade;
import com.junbo.billing.spec.enums.BalanceStatus;
import com.junbo.billing.spec.enums.EventActionType;
import com.junbo.billing.spec.model.*;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.OrderId;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by xmchen on 14-2-19.
 */
public class BalanceRepositoryFacadeImpl implements BalanceRepositoryFacade {
    private TransactionRepositoryFacade transactionRepositoryFacade;
    private BalanceRepository balanceRepository;
    private BalanceItemRepository balanceItemRepository;
    private BalanceItemEventRepository balanceItemEventRepository;
    private BalanceEventRepository balanceEventRepository;
    private OrderBalanceLinkRepository orderBalanceLinkRepository;
    private TaxItemRepository taxItemRepository;
    private DiscountItemRepository discountItemRepository;

    @Required
    public void setTransactionRepositoryFacade(TransactionRepositoryFacade transactionRepositoryFacade) {
        this.transactionRepositoryFacade = transactionRepositoryFacade;
    }

    @Required
    public void setBalanceRepository(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Required
    public void setBalanceItemRepository(BalanceItemRepository balanceItemRepository) {
        this.balanceItemRepository = balanceItemRepository;
    }

    @Required
    public void setBalanceItemEventRepository(BalanceItemEventRepository balanceItemEventRepository) {
        this.balanceItemEventRepository = balanceItemEventRepository;
    }

    @Required
    public void setBalanceEventRepository(BalanceEventRepository balanceEventRepository) {
        this.balanceEventRepository = balanceEventRepository;
    }

    @Required
    public void setOrderBalanceLinkRepository(OrderBalanceLinkRepository orderBalanceLinkRepository) {
        this.orderBalanceLinkRepository = orderBalanceLinkRepository;
    }

    @Required
    public void setTaxItemRepository(TaxItemRepository taxItemRepository) {
        this.taxItemRepository = taxItemRepository;
    }

    @Required
    public void setDiscountItemRepository(DiscountItemRepository discountItemRepository) {
        this.discountItemRepository = discountItemRepository;
    }

    @Override
    public Balance saveBalance(Balance balance) {
        Long createdBy = balance.getUserId().getValue();

        // balance
        balance.setCreatedTime(new Date());
        balance.setCreatedBy(createdBy);
        balance.setRequestorId("GOD");
        Balance savedBalance = balanceRepository.create(balance).get();
        balance.setId(savedBalance.getId());

        for (BalanceItem balanceItem : balance.getBalanceItems()) {
            // balance item
            balanceItem.setBalanceId(savedBalance.getId().getValue());
            balanceItem.setCreatedTime(new Date());
            balanceItem.setCreatedBy(createdBy);
            BalanceItem savedBalanceItem = balanceItemRepository.create(balanceItem).get();
            balanceItem.setId(savedBalanceItem.getId());

            // balance item event
            saveBalanceItemEvent(balanceItem);

            // balance item -> tax item
            for (TaxItem taxItem : balanceItem.getTaxItems()) {
                taxItem.setBalanceItemId(savedBalanceItem.getId());
                taxItem.setCreatedTime(new Date());
                taxItem.setCreatedBy(createdBy);
                taxItemRepository.create(taxItem).get();
            }

            // balance item -> discount item
            for (DiscountItem discountItem : balanceItem.getDiscountItems()) {
                discountItem.setBalanceItemId(savedBalanceItem.getId());
                discountItem.setCreatedTime(new Date());
                discountItem.setCreatedBy(createdBy);
                discountItemRepository.create(discountItem).get();
            }
        }

        for (Transaction transaction : balance.getTransactions()) {
            // transactions
            transaction.setBalanceId(savedBalance.getId());
            transaction.setCreatedBy(createdBy);
            transaction.setCreatedTime(new Date());
            transactionRepositoryFacade.saveTransaction(transaction);
        }

        // persist the order balance link
        for (OrderId orderId : balance.getOrderIds()) {
            OrderBalanceLink orderBalanceLink = new OrderBalanceLink();
            orderBalanceLink.setBalanceId(savedBalance.getId().getValue());
            orderBalanceLink.setOrderId(orderId.getValue());
            orderBalanceLink.setCreatedTime(new Date());
            orderBalanceLink.setCreatedBy(createdBy);
            orderBalanceLinkRepository.create(orderBalanceLink).get();
        }

        // create balance event
        saveBalanceEvent(balance);

        return balance;
    }

    @Override
    public Balance getBalance(Long balanceId) {
        Balance balance = balanceRepository.get(new BalanceId(balanceId)).get();
        if (balance == null) {
            return null;
        }

        List<BalanceItem> itemEntities = balanceItemRepository.findByBalanceId(balanceId).get();
        for (BalanceItem balanceItem : itemEntities) {
            balance.addBalanceItem(balanceItem);

            List<TaxItem> taxItems = taxItemRepository.findByBalanceItemId(balanceItem.getId()).get();
            for (TaxItem taxItem : taxItems) {
                balanceItem.addTaxItem(taxItem);
            }

            List<DiscountItem> discountItems = discountItemRepository.findByBalanceItemId(balanceItem.getId()).get();
            for (DiscountItem discountItem : discountItems) {
                balanceItem.addDiscountItem(discountItem);
            }
        }
        List<Transaction> transactions = transactionRepositoryFacade.getTransactions(balanceId);
        for (Transaction transaction : transactions) {
            balance.addTransaction(transaction);
        }

        List<OrderBalanceLink> orderBalanceLinks = orderBalanceLinkRepository.findByBalanceId(balanceId).get();
        List<OrderId> orderIds = new ArrayList<>();
        if (orderBalanceLinks != null) {
            for (OrderBalanceLink orderBalanceLink : orderBalanceLinks) {
                orderIds.add(new OrderId(orderBalanceLink.getOrderId()));
            }
        }
        balance.setOrderIds(orderIds);

        return balance;
    }

    @Override
    public List<Balance> getBalances(Long orderId) {
        List<Balance> balances = new ArrayList<>();
        List<OrderBalanceLink> orderBalanceLinks = orderBalanceLinkRepository.findByOrderId(orderId).get();
        for (OrderBalanceLink orderBalanceLink : orderBalanceLinks) {
            Balance balance = getBalance(orderBalanceLink.getBalanceId());
            balances.add(balance);
        }

        return balances;
    }

    @Override
    public Balance getBalanceByUuid(UUID uuid) {
        List<Balance> balances = balanceRepository.getByTrackingUuid(uuid).get();
        if (balances != null && balances.size() > 0) {
            Long balanceId = balances.get(0).getId().getValue();
            return getBalance(balanceId);
        }
        return null;
    }

    @Override
    public Balance updateBalance(Balance balance, EventActionType eventActionType) {
        Balance savedBalance = getBalance(balance.getId().getValue());

        savedBalance.setType(balance.getType());
        savedBalance.setStatus(balance.getStatus());
        savedBalance.setShippingAddressId(balance.getShippingAddressId());
        savedBalance.setUpdatedTime(new Date());
        savedBalance.setUpdatedBy(balance.getUserId().getValue());
        balanceRepository.update(savedBalance).get();

        for (Transaction transaction : balance.getTransactions()) {
            if (transaction.getId() == null) {
                transaction.setBalanceId(balance.getId());
                transaction.setCreatedBy(balance.getUserId().getValue());
                transaction.setCreatedTime(new Date());
                transactionRepositoryFacade.saveTransaction(transaction);
                savedBalance.addTransaction(transaction);
            } else {
                transactionRepositoryFacade.updateTransaction(transaction);
            }
        }

        // create balance event
        saveBalanceEvent(savedBalance);

        return setBackNonPersistAttributes(savedBalance, balance);
    }

    @Override
    public List<BalanceId> fetchToSettleBalanceIds(Integer count) {
        List<BalanceId> ids = new ArrayList<>();

        List<Balance> balances = balanceRepository.getInitBalances().get();
        for (Balance balance : balances) {
            if (count <= 0) {
                break;
            }
            ids.add(balance.getId());
            count--;
        }

        balances = balanceRepository.getAwaitingPaymentBalances().get();
        for (Balance balance : balances) {
            if (count <= 0) {
                break;
            }
            ids.add(balance.getId());
            count--;
        }

        balances = balanceRepository.getUnconfirmedBalances().get();
        for (Balance balance : balances) {
            if (count <= 0) {
                break;
            }
            ids.add(balance.getId());
            count--;
        }

        return ids;
    }

    @Override
    public List<Balance> getRefundBalancesByOriginalId(Long balanceId) {
        List<Balance> balances = balanceRepository.getRefundBalancesByOriginalId(balanceId).get();
        List<Balance> results = new ArrayList<>();
        if (balances != null) {
            for (Balance balance : balances) {
                Balance b = getBalance(balance.getId().getValue());
                if (balance != null) {
                    results.add(b);
                }
            }
        }
        return results;
    }

    private void saveBalanceEvent(Balance balance) {
        // balance event
        BalanceEvent balanceEvent = new BalanceEvent();
        balanceEvent.setBalanceId(balance.getId().getValue());
        balanceEvent.setActionTypeId(EventActionType.CREATE.getId());
        balanceEvent.setStatusId(BalanceStatus.valueOf(balance.getStatus()).getId());
        balanceEvent.setTotalAmount(balance.getTotalAmount());
        balanceEvent.setTaxAmount(balance.getTaxAmount());
        balanceEvent.setDiscountAmount(balance.getDiscountAmount());
        balanceEvent.setEventDate(new Date());
        balanceEventRepository.create(balanceEvent).get();
    }

    private void saveBalanceItemEvent(BalanceItem balanceItem) {
        // balance item event
        BalanceItemEvent balanceItemEvent = new BalanceItemEvent();
        balanceItemEvent.setBalanceItemId(balanceItem.getId());
        balanceItemEvent.setAmount(balanceItem.getAmount());
        balanceItemEvent.setTaxAmount(balanceItem.getTaxAmount());
        balanceItemEvent.setDiscountAmount(balanceItem.getDiscountAmount());
        balanceItemEvent.setActionTypeId(EventActionType.CREATE.getId());
        balanceItemEvent.setEventDate(new Date());
        balanceItemEventRepository.create(balanceItemEvent).get();
    }

    private Balance setBackNonPersistAttributes(Balance saved, Balance origin) {
        // set back the attributes which are not saved in DB
        saved.setSuccessRedirectUrl(origin.getSuccessRedirectUrl());
        saved.setCancelRedirectUrl(origin.getCancelRedirectUrl());
        saved.setProviderConfirmUrl(origin.getProviderConfirmUrl());
        return saved;
    }
}
