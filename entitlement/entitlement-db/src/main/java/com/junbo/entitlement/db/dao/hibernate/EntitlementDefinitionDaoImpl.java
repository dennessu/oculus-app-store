/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.hibernate;

import com.junbo.entitlement.db.dao.EntitlementDefinitionDao;
import com.junbo.entitlement.db.entity.EntitlementDefinitionEntity;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.hibernate.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Hibernate Impl of EntitlementDefinition Dao.
 */
public class EntitlementDefinitionDaoImpl extends BaseDao<EntitlementDefinitionEntity>
        implements EntitlementDefinitionDao {
    @Override
    public EntitlementDefinitionEntity get(Long id) {
        EntitlementDefinitionEntity entitlementDefinitionEntity =
                (EntitlementDefinitionEntity)
                        currentSession().get(EntitlementDefinitionEntity.class, id);
        if (entitlementDefinitionEntity != null
                && "DELETED".equals(entitlementDefinitionEntity.getStatus())) {
            return null;
        }
        return entitlementDefinitionEntity;
    }

    @Override
    public List<EntitlementDefinitionEntity> getByParams(Long developerId, String group,
                                                         String tag, EntitlementType type, PageMetadata pageMetadata) {
        StringBuilder queryString = new StringBuilder("from EntitlementDefinitionEntity" +
                " where status is null" +
                " and developerId = (:developerId)");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("developerId", developerId);

        addSingleParam("type", "type", type, "=", queryString, params);
        addSingleParam("group", "group", group, "=", queryString, params);
        addSingleParam("tag", "tag", tag, "=", queryString, params);

        Query q = currentSession().createQuery(queryString.toString());

        q = addPageMeta(addParams(q, params), pageMetadata);

        return q.list();
    }

    @Override
    public EntitlementDefinitionEntity getByTrackingUuid(UUID trackingUuid) {
        String queryString = "from EntitlementDefinitionEntity where trackingUuid = (:trackingUuid)";
        Query q = currentSession().createQuery(queryString).setParameter("trackingUuid", trackingUuid);
        return (EntitlementDefinitionEntity) q.uniqueResult();
    }
}
