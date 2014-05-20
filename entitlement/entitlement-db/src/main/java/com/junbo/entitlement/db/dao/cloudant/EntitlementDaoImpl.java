/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.cloudant;

import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * cloudantImpl of entitlementDao.
 */
public class EntitlementDaoImpl extends CloudantClient<EntitlementEntity> implements EntitlementDao {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    protected Long generateId(Long shardId) {
        return idGenerator.nextId(shardId);
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views;
    }

    @Override
    public EntitlementEntity insert(EntitlementEntity entitlement) {
        entitlement.setpId(generateId(entitlement.getShardMasterId()));
        entitlement.setIsDeleted(false);
        return super.cloudantPost(entitlement);
    }

    @Override
    public EntitlementEntity get(Long entitlementId) {
        return super.cloudantGet(entitlementId.toString());
    }

    @Override
    public EntitlementEntity update(EntitlementEntity entitlement) {
        return super.cloudantPut(entitlement);
    }

    @Override
    public List<EntitlementEntity> getBySearchParam(EntitlementSearchParam entitlementSearchParam, PageMetadata pageMetadata) {
        return null;
    }

    @Override
    public EntitlementEntity getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        List<EntitlementEntity> results = super.queryView("byTrackingUuid", trackingUuid.toString());
        return results.size() == 0 ? null : results.get(0);
    }

    @Override
    public EntitlementEntity get(Long userId, Long itemId, String type) {
        List<EntitlementEntity> results = super.queryView("byUserIdAndItemIdAndType",
                userId.toString() + ":" + itemId.toString() + ":" + type);
        return results.size() == 0 ? null : results.get(0);
    }

    protected CloudantViews views = new CloudantViews() {{
        CloudantView byTrackingUuid = new CloudantView();
        byTrackingUuid.setMap("function(doc) {" +
                "  emit(doc.trackingUuid.toString(), doc._id)" +
                "}");
        byTrackingUuid.setResultClass(String.class);
        CloudantView byUserIdAndItemIdAndType = new CloudantView();
        byUserIdAndItemIdAndType.setMap("function(doc) {" +
                "  emit(doc.userId.toString() + \":\" + " +
                "doc.itemId.toString() + \":\" + doc.type, doc._id)" +
                "}");
        byUserIdAndItemIdAndType.setResultClass(String.class);
        Map<String, CloudantView> viewMap = new HashMap<>();
        viewMap.put("byTrackingUuid", byTrackingUuid);
        viewMap.put("byUserIdAndItemIdAndType", byUserIdAndItemIdAndType);
        setViews(viewMap);
    }};
}
