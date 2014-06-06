/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.dao.cloudant;

import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.ewallet.db.dao.LotTransactionDao;
import com.junbo.ewallet.db.entity.LotTransactionEntity;

import java.math.BigDecimal;
import java.util.*;

/**
 * cloudantImpl of LotTransactionDao.
 */
public class LotTransactionDaoImpl extends TransactionBaseDao<LotTransactionEntity> implements LotTransactionDao {
    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }

    @Override
    public LotTransactionEntity insert(LotTransactionEntity lotTransactionEntity) {
        lotTransactionEntity.setIsRefundEnded(false);
        return super.insert(lotTransactionEntity);
    }

    @Override
    public void update(LotTransactionEntity lotTransactionEntity) {
        if (lotTransactionEntity.getUnrefundedAmount().equals(BigDecimal.ZERO)) {
            lotTransactionEntity.setIsRefundEnded(true);
        }
        super.update(lotTransactionEntity);
    }

    @Override
    public List<LotTransactionEntity> getByTransactionId(Long transactionId) {
        String key = transactionId.toString() + ":false";
        List<LotTransactionEntity> results = super.queryView("byTransactionIdAndIsRefundEnded", key).get();
        Collections.sort(results, new Comparator<LotTransactionEntity>() {
            @Override
            public int compare(LotTransactionEntity o1, LotTransactionEntity o2) {
                return o2.getType().getId() - o1.getType().getId();
            }
        });
        return results;
    }

    protected CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();

        CloudantView byTransactionIdAndIsRefundEnded = new CloudantView();
        byTransactionIdAndIsRefundEnded.setMap("function(doc) {" +
                "emit(doc.transactionId.toString() + \':\' + " +
                "doc.isRefundEnded, doc._id)" +
                "}");
        byTransactionIdAndIsRefundEnded.setResultClass(String.class);
        viewMap.put("byTransactionIdAndIsRefundEnded", byTransactionIdAndIsRefundEnded);

        setViews(viewMap);
    }};
}
