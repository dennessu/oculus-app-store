/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.repository;

import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.dao.EntitlementHistoryDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.db.entity.EntitlementHistoryEntity;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.mapper.EntitlementMapper;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.UUID;

/**
 * Repository of Entitlement.
 */
public class EntitlementRepository {
    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    @Autowired
    @Qualifier("entitlementDao")
    private EntitlementDao entitlementDao;
    @Autowired
    @Qualifier("entitlementRedisDao")
    private EntitlementDao entitlementRedisDao;
    @Autowired
    private EntitlementHistoryDao entitlementHistoryDao;
    @Autowired
    private EntitlementMapper entitlementMapper;

    public Entitlement get(Long entitlementId) {
//        return entitlementMapper.toEntitlement(entitlementRedisDao.get(entitlementId));
        return entitlementMapper.toEntitlement(entitlementDao.get(entitlementId));
    }

    public Entitlement insert(Entitlement entitlement) {
        Long id = entitlementDao.insert(entitlementMapper.toEntitlementEntity(entitlement));
        EntitlementEntity result = entitlementDao.get(id);
//        entitlementRedisDao.insert(result);
        entitlementHistoryDao.insert(new EntitlementHistoryEntity(CREATE, result));
        return entitlementMapper.toEntitlement(result);
    }

    public Entitlement update(Entitlement entitlement) {
        Long id = entitlementDao.update(entitlementMapper.toEntitlementEntity(entitlement));
        EntitlementEntity result = entitlementDao.get(id);
//        entitlementRedisDao.update(result);
        entitlementHistoryDao.insert(new EntitlementHistoryEntity(UPDATE, result));
        return entitlementMapper.toEntitlement(result);
    }

    public List<Entitlement> getBySearchParam(EntitlementSearchParam searchParam, PageMetadata pageMetadata) {
        return entitlementMapper.toEntitlementList(
                entitlementDao.getBySearchParam(
                        searchParam, pageMetadata == null ? new PageMetadata() : pageMetadata));
    }

    public List<Entitlement> getBySearchParam(EntitlementSearchParam searchParam) {
        return entitlementMapper.toEntitlementList(entitlementDao.getBySearchParam(searchParam, new PageMetadata()));
    }

    public void delete(Entitlement entitlement, String reason) {
        EntitlementEntity entitlementEntity = entitlementMapper.toEntitlementEntity(entitlement);
        entitlementEntity.setStatus(EntitlementStatus.DELETED);
        entitlementEntity.setStatusReason(reason);
        entitlementEntity.setManagedLifecycle(false);
        entitlementEntity.setConsumable(false);
        entitlementEntity.setUseCount(0);
        entitlementHistoryDao.insert(new EntitlementHistoryEntity(DELETE, entitlementEntity));
        entitlementDao.update(entitlementEntity);
//        entitlementRedisDao.update(entitlementEntity);
    }

    public Entitlement getByTrackingUuid(UUID trackingUuid) {
//        return entitlementMapper.toEntitlement(entitlementRedisDao.getByTrackingUuid(trackingUuid));
        return entitlementMapper.toEntitlement(entitlementDao.getByTrackingUuid(trackingUuid));
    }

    public Entitlement getExistingManagedEntitlement(Long userId, Long definitionId) {
//        return entitlementMapper.toEntitlement(entitlementRedisDao.getExistingManagedEntitlement(
//          userId, definitionId));
        return entitlementMapper.toEntitlement(entitlementDao.getExistingManagedEntitlement(userId, definitionId));
    }

    public Boolean existWithEntitlementDefinition(Long definitionId){
        return entitlementDao.existEntitlementDefinition(definitionId);
    }
}
