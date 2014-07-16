/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.sql;

import com.junbo.billing.db.dao.OrderBalanceLinkEntityDao;
import com.junbo.billing.db.entity.OrderBalanceLinkEntity;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.repo.OrderBalanceLinkRepository;
import com.junbo.billing.spec.model.OrderBalanceLink;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haomin on 14-6-9.
 */
public class OrderBalanceLinkRepositorySqlImpl implements OrderBalanceLinkRepository {
    private OrderBalanceLinkEntityDao orderBalanceLinkEntityDao;
    private ModelMapper modelMapper;

    @Required
    public void setOrderBalanceLinkEntityDao(OrderBalanceLinkEntityDao orderBalanceLinkEntityDao) {
        this.orderBalanceLinkEntityDao = orderBalanceLinkEntityDao;
    }

    @Required
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Promise<List<OrderBalanceLink>> findByOrderId(Long orderId) {
        List<OrderBalanceLinkEntity> list = orderBalanceLinkEntityDao.findByOrderId(orderId);
        List<OrderBalanceLink> orderBalanceLinks = new ArrayList<>();
        for (OrderBalanceLinkEntity entity : list) {
            OrderBalanceLink orderBalanceLink = modelMapper.toOrderBalanceLink(entity, new MappingContext());
            if (orderBalanceLink != null) {
                orderBalanceLinks.add(orderBalanceLink);
            }
        }

        return Promise.pure(orderBalanceLinks);
    }

    @Override
    public Promise<List<OrderBalanceLink>> findByBalanceId(Long balanceId) {
        List<OrderBalanceLinkEntity> list = orderBalanceLinkEntityDao.findByBalanceId(balanceId);
        List<OrderBalanceLink> orderBalanceLinks = new ArrayList<>();
        for (OrderBalanceLinkEntity entity : list) {
            OrderBalanceLink orderBalanceLink = modelMapper.toOrderBalanceLink(entity, new MappingContext());
            if (orderBalanceLink != null) {
                orderBalanceLinks.add(orderBalanceLink);
            }
        }

        return Promise.pure(orderBalanceLinks);
    }

    @Override
    public Promise<OrderBalanceLink> get(Long id) {
        OrderBalanceLinkEntity entity = orderBalanceLinkEntityDao.get(id);
        if (entity != null) {
            return Promise.pure(modelMapper.toOrderBalanceLink(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<OrderBalanceLink> create(OrderBalanceLink model) {
        OrderBalanceLinkEntity entity = modelMapper.toOrderBalanceLinkEntity(model, new MappingContext());
        OrderBalanceLinkEntity saved = orderBalanceLinkEntityDao.save(entity);
        return get(saved.getId());
    }

    @Override
    public Promise<OrderBalanceLink> update(OrderBalanceLink model, OrderBalanceLink oldModel) {
        OrderBalanceLinkEntity entity = modelMapper.toOrderBalanceLinkEntity(model, new MappingContext());
        OrderBalanceLinkEntity oldEntity = modelMapper.toOrderBalanceLinkEntity(oldModel, new MappingContext());
        orderBalanceLinkEntityDao.update(entity, oldEntity);
        return get(entity.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Order balance link not support delete");
    }
}
