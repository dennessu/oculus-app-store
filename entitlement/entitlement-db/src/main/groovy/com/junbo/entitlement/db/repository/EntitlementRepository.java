/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.repository;

import com.junbo.common.cloudant.CloudantClientBase;
import com.junbo.common.cloudant.client.CloudantClientBulk;
import com.junbo.common.model.Results;
import com.junbo.common.util.Context;
import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.dao.EntitlementHistoryDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.db.entity.EntitlementHistoryEntity;
import com.junbo.entitlement.db.mapper.EntitlementMapper;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.langur.core.promise.Promise;

import java.util.*;

/**
 * Repository of Entitlement.
 */
public class EntitlementRepository {
    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    private EntitlementDao entitlementDao;
    private EntitlementHistoryDao entitlementHistoryDao;
    private EntitlementMapper entitlementMapper;

    public void setEntitlementDao(EntitlementDao entitlementDao) {
        this.entitlementDao = entitlementDao;
    }

    public void setEntitlementHistoryDao(EntitlementHistoryDao entitlementHistoryDao) {
        this.entitlementHistoryDao = entitlementHistoryDao;
    }

    public void setEntitlementMapper(EntitlementMapper entitlementMapper) {
        this.entitlementMapper = entitlementMapper;
    }

    public Entitlement get(String entitlementId) {
        return entitlementMapper.toEntitlement(entitlementDao.get(entitlementId));
    }

    public Entitlement insert(Entitlement entitlement) {
        final EntitlementEntity result = entitlementDao.insert(entitlementMapper.toEntitlementEntity(entitlement));
        entitlementHistoryDao.insertAsync(new EntitlementHistoryEntity(CREATE, result));
        return entitlementMapper.toEntitlement(result);
    }

    public Map<Long, List<Entitlement>> bulkInsert(final Map<Long, List<Entitlement>> entitlements) {
        final Map<Long, List<Entitlement>> resultEntitlements = new HashMap<>();
        try {
            CloudantClientBase.setUseBulk(true);
            for (Long actionId : entitlements.keySet()) {
                List<Entitlement> result = new ArrayList<>();
                for (Entitlement e : entitlements.get(actionId)) {
                    result.add(entitlementMapper.toEntitlement(
                            entitlementDao.insert(entitlementMapper.toEntitlementEntity(e))));
                }
                resultEntitlements.put(actionId, result);
            }
        }
        finally {
            CloudantClientBulk.commit().get();
            CloudantClientBase.setUseBulk(false);
        }
        Context.get().registerAsyncTask(new Promise.Func0<Promise>() {
            @Override
            public Promise apply() {
                Promise result = Promise.pure();
                try {
                    CloudantClientBase.setUseBulk(true);
                    for (List<Entitlement> es : resultEntitlements.values()) {
                        final List<Entitlement> localEs = es;
                        result = result.then(new Promise.Func<Entitlement, Promise<Void>>() {
                            @Override
                            public Promise<Void> apply(Entitlement entitlement) {
                                return Promise.each(localEs.iterator(), new Promise.Func<Entitlement, Promise>() {
                                    @Override
                                    public Promise apply(Entitlement entitlement) {
                                        return entitlementHistoryDao.insertSync(new EntitlementHistoryEntity(CREATE, entitlementMapper.toEntitlementEntity(entitlement)));
                                    }
                                });
                            }
                        });
                    }
                } finally {
                    result = CloudantClientBulk.commit();
                    CloudantClientBase.setUseBulk(false);
                }
                return result;
            }
        });
        return resultEntitlements;
    }

    public Entitlement update(Entitlement entitlement, Entitlement oldEntitlement) {
        final EntitlementEntity result = entitlementDao.update(entitlementMapper.toEntitlementEntity(entitlement),
                entitlementMapper.toEntitlementEntity(oldEntitlement));
        entitlementHistoryDao.insertAsync(new EntitlementHistoryEntity(UPDATE, result));
        return entitlementMapper.toEntitlement(result);
    }

    public Results<Entitlement> getBySearchParam(EntitlementSearchParam searchParam, PageMetadata pageMetadata) {
        return entitlementMapper.toEntitlementResults(
                entitlementDao.getBySearchParam(
                        searchParam, pageMetadata == null ? new PageMetadata() : pageMetadata));
    }

    public void delete(String entitlementId) {
        final EntitlementEntity entitlementEntity = entitlementDao.get(entitlementId);
        entitlementEntity.setIsDeleted(true);
        entitlementHistoryDao.insertAsync(new EntitlementHistoryEntity(DELETE, entitlementEntity));
        entitlementDao.update(entitlementEntity, entitlementEntity);
    }

    public Entitlement getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        return entitlementMapper.toEntitlement(entitlementDao.getByTrackingUuid(shardMasterId, trackingUuid));
    }

    public Entitlement get(Long userId, String itemId, String type) {
        return entitlementMapper.toEntitlement(entitlementDao.get(userId, itemId, type));
    }
}
