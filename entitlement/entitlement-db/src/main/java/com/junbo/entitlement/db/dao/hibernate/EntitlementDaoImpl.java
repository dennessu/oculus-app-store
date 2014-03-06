/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.hibernate;

import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.hibernate.Query;

import java.util.*;

/**
 * Hibernate Impl of Entitlement Dao.
 */
public class EntitlementDaoImpl extends BaseDao<EntitlementEntity> implements EntitlementDao {
    @Override
    public List<EntitlementEntity> getBySearchParam(
            EntitlementSearchParam entitlementSearchParam, PageMetadata pageMetadata) {
        StringBuilder queryStringBuilder = new StringBuilder(
                "select * from entitlement" +
                        " where user_id = (:userId)" +
                        " and developer_id = (:developerId)");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", entitlementSearchParam.getUserId());
        params.put("developerId", entitlementSearchParam.getDeveloperId());
        addSearchParam(entitlementSearchParam, queryStringBuilder, params);
        Query q = currentSession().createSQLQuery(
                queryStringBuilder.toString()).addEntity(EntitlementEntity.class);
        q = addPageMeta(addParams(q, params), pageMetadata);
        return q.list();
    }

    private void addSearchParam(EntitlementSearchParam entitlementSearchParam,
                                StringBuilder queryStringBuilder,
                                Map<String, Object> params) {
        if (entitlementSearchParam.getStatus() != null) {
            Date now = EntitlementContext.now();
            EntitlementStatus status = EntitlementStatus.valueOf(entitlementSearchParam.getStatus());
            if (EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS.contains(status)) {
                queryStringBuilder.append(" and status = (:status)");
            } else if (status.equals(EntitlementStatus.ACTIVE)) {
                queryStringBuilder.append(" and ( status = (:status)" +
                        " or ( managed_lifecycle = true" +
                        " and ( consumable = false or ( consumable = true and use_count > 0 ))" +
                        " and ( grant_time <= (:now) and ( expiration_time is null or expiration_time >= (:now) ))" +
                        " ))");
                params.put("now", now);
            } else if (status.equals(EntitlementStatus.PENDING)) {
                queryStringBuilder.append(" and ( status = (:status)" +
                        " or ( managed_lifecycle = true" +
                        " and grant_time >= (:now)" +
                        " ))");
                params.put("now", now);
            } else if (status.equals(EntitlementStatus.DISABLED)) {
                queryStringBuilder.append(" and ( status = (:status)" +
                        " or ( managed_lifecycle = true" +
                        " and ( consumable = true and use_count < 1" +
                        " or expiration_time <= (:now) )" +
                        " ))");
                params.put("now", now);
            }
            params.put("status", status.ordinal());
        }

        addCollectionParam("entitlement_group", "groups",
                entitlementSearchParam.getGroups(), queryStringBuilder, params);
        addCollectionParam("tag", "tags", entitlementSearchParam.getTags(), queryStringBuilder, params);
        addCollectionParam("entitlement_definition_id", "definitionIds",
                entitlementSearchParam.getDefinitionIds(), queryStringBuilder, params);
        if (CommonUtils.isNotNull(entitlementSearchParam.getType())) {
            addSingleParam("type", "type",
                    EntitlementType.valueOf(entitlementSearchParam.getType()).ordinal(),
                    "=", queryStringBuilder, params);
        }
        addCollectionParam("offer_id", "offerIds", entitlementSearchParam.getOfferIds(), queryStringBuilder, params);
        addSingleParam("grant_time", "startGrantTime",
                entitlementSearchParam.getStartGrantTime(),
                ">=", queryStringBuilder, params);
        if (CommonUtils.isNotNull(entitlementSearchParam.getEndGrantTime())) {
            addSingleParam("grant_time", "endGrantTime",
                    new Date(entitlementSearchParam.getEndGrantTime().getTime() + 1 * 24 * 3600 * 1000),
                    "<=", queryStringBuilder, params);
        }
        addSingleParam("expiration_time", "startExpirationTime",
                entitlementSearchParam.getStartExpirationTime(),
                ">=", queryStringBuilder, params);
        if (CommonUtils.isNotNull(entitlementSearchParam.getEndExpirationTime())) {
            addSingleParam("expiration_time", "endExpirationTime",
                    new Date(entitlementSearchParam.getEndExpirationTime().getTime() + 1 * 24 * 3600 * 1000),
                    "<=", queryStringBuilder, params);
        }
        addSingleParam("modified_time", "lastModifiedTime",
                entitlementSearchParam.getLastModifiedTime(),
                ">=", queryStringBuilder, params);
    }


    @Override
    public EntitlementEntity getByTrackingUuid(UUID trackingUuid) {
        String queryString = "from EntitlementEntity where trackingUuid = (:trackingUuid)";
        Query q = currentSession().createQuery(queryString).setParameter("trackingUuid", trackingUuid);
        return (EntitlementEntity) q.uniqueResult();
    }

    @Override
    public EntitlementEntity getExistingManagedEntitlement(Long userId, Long definitionId) {
        String queryString = "from EntitlementEntity" +
                " where managedLifecycle = true" +
                " and userId = (:userId)" +
                " and entitlementDefinitionId = (:definitionId)" +
                " and status not in (:notManagedStatus)";
        Query q = currentSession().createQuery(queryString)
                .setLong("userId", userId)
                .setLong("definitionId", definitionId)
                .setParameterList("notManagedStatus", EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS);
        return (EntitlementEntity) q.uniqueResult();
    }

    @Override
    public Boolean existEntitlementDefinition(Long definitionId) {
        String queryString = "from EntitlementEntity" +
                " where entitlementDefinitionId = (:definitionId)" +
                " and status not in (:notManagedStatus)";
        Query q = currentSession().createQuery(queryString)
                .setLong("definitionId", definitionId)
                .setParameterList("notManagedStatus", EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS);
        return q.list().size() != 0;
    }
}
