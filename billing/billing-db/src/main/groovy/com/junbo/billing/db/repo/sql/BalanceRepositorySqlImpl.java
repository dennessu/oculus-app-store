/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.sql;

import com.junbo.billing.db.dao.BalanceEntityDao;
import com.junbo.billing.db.entity.BalanceEntity;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.repo.BalanceRepository;
import com.junbo.billing.spec.model.Balance;
import com.junbo.common.id.BalanceId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by haomin on 14-6-9.
 */
public class BalanceRepositorySqlImpl implements BalanceRepository {
    private BalanceEntityDao balanceEntityDao;
    private ModelMapper modelMapper;

    @Required
    public void setBalanceEntityDao(BalanceEntityDao balanceEntityDao) {
        this.balanceEntityDao = balanceEntityDao;
    }

    @Required
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    @Override
    public Promise<List<Balance>> getByTrackingUuid(UUID trackingUuid) {
        List<BalanceEntity> list = balanceEntityDao.getByTrackingUuid(trackingUuid);
        List<Balance> balances = new ArrayList<>();
        for (BalanceEntity entity : list) {
            Balance balance = modelMapper.toBalance(entity, new MappingContext());
            if (balance != null) {
                balances.add(balance);
            }
        }

        return Promise.pure(balances);
    }

    @Override
    public Promise<List<Balance>> getInitBalances() {
        List<BalanceEntity> list = balanceEntityDao.getInitBalances();
        List<Balance> balances = new ArrayList<>();
        for (BalanceEntity entity : list) {
            Balance balance = modelMapper.toBalance(entity, new MappingContext());
            if (balance != null) {
                balances.add(balance);
            }
        }

        return Promise.pure(balances);
    }

    @Override
    public Promise<List<Balance>> getAwaitingPaymentBalances() {
        List<BalanceEntity> list = balanceEntityDao.getAwaitingPaymentBalances();
        List<Balance> balances = new ArrayList<>();
        for (BalanceEntity entity : list) {
            Balance balance = modelMapper.toBalance(entity, new MappingContext());
            if (balance != null) {
                balances.add(balance);
            }
        }

        return Promise.pure(balances);
    }

    @Override
    public Promise<List<Balance>> getUnconfirmedBalances() {
        List<BalanceEntity> list = balanceEntityDao.getUnconfirmedBalances();
        List<Balance> balances = new ArrayList<>();
        for (BalanceEntity entity : list) {
            Balance balance = modelMapper.toBalance(entity, new MappingContext());
            if (balance != null) {
                balances.add(balance);
            }
        }

        return Promise.pure(balances);
    }

    @Override
    public Promise<List<Balance>> getRefundBalancesByOriginalId(Long balanceId) {
        List<BalanceEntity> list = balanceEntityDao.getRefundBalancesByOriginalId(balanceId);
        List<Balance> balances = new ArrayList<>();
        for (BalanceEntity entity : list) {
            Balance balance = modelMapper.toBalance(entity, new MappingContext());
            if (balance != null) {
                balances.add(balance);
            }
        }

        return Promise.pure(balances);
    }

    @Override
    public Promise<Balance> get(BalanceId id) {
        BalanceEntity entity = balanceEntityDao.get(id.getValue());
        if (entity != null) {
            return Promise.pure(modelMapper.toBalance(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<Balance> create(Balance model) {
        BalanceEntity entity = modelMapper.toBalanceEntity(model, new MappingContext());
        BalanceEntity saved = balanceEntityDao.save(entity);
        return get(new BalanceId(saved.getId()));
    }

    @Override
    public Promise<Balance> update(Balance model, Balance oldModel) {
        BalanceEntity entity = modelMapper.toBalanceEntity(model, new MappingContext());
        BalanceEntity oldEntity = modelMapper.toBalanceEntity(oldModel, new MappingContext());
        balanceEntityDao.update(entity, oldEntity);
        return get(new BalanceId(entity.getId()));
    }

    @Override
    public Promise<Void> delete(BalanceId id) {
        throw new UnsupportedOperationException("Delete on balance not support");
    }
}
