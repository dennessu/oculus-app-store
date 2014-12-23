/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.dao.paymentinstrument.FacebookPaymentAccountDao;
import com.junbo.payment.db.entity.paymentinstrument.FacebookPaymentAccountMappingEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.db.repo.FacebookPaymentAccountRepository;
import com.junbo.payment.spec.internal.FacebookPaymentAccountMapping;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Facebook Payment Account Repository Sql Impl.
 */
public class FacebookPaymentAccountRepositorySqlImpl implements FacebookPaymentAccountRepository {
    private FacebookPaymentAccountDao fbPaymentAccountDao;
    private PaymentMapper paymentMapper;
    private IdGenerator idGenerator;
    @Override
    public Promise<List<FacebookPaymentAccountMapping>> getByUserId(Long userId) {
        List<FacebookPaymentAccountMappingEntity> list = fbPaymentAccountDao.getByUserId(userId);
        List<FacebookPaymentAccountMapping> facebookPaymentAccounts = new ArrayList<>();
        for (FacebookPaymentAccountMappingEntity entity : list) {
            FacebookPaymentAccountMapping fbPaymentAccount = paymentMapper.toFacebookPaymentAccount(entity, new MappingContext());
            if (fbPaymentAccount != null) {
                facebookPaymentAccounts.add(fbPaymentAccount);
            }
        }

        return Promise.pure(facebookPaymentAccounts);
    }

    @Override
    public Promise<FacebookPaymentAccountMapping> get(Long id) {
        FacebookPaymentAccountMappingEntity entity = fbPaymentAccountDao.get(id);
        if (entity != null) {
            return Promise.pure(paymentMapper.toFacebookPaymentAccount(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<FacebookPaymentAccountMapping> create(FacebookPaymentAccountMapping model) {
        if (model.getId() == null) {
            model.setId(idGenerator.nextId(model.getUserId()));
        }
        FacebookPaymentAccountMappingEntity entity = paymentMapper.toFacebookPaymentAccountEntity(model, new MappingContext());
        Long savedId = fbPaymentAccountDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<FacebookPaymentAccountMapping> update(FacebookPaymentAccountMapping model, FacebookPaymentAccountMapping oldModel) {
        FacebookPaymentAccountMappingEntity entity = paymentMapper.toFacebookPaymentAccountEntity(model, new MappingContext());
        FacebookPaymentAccountMappingEntity updated = fbPaymentAccountDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        fbPaymentAccountDao.delete(id);
        return Promise.pure(null);
    }

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
    @Required
    public void setPaymentMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }
    @Required
    public void setFbPaymentAccountDao(FacebookPaymentAccountDao fbPaymentAccountDao) {
        this.fbPaymentAccountDao = fbPaymentAccountDao;
    }
}
