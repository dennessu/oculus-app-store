/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.sql;

import com.junbo.billing.db.dao.DiscountItemEntityDao;
import com.junbo.billing.db.entity.DiscountItemEntity;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.repo.DiscountItemRepository;
import com.junbo.billing.spec.model.DiscountItem;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haomin on 14-6-9.
 */
public class DiscountItemRepositorySqlImpl implements DiscountItemRepository {
    private DiscountItemEntityDao discountItemEntityDao;
    private ModelMapper modelMapper;

    @Required
    public void setDiscountItemEntityDao(DiscountItemEntityDao discountItemEntityDao) {
        this.discountItemEntityDao = discountItemEntityDao;
    }

    @Required
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Promise<List<DiscountItem>> findByBalanceItemId(Long balanceItemId) {
        List<DiscountItemEntity> list = discountItemEntityDao.findByBalanceItemId(balanceItemId);
        List<DiscountItem> discountItems = new ArrayList<>();
        for (DiscountItemEntity entity : list) {
            DiscountItem discountItem = modelMapper.toDiscountItem(entity, new MappingContext());
            if (discountItem != null) {
                discountItems.add(discountItem);
            }
        }

        return Promise.pure(discountItems);
    }

    @Override
    public Promise<DiscountItem> get(Long id) {
        DiscountItemEntity entity = discountItemEntityDao.get(id);
        if (entity != null) {
            return Promise.pure(modelMapper.toDiscountItem(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<DiscountItem> create(DiscountItem model) {
        DiscountItemEntity entity = modelMapper.toDiscountItemEntity(model, new MappingContext());
        DiscountItemEntity saved = discountItemEntityDao.save(entity);
        return get(saved.getId());
    }

    @Override
    public Promise<DiscountItem> update(DiscountItem model, DiscountItem oldModel) {
        DiscountItemEntity entity = modelMapper.toDiscountItemEntity(model, new MappingContext());
        DiscountItemEntity oldEntity = modelMapper.toDiscountItemEntity(oldModel, new MappingContext());
        discountItemEntityDao.update(entity, oldEntity);
        return get(entity.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on discount item not support");
    }
}
