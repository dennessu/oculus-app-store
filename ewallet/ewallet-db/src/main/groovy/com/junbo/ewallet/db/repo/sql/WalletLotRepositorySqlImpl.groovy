/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo.sql

import com.junbo.ewallet.db.dao.WalletLotDao
import com.junbo.ewallet.db.entity.WalletLotEntity
import com.junbo.ewallet.db.mapper.ModelMapper
import com.junbo.ewallet.db.repo.WalletLotRepository
import com.junbo.ewallet.spec.model.WalletLot
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 6/23/14.
 */
@CompileStatic
class WalletLotRepositorySqlImpl implements WalletLotRepository {
    private WalletLotDao walletLotDao
    private ModelMapper modelMapper

    @Required
    void setWalletLotDao(WalletLotDao walletLotDao) {
        this.walletLotDao = walletLotDao
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<WalletLot> get(Long id) {
        WalletLotEntity entity = walletLotDao.get(id)
        if (entity != null) {
            return Promise.pure(modelMapper.toWalletLot(entity, new MappingContext()))
        }

        return Promise.pure(null)
    }

    @Override
    Promise<WalletLot> create(WalletLot model) {
        WalletLotEntity entity = modelMapper.toWalletLotEntity(model, new MappingContext())
        Long savedId = walletLotDao.insert(entity)
        return get(savedId)
    }

    @Override
    Promise<WalletLot> update(WalletLot model, WalletLot oldModel) {
        WalletLotEntity entity = modelMapper.toWalletLotEntity(model, new MappingContext())
        walletLotDao.update(entity)
        return get(model.getId())
    }

    @Override
    Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on walletlot not support")
    }

    @Override
    Promise<List<WalletLot>> getValidLot(Long walletId) {
        List<WalletLotEntity> list = walletLotDao.getValidLot(walletId)
        List<WalletLot> walletLotArrayList = new ArrayList<>()
        for (WalletLotEntity entity : list) {
            WalletLot walletLot = modelMapper.toWalletLot(entity, new MappingContext())
            if (walletLot != null) {
                walletLotArrayList.add(walletLot)
            }
        }

        return Promise.pure(walletLotArrayList)
    }

    @Override
    Promise<BigDecimal> getValidAmount(Long walletId) {
        BigDecimal amount = walletLotDao.getValidAmount(walletId)
        return Promise.pure(amount)
    }
}
