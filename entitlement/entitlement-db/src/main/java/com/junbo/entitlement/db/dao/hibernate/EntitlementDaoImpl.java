/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.hibernate;

import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.OfferId;
import com.junbo.entitlement.common.def.Function;
import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.hibernate.Query;
import org.springframework.util.CollectionUtils;

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
                        " where user_id = (:userId)");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", entitlementSearchParam.getUserId().getValue());

        addSearchParam(entitlementSearchParam, queryStringBuilder, params);
        Query q = currentSession().createSQLQuery(
                queryStringBuilder.toString()).addEntity(EntitlementEntity.class);
        q = addPageMeta(addParams(q, params), pageMetadata);
        return q.list();
    }

    private void addSearchParam(EntitlementSearchParam entitlementSearchParam,
                                StringBuilder queryStringBuilder,
                                Map<String, Object> params) {
        if (CommonUtils.isNotNull(entitlementSearchParam.getDeveloperId())) {
            addSingleParam("developer_id", "developerId",
                    entitlementSearchParam.getDeveloperId().getValue(),
                    "=", queryStringBuilder, params);
        }
        if (entitlementSearchParam.getStatus() != null) {
            Date now = EntitlementContext.current().getNow();
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
            params.put("status", status.getId());
        } else {
            //default not to search DELETED and BANNED Entitlement
            queryStringBuilder.append(" and status >= 0");
        }

        addCollectionParam("entitlement_group", "groups",
                entitlementSearchParam.getGroups(), queryStringBuilder, params);
        addCollectionParam("tag", "tags", entitlementSearchParam.getTags(), queryStringBuilder, params);
        if (!CollectionUtils.isEmpty(entitlementSearchParam.getDefinitionIds())) {
            addCollectionParam("entitlement_definition_id", "definitionIds",
                    CommonUtils.select(entitlementSearchParam.getDefinitionIds(),
                            new Function<Long, EntitlementDefinitionId>() {
                                @Override
                                public Long apply(EntitlementDefinitionId entitlementDefinitionId) {
                                    return entitlementDefinitionId.getValue();
                                }
                            }),
                    queryStringBuilder, params);
        }
        if (CommonUtils.isNotNull(entitlementSearchParam.getType())) {
            addSingleParam("type", "type",
                    EntitlementType.valueOf(entitlementSearchParam.getType()).getId(),
                    "=", queryStringBuilder, params);
        }
        if (!CollectionUtils.isEmpty(entitlementSearchParam.getOfferIds())) {
            addCollectionParam("offer_id", "offerIds",
                    CommonUtils.select(entitlementSearchParam.getOfferIds(),
                            new Function<Long, OfferId>() {
                                @Override
                                public Long apply(OfferId offerId) {
                                    return offerId.getValue();
                                }
                            }),
                    queryStringBuilder, params);
        }
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
                " where userId = (:userId)" +
                " and entitlementDefinitionId = (:definitionId)" +
                " and status >= 0" +
                " and managedLifecycle = true";
        Query q = currentSession().createQuery(queryString)
                .setLong("userId", userId)
                .setLong("definitionId", definitionId);
        return (EntitlementEntity) q.uniqueResult();
    }

    @Override
    public EntitlementEntity getExistingManagedEntitlement(
            Long userId, EntitlementType type, Long developerId, String group, String tag) {
        String queryString = "from EntitlementEntity" +
                " where userId =(:userId)" +
                " and developerId = (:developerId)" +
                " and type = (:type)" +
                " and group = (:group)" +
                " and tag = (:tag)" +
                " and status >= 0" +
                " and managedLifecycle = true";
        Query q = currentSession().createQuery(queryString)
                .setLong("userId", userId)
                .setLong("developerId", developerId)
                .setInteger("type", type.getId())
                .setString("group", group)
                .setString("tag", tag);
        return (EntitlementEntity) q.uniqueResult();
    }
}
