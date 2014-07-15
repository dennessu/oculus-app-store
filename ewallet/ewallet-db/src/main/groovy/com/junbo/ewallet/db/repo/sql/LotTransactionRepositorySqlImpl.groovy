/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo.sql

import com.junbo.ewallet.db.dao.LotTransactionDao
import com.junbo.ewallet.db.entity.LotTransactionEntity
import com.junbo.ewallet.db.mapper.ModelMapper
import com.junbo.ewallet.db.repo.LotTransactionRepository
import com.junbo.ewallet.spec.model.LotTransaction
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 6/23/14.
 */
@CompileStatic
class LotTransactionRepositorySqlImpl implements LotTransactionRepository {
    private LotTransactionDao lotTransactionDao
    private ModelMapper modelMapper

    @Required
    void setLotTransactionDao(LotTransactionDao lotTransactionDao) {
        this.lotTransactionDao = lotTransactionDao
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }


    @Override
    Promise<LotTransaction> get(Long id) {
        LotTransactionEntity entity = lotTransactionDao.get(id);
        if (entity != null) {
            return Promise.pure(modelMapper.toLotTransaction(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    Promise<LotTransaction> create(LotTransaction model) {
        LotTransactionEntity entity = modelMapper.toLotTransactionEntity(model, new MappingContext());
        Long savedId = lotTransactionDao.insert(entity);
        return get(savedId);
    }

    @Override
    Promise<LotTransaction> update(LotTransaction model) {
        LotTransactionEntity entity = modelMapper.toLotTransactionEntity(model, new MappingContext());
        lotTransactionDao.update(entity);
        return get(model.getId());
    }

    @Override
    Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on lot transaction not support");
    }

    @Override
    Promise<List<LotTransaction>> getByTransactionId(Long transactionId) {
        List<LotTransactionEntity> list = lotTransactionDao.getByTransactionId(transactionId);
        List<LotTransaction> lotTransactionArrayList = new ArrayList<>();
        for (LotTransactionEntity entity : list) {
            LotTransaction lotTransaction = modelMapper.toLotTransaction(entity, new MappingContext());
            if (lotTransaction != null) {
                lotTransactionArrayList.add(lotTransaction);
            }
        }

        return Promise.pure(lotTransactionArrayList);
    }
}
