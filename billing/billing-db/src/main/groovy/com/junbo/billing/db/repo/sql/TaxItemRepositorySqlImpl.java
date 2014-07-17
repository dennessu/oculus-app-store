/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.sql;

import com.junbo.billing.db.dao.TaxItemEntityDao;
import com.junbo.billing.db.entity.TaxItemEntity;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.repo.TaxItemRepository;
import com.junbo.billing.spec.model.TaxItem;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haomin on 14-6-9.
 */
public class TaxItemRepositorySqlImpl implements TaxItemRepository {
    private TaxItemEntityDao taxItemEntityDao;
    private ModelMapper modelMapper;

    @Required
    public void setTaxItemEntityDao(TaxItemEntityDao taxItemEntityDao) {
        this.taxItemEntityDao = taxItemEntityDao;
    }

    @Required
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Promise<List<TaxItem>> findByBalanceItemId(Long balanceItemId) {
        List<TaxItemEntity> list = taxItemEntityDao.findByBalanceItemId(balanceItemId);
        List<TaxItem> taxItems = new ArrayList<>();
        for (TaxItemEntity entity : list) {
            TaxItem taxItem = modelMapper.toTaxItem(entity, new MappingContext());
            if (taxItem != null) {
                taxItems.add(taxItem);
            }
        }

        return Promise.pure(taxItems);
    }

    @Override
    public Promise<TaxItem> get(Long id) {
        TaxItemEntity entity = taxItemEntityDao.get(id);
        if (entity != null) {
            return Promise.pure(modelMapper.toTaxItem(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<TaxItem> create(TaxItem model) {
        TaxItemEntity entity = modelMapper.toTaxItemEntity(model, new MappingContext());
        TaxItemEntity saved = taxItemEntityDao.save(entity);
        return get(saved.getId());
    }

    @Override
    public Promise<TaxItem> update(TaxItem model, TaxItem oldModel) {
        TaxItemEntity entity = modelMapper.toTaxItemEntity(model, new MappingContext());
        TaxItemEntity oldEntity = modelMapper.toTaxItemEntity(oldModel, new MappingContext());
        taxItemEntityDao.update(entity, oldEntity);
        return get(entity.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("tax item not support delete");
    }
}
