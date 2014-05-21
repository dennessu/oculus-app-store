/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.postgresql;

import com.junbo.common.id.ItemId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.def.Function;
import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
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
    public Results<EntitlementEntity> getBySearchParam(
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
        queryStringBuilder.append(" and is_deleted = false");
        Query q = currentSession(entitlementSearchParam.getUserId().getValue()).createSQLQuery(
                queryStringBuilder.toString()).addEntity(EntitlementEntity.class);
        q = addPageMeta(addParams(q, params), pageMetadata);
        Results<EntitlementEntity> results = new Results<>();
        results.setItems(q.list());
        return results;
    }

    private void addSearchParam(EntitlementSearchParam entitlementSearchParam,
                                StringBuilder queryStringBuilder,
                                Map<String, Object> params) throws ParseException {
        if (entitlementSearchParam.getIsBanned() != null) {
            addSingleParam("is_banned", "isBanned",
                    entitlementSearchParam.getIsBanned(), "=", queryStringBuilder, params);
        }

        if (!StringUtils.isEmpty(entitlementSearchParam.getType())) {
            addSingleParam("type", "type",
                    entitlementSearchParam.getType().toUpperCase(), "=", queryStringBuilder, params);
        }

        Date now = EntitlementContext.current().getNow();
        if (Boolean.FALSE.equals(entitlementSearchParam.getIsActive())) {
            queryStringBuilder.append(" and ( grant_time >= (:now)" +
                    " or expiration_time <= (:now)" +
                    " or use_count = 0" +
                    " or is_banned = true )");
            params.put("now", now);
        } else if (Boolean.TRUE.equals(entitlementSearchParam.getIsActive())) {
            queryStringBuilder.append(
                    " and use_count > 0" +
                            " and ( grant_time <= (:now)" +
                            " and expiration_time >= (:now) )" +
                            " and is_banned = false");
            params.put("now", now);
        }

        if (!CollectionUtils.isEmpty(entitlementSearchParam.getItemIds())) {
            addCollectionParam("item_id", "itemIds",
                    CommonUtils.select(entitlementSearchParam.getItemIds(),
                            new Function<Long, ItemId>() {
                                @Override
                                public Long apply(ItemId itemId) {
                                    return itemId.getValue();
                                }
                            }),
                    queryStringBuilder, params);
        }
        if (!StringUtils.isEmpty(entitlementSearchParam.getStartGrantTime())) {
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
        String queryString = "from EntitlementEntity where trackingUuid = (:trackingUuid) and isDeleted = false";
        Query q = currentSession(shardMasterId).createQuery(queryString).setParameter("trackingUuid", trackingUuid);
        return (EntitlementEntity) q.uniqueResult();
    }

    @Override
    public EntitlementEntity get(Long userId, Long itemId, String type) {
        String queryString = "from EntitlementEntity where userId = (:userId)" +
                " and itemId = (:itemId) and type = (:type) and isDeleted = false";
        Query q = currentSession(userId).createQuery(queryString)
                .setLong("userId", userId)
                .setLong("itemId", itemId)
                .setString("type", type == null ? EntitlementConsts.NO_TYPE : type.toUpperCase());
        return (EntitlementEntity) q.uniqueResult();
    }
}
