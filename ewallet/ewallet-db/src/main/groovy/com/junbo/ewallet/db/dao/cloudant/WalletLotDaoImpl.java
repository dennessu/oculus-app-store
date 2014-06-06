/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.dao.cloudant;

import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.ewallet.common.def.WalletConst;
import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.WalletLotEntity;

import java.math.BigDecimal;
import java.util.*;

/**
 * cloudantImpl of WalletLotDao.
 */
public class WalletLotDaoImpl extends BaseDao<WalletLotEntity> implements WalletLotDao {
    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }

    @Override
    public WalletLotEntity insert(WalletLotEntity walletLotEntity) {
        walletLotEntity.setIsActive(Boolean.TRUE);
        return super.insert(walletLotEntity);
    }

    @Override
    public WalletLotEntity update(WalletLotEntity walletLotEntity) {
        if (walletLotEntity.getRemainingAmount().equals(BigDecimal.ZERO)) {
            walletLotEntity.setIsActive(Boolean.FALSE);
        }
        return super.update(walletLotEntity);
    }

    @Override
    public void delete(Long id) {
        super.cloudantDelete(id.toString());
    }

    @Override
    public List<WalletLotEntity> getValidLot(Long walletId) {
        String key = "walletId:'" + walletId + "' AND isActive:true AND expirationDate:{" + new Date().getTime() + " TO " + WalletConst.MAX_DATE + "}";
        List<WalletLotEntity> lots = super.search("search", key, 200, null).get().getResults();
        Collections.sort(lots, new Comparator<WalletLotEntity>() {
            @Override
            public int compare(WalletLotEntity o1, WalletLotEntity o2) {
                return o1.getType().getId() - o2.getType().getId();
            }
        });
        return lots;
    }

    @Override
    public BigDecimal getValidAmount(Long walletId) {
        String key = "walletId:'" + walletId + "' AND isActive:true AND expirationDate:{" + new Date().getTime() + " TO " + WalletConst.MAX_DATE + "}";
        List<WalletLotEntity> lots = super.search("search", key, 200, null).get().getResults();
        BigDecimal result = BigDecimal.ZERO;
        for (WalletLotEntity lot : lots) {
            result = result.add(lot.getRemainingAmount());
        }
        return result;
    }

    protected CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();
        Map<String, CloudantIndex> indexMap = new HashMap<>();
        CloudantIndex index = new CloudantIndex();
        index.setIndex("function(doc) {" +
                "index(\'walletId\', doc.walletId);" +
                "index(\'isActive\', doc.isActive);" +
                "index(\'expirationDate\', doc.expirationDate);" +
                "}");
        index.setResultClass(String.class);
        indexMap.put("search", index);

        setViews(viewMap);
        setIndexes(indexMap);
    }};
}
