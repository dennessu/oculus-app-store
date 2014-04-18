/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.hibernate;

import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.def.Function;
import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.spec.def.EntitlementType;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.hibernate.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
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
        try {
            addSearchParam(entitlementSearchParam, queryStringBuilder, params);
        } catch (ParseException e) {
            //just ignore. This is checked in service layer.
        }
        queryStringBuilder.append(" and is_deleted is null");
        Query q = currentSession(entitlementSearchParam.getUserId().getValue()).createSQLQuery(
                queryStringBuilder.toString()).addEntity(EntitlementEntity.class);
        q = addPageMeta(addParams(q, params), pageMetadata);
        return q.list();
    }

    private void addSearchParam(EntitlementSearchParam entitlementSearchParam,
                                StringBuilder queryStringBuilder,
                                Map<String, Object> params) throws ParseException {
        if (CommonUtils.isNotNull(entitlementSearchParam.getClientId())) {
            queryStringBuilder.append(" and '{\"\\\"" +
                    entitlementSearchParam.getClientId() +
                    "\\\"\"}'\\:\\:text[] <@ (json_val_arr(in_app_context))");
        }
        if (entitlementSearchParam.getIsBanned() != null) {
            addSingleParam("is_banned", "isBanned",
                    entitlementSearchParam.getIsBanned(), "=", queryStringBuilder, params);
        }
        if (!Boolean.TRUE.equals(entitlementSearchParam.getIsBanned())) {
            Date now = EntitlementContext.current().getNow();
            if (Boolean.FALSE.equals(entitlementSearchParam.getIsActive())) {
                queryStringBuilder.append(" and ( grant_time >= (:now)" +
                        " or expiration_time <= (:now)" +
                        " or use_count = 0 )");
            } else {
                queryStringBuilder.append(
                        " and ( use_count is null or use_count > 0 )" +
                                " and ( grant_time <= (:now)" +
                                " and ( expiration_time is null or expiration_time >= (:now) ))");
            }
            params.put("now", now);
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
        if (!StringUtils.isEmpty(entitlementSearchParam.getStartGrantTime())) {
            addSingleParam("grant_time", "startGrantTime",
                    entitlementSearchParam.getStartGrantTime(),
                    ">=", queryStringBuilder, params);
        }
        if (!StringUtils.isEmpty(entitlementSearchParam.getEndGrantTime())) {
            addSingleParam("grant_time", "startGrantTime",
                    EntitlementConsts.DATE_FORMAT.parse(entitlementSearchParam.getStartGrantTime()),
                    ">=", queryStringBuilder, params);
        }
        if (!StringUtils.isEmpty(entitlementSearchParam.getEndGrantTime())) {
            addSingleParam("grant_time", "endGrantTime",
                    EntitlementConsts.DATE_FORMAT.parse(entitlementSearchParam.getEndGrantTime()),
                    "<=", queryStringBuilder, params);
        }
        if (!StringUtils.isEmpty(entitlementSearchParam.getStartExpirationTime())) {
            addSingleParam("expiration_time", "startExpirationTime",
                    EntitlementConsts.DATE_FORMAT.parse(entitlementSearchParam.getStartExpirationTime()),
                    ">=", queryStringBuilder, params);
        }
        if (!StringUtils.isEmpty(entitlementSearchParam.getEndExpirationTime())) {
            addSingleParam("expiration_time", "endExpirationTime",
                    EntitlementConsts.DATE_FORMAT.parse(entitlementSearchParam.getEndExpirationTime()),
                    "<=", queryStringBuilder, params);
        }
        if (!StringUtils.isEmpty(entitlementSearchParam.getLastModifiedTime())) {
            addSingleParam("modified_time", "lastModifiedTime",
                    EntitlementConsts.DATE_FORMAT.parse(entitlementSearchParam.getLastModifiedTime()),
                    ">=", queryStringBuilder, params);
        }
    }

    @Override
    public EntitlementEntity getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        String queryString = "from EntitlementEntity where trackingUuid = (:trackingUuid) and isDeleted = null";
        Query q = currentSession(shardMasterId).createQuery(queryString).setParameter("trackingUuid", trackingUuid);
        return (EntitlementEntity) q.uniqueResult();
    }
}
