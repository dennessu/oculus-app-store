/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.sql;

import com.junbo.billing.db.dao.BalanceItemEntityDao;
import com.junbo.billing.db.entity.BalanceItemEntity;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.repo.BalanceItemRepository;
import com.junbo.billing.spec.model.BalanceItem;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haomin on 14-6-9.
 */
public class BalanceItemRepositorySqlImpl implements BalanceItemRepository {
    private BalanceItemEntityDao balanceItemEntityDao;
    private ModelMapper modelMapper;

    @Required
    public void setBalanceItemEntityDao(BalanceItemEntityDao balanceItemEntityDao) {
        this.balanceItemEntityDao = balanceItemEntityDao;
    }

    @Required
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Promise<List<BalanceItem>> findByBalanceId(Long balanceId) {
        List<BalanceItemEntity> list = balanceItemEntityDao.findByBalanceId(balanceId);
        List<BalanceItem> balanceItems = new ArrayList<>();
        for (BalanceItemEntity entity : list) {
            BalanceItem balance = modelMapper.toBalanceItem(entity, new MappingContext());
            if (balance != null) {
                balanceItems.add(balance);
            }
        }

        return Promise.pure(balanceItems);
    }

    @Override
    public Promise<BalanceItem> get(Long id) {
        BalanceItemEntity entity = balanceItemEntityDao.get(id);
        if (entity != null) {
            return Promise.pure(modelMapper.toBalanceItem(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<BalanceItem> create(BalanceItem model) {
        BalanceItemEntity entity = modelMapper.toBalanceItemEntity(model, new MappingContext());
        BalanceItemEntity saved = balanceItemEntityDao.save(entity);
        return get(saved.getId());
    }

    @Override
    public Promise<BalanceItem> update(BalanceItem model, BalanceItem oldModel) {
        BalanceItemEntity entity = modelMapper.toBalanceItemEntity(model, new MappingContext());
        BalanceItemEntity oldEntity = modelMapper.toBalanceItemEntity(oldModel, new MappingContext());
        balanceItemEntityDao.update(entity, oldEntity);
        return get(entity.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on balance item not support");
    }
}
