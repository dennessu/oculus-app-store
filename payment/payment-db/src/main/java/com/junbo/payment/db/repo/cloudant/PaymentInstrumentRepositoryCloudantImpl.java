/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.cloudant;

import com.junbo.common.id.PIType;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.db.repo.PaymentInstrumentRepository;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

import java.util.List;

/**
 * Created by haomin on 14-6-19.
 */
public class PaymentInstrumentRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<PaymentInstrument, Long> implements PaymentInstrumentRepository {
    @Override
    public Promise<List<PaymentInstrument>> getByUserId(Long userId) {
        return super.queryView("by_user_id", userId.toString());
    }

    @Override
    public Promise<List<PaymentInstrument>> getByUserAndType(Long userId, PIType piType) {
        if(piType == null){
            return super.queryView("by_user_id_deleted", userId + ":" + "false");
        }else{
            return super.queryView("by_user_id_deleted_type", userId + ":" + "false" + ":" + piType.getId());
        }
    }
    @Override
    public Promise<PaymentInstrument> create(PaymentInstrument entity) {
        entity.setIsDeleted(false);
        return super.create(entity);
    }

    @Override
    public Promise<Void> delete(Long piId){
        PaymentInstrument existing = this.get(piId).get();
        if(existing != null){
            existing.setIsDeleted(true);
            this.update(existing, existing);
        }
        return Promise.pure(null);
    }
}
