/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.dao.cloudant;

import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.ewallet.db.dao.TransactionDao;
import com.junbo.ewallet.db.entity.TransactionEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * cloudantImpl of TransactionDao.
 */
public class TransactionDaoImpl extends TransactionBaseDao<TransactionEntity> implements TransactionDao {
    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }

    @Override
    public List<TransactionEntity> getByWalletId(Long walletId) {
        return super.queryView("byWalletId", walletId.toString()).get();
    }

    @Override
    public TransactionEntity getByTrackingUuid(Long shardMasterId, UUID uuid) {
        List<TransactionEntity> results = super.queryView("byTrackingUuid", uuid.toString()).get();
        return results.size() == 0 ? null : results.get(0);
    }

    protected CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();

        CloudantView byTrackingUuid = new CloudantView();
        byTrackingUuid.setMap("function(doc) {" +
                "emit(doc.trackingUuid.toString(), doc._id)" +
                "}");
        byTrackingUuid.setResultClass(String.class);
        viewMap.put("byTrackingUuid", byTrackingUuid);

        CloudantView byWalletId = new CloudantView();
        byWalletId.setMap("function(doc) {" +
                "emit(doc.walletId.toString(), doc._id)" +
                "}");
        byWalletId.setResultClass(String.class);
        viewMap.put("byWalletId", byWalletId);

        setViews(viewMap);
    }};
}
