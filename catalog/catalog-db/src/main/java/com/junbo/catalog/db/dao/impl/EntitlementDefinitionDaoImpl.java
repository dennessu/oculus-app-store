/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.dao.EntitlementDefinitionDao;
import com.junbo.catalog.db.entity.EntitlementDefinitionEntity;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import org.hibernate.Query;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Hibernate Impl of EntitlementDefinition Dao.
 */
public class EntitlementDefinitionDaoImpl extends BaseDaoImpl<EntitlementDefinitionEntity>
        implements EntitlementDefinitionDao {
    @Override
    public List<EntitlementDefinitionEntity> getByParams(Long developerId, String clientId, String group, String tag,
                                                         EntitlementType type, Boolean isConsumable, PageableGetOptions pageableGetOptions) {
        StringBuilder queryString = new StringBuilder("select * from entitlement_definition" +
                " where developer_id = (:developerId)");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("developerId", developerId);

        if (!StringUtils.isEmpty(clientId)) {
            queryString.append(" and '{\"\\\"" +
                    clientId +
                    "\\\"\"}'\\:\\:text[] <@ (json_val_arr(in_app_context))");
        }
        if (type != null) {
            queryString.append(" and type = (:type)");
            params.put("type", type);
        }
        if (!StringUtils.isEmpty(group)) {
            queryString.append(" and entitlement_group = (:group)");
            params.put("group", group);
        }
        if (!StringUtils.isEmpty(tag)) {
            queryString.append(" and tag = (:tag)");
            params.put("tag", tag);
        }
        if (isConsumable != null) {
            queryString.append(" and consumable = (:isConsumable)");
            params.put("isConsumable", isConsumable);
        }

        queryString.append(" and deleted = false");

        Query q = currentSession().createSQLQuery(queryString.toString()).addEntity(this.getEntityType());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            q.setParameter(entry.getKey(), entry.getValue());
        }
        q.setMaxResults(pageableGetOptions.getSize()).setFirstResult(pageableGetOptions.getStart());

        return q.list();
    }

    @Override
    public EntitlementDefinitionEntity getByTrackingUuid(UUID trackingUuid) {
        String queryString = "from EntitlementDefinitionEntity where trackingUuid = (:trackingUuid)";
        Query q = currentSession().createQuery(queryString).setParameter("trackingUuid", trackingUuid);
        return (EntitlementDefinitionEntity) q.uniqueResult();
    }

    @Override
    public Long update(EntitlementDefinitionEntity entity) {
        EntitlementDefinitionEntity existed = (EntitlementDefinitionEntity)
                currentSession().load(EntitlementDefinitionEntity.class, entity.getId());
        entity.setCreatedTime(existed.getCreatedTime());
        entity.setCreatedBy(existed.getCreatedBy());
        entity.setUpdatedBy(Constants.SYSTEM_INTERNAL); //TODO
        entity.setUpdatedTime(Utils.now());
        currentSession().merge(entity);
        return entity.getId();
    }
}
