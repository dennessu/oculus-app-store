/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.dao.cloudant;

import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.spec.def.WalletType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * cloudantImpl of WalletDao.
 */
public class WalletDaoImpl extends BaseDao<WalletEntity> implements WalletDao {
    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }

    @Override
    public WalletEntity getByTrackingUuid(Long shardMasterId, UUID uuid) {
        List<WalletEntity> results = super.queryView("byTrackingUuid", uuid.toString()).get();
        return results.size() == 0 ? null : results.get(0);
    }

    @Override
    public WalletEntity get(Long userId, WalletType type, String currency) {
        String key = userId.toString() + ":" + type.toString() + ":" + currency;
        List<WalletEntity> results = super.queryView("byUserIdAndTypeAndCurrency", key).get();
        return results.size() == 0 ? null : results.get(0);
    }

    @Override
    public List<WalletEntity> getAll(Long userId) {
        return super.queryView("byUserId", userId.toString()).get();
    }

    protected CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();

        CloudantView byTrackingUuid = new CloudantView();
        byTrackingUuid.setMap("function(doc) {" +
                "emit(doc.trackingUuid.toString(), doc._id)" +
                "}");
        byTrackingUuid.setResultClass(String.class);
        viewMap.put("byTrackingUuid", byTrackingUuid);

        CloudantView byUserId = new CloudantView();
        byUserId.setMap("function(doc) {" +
                "emit(doc.userId.toString(), doc._id)" +
                "}");
        byUserId.setResultClass(String.class);
        viewMap.put("byUserId", byUserId);

        CloudantView byUserIdAndTypeAndCurrency = new CloudantView();
        byUserIdAndTypeAndCurrency.setMap("function(doc) {" +
                "emit(doc.userId.toString() + \':\' + " +
                "doc.type + \':\' + doc.currency, doc._id)" +
                "}");
        byUserIdAndTypeAndCurrency.setResultClass(String.class);
        viewMap.put("byUserIdAndTypeAndCurrency", byUserIdAndTypeAndCurrency);

        setViews(viewMap);
    }};
}
