/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo.sql

import com.junbo.ewallet.db.dao.WalletDao
import com.junbo.ewallet.db.entity.WalletEntity
import com.junbo.ewallet.db.mapper.ModelMapper
import com.junbo.ewallet.db.repo.WalletRepository
import com.junbo.ewallet.spec.def.WalletType
import com.junbo.ewallet.spec.model.Wallet
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 6/23/14.
 */
@CompileStatic
class WalletRepositorySqlImpl implements WalletRepository {
    private WalletDao walletDao
    private ModelMapper modelMapper

    @Required
    void setWalletDao(WalletDao walletDao) {
        this.walletDao = walletDao
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<Wallet> get(Long id) {
        WalletEntity entity = walletDao.get(id)
        if (entity != null) {
            return Promise.pure(modelMapper.toWallet(entity, new MappingContext()))
        }

        return Promise.pure(null)    }

    @Override
    Promise<Wallet> create(Wallet model) {
        WalletEntity entity = modelMapper.toWalletEntity(model, new MappingContext())
        Long savedId = walletDao.insert(entity)
        return get(savedId)
    }

    @Override
    Promise<Wallet> update(Wallet model) {
        WalletEntity entity = modelMapper.toWalletEntity(model, new MappingContext())
        walletDao.update(entity)
        return get(model.getId())
    }

    @Override
    Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on wallet not support")
    }

    @Override
    Promise<Wallet> getByTrackingUuid(Long shardMasterId, UUID uuid) {
        WalletEntity entity = walletDao.getByTrackingUuid(shardMasterId, uuid)
        return Promise.pure(modelMapper.toWallet(entity, new MappingContext()))
    }

    @Override
    Promise<Wallet> get(Long userId, WalletType type, String currency) {
        WalletEntity entity = walletDao.get(userId, type, currency)
        return Promise.pure(modelMapper.toWallet(entity, new MappingContext()))
    }

    @Override
    Promise<List<Wallet>> getAll(Long userId) {
        List<WalletEntity> list = walletDao.getAll(userId)
        List<Wallet> walletArrayList = new ArrayList<>()
        for (WalletEntity entity : list) {
            Wallet wallet = modelMapper.toWallet(entity, new MappingContext())
            if (wallet != null) {
                walletArrayList.add(wallet)
            }
        }

        return Promise.pure(walletArrayList)
    }
}
