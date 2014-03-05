/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.redis;

import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Just a demo. Should consider transaction, index and more other things.
 */
public class EntitlementDaoImpl implements EntitlementDao {
    @Autowired
    private Jedis jedis;

    @Override
    public Long insert(EntitlementEntity entitlement) {
        jedis.set("entitlement:" + entitlement.getEntitlementId(),
                JsonMarshaller.marshall(entitlement));
        if (entitlement.getTrackingUuid() != null) {
            jedis.set("trackingUuid:" + entitlement.getTrackingUuid(),
                    entitlement.getEntitlementId().toString());
        }
        jedis.sadd("definition:" + entitlement.getEntitlementDefinitionId() +
                ":managed:" + entitlement.getManagedLifecycle(),
                entitlement.getEntitlementId().toString());
        return entitlement.getEntitlementId();
    }

    @Override
    public EntitlementEntity get(Long entitlementId) {
        return JsonMarshaller.unmarshall(EntitlementEntity.class,
                jedis.get("entitlement:" + entitlementId.toString()));
    }

    @Override
    public Long update(EntitlementEntity entitlement) {
        jedis.set("entitlement:" + entitlement.getEntitlementId(),
                JsonMarshaller.marshall(entitlement));
        if (entitlement.getTrackingUuid() != null) {
            jedis.set("trackingUuid:" + entitlement.getTrackingUuid(),
                    entitlement.getEntitlementId().toString());
        }
        return entitlement.getEntitlementId();
    }

    @Override
    public List<EntitlementEntity> getBySearchParam(
            EntitlementSearchParam entitlementSearchParam, PageMetadata pageMetadata) {
        return null;
    }

    @Override
    public EntitlementEntity getByTrackingUuid(UUID trackingUuid) {
        String id = jedis.get("trackingUuid:" + trackingUuid);
        return getById(id);
    }

    @Override
    public EntitlementEntity getExistingManagedEntitlement(
            Long userId, Long definitionId) {
        Set<String> ids = jedis.smembers("definition:" + definitionId + ":managed:true");
        String id = null;
        if (ids.iterator().hasNext()) {
            id = ids.iterator().next();
        }
        return getById(id);
    }

    private EntitlementEntity getById(String id) {
        if (!StringUtils.isEmpty(id)) {
            return JsonMarshaller.unmarshall(
                    EntitlementEntity.class, jedis.get("entitlement:" + id));
        }
        return null;
    }
}
