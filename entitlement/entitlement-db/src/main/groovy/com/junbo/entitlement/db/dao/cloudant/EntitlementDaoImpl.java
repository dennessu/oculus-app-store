/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.cloudant;

import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantSearchResult;
import com.junbo.common.id.ItemId;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;

/**
 * cloudantImpl of entitlementDao.
 */
public class EntitlementDaoImpl extends CloudantClient<EntitlementEntity> implements EntitlementDao {

    @Override
    public EntitlementEntity insert(EntitlementEntity entitlement) {
        entitlement.setIsDeleted(false);
        entitlement.setUpdatedBy(123L);
        entitlement.setUpdatedTime(new Date());
        return cloudantPostSync(entitlement);
    }

    @Override
    public EntitlementEntity get(String entitlementId) {
        EntitlementEntity result = cloudantGetSync(entitlementId);
        return result == null || result.getIsDeleted().equals(true) ? null : result;
    }

    @Override
    public EntitlementEntity update(EntitlementEntity entitlement, EntitlementEntity oldEntitlement) {
        if (entitlement.getIsDeleted() == null) {
            entitlement.setIsDeleted(false);
        }
        return cloudantPutSync(entitlement, oldEntitlement);
    }

    @Override
    public Results<EntitlementEntity> getBySearchParam(EntitlementSearchParam entitlementSearchParam, PageMetadata pageMetadata) {
        StringBuilder sb = new StringBuilder("userId:" + longToString(entitlementSearchParam.getUserId().getValue()));
        sb.append(" AND isDeleted:false");
        String query = null;
        try {
            query = addSearchParams(sb, entitlementSearchParam);
        } catch (ParseException e) {
            //just ignore. This is checked in service layer.
        }

        int size = pageMetadata.getCount() == null ||
                pageMetadata.getCount() > EntitlementConsts.MAX_PAGE_SIZE
                ? EntitlementConsts.DEFAULT_PAGE_SIZE : pageMetadata.getCount();
        String bookmark = pageMetadata.getBookmark();

        CloudantSearchResult<EntitlementEntity> searchResult = searchSync("search", query, size, bookmark);
        Results<EntitlementEntity> results = new Results<>();
        results.setItems(searchResult.getResults());
        //use next to store bookmark
        Link next = new Link();
        next.setHref(searchResult.getBookmark());
        results.setNext(next);
        results.setTotal(searchResult.getTotal());
        return results;
    }

    private String addSearchParams(StringBuilder sb, EntitlementSearchParam entitlementSearchParam) throws ParseException {
        if (entitlementSearchParam.getIsBanned() != null) {
            sb.append(" AND isBanned:" + entitlementSearchParam.getIsBanned());
        }
        if (!StringUtils.isEmpty(entitlementSearchParam.getType())) {
            sb.append(" AND type:" + entitlementSearchParam.getType().toUpperCase());
        }

        Long now = EntitlementContext.current().getNow().getTime() + 60000L;
        if (Boolean.FALSE.equals(entitlementSearchParam.getIsActive())) {
            sb.append(" AND ( grantTime:[" + now + " TO " + EntitlementConsts.MAX_DATE + "]" +
                    " OR expirationTime:[" + EntitlementConsts.MIN_DATE + " TO " + now + "]" +
                    " OR useCount:[" + EntitlementConsts.MIN_USECOUNT + " TO 0]" +
                    " OR isBanned:true )");
        } else if (Boolean.TRUE.equals(entitlementSearchParam.getIsActive())) {
            sb.append(
                    " AND useCount:{0 TO " + EntitlementConsts.UNCONSUMABLE_USECOUNT + "]" +
                            " AND grantTime:[" + EntitlementConsts.MIN_DATE + " TO " + now + "]" +
                            " AND expirationTime:[" + now + " TO " + EntitlementConsts.MAX_DATE + "]" +
                            " AND isBanned:false");
        }

        Set<ItemId> itemIds = entitlementSearchParam.getItemIds();
        if (!CollectionUtils.isEmpty(itemIds)) {
            sb.append(" AND itemId:(");
            Iterator<ItemId> iterator = itemIds.iterator();
            while (iterator.hasNext()) {
                ItemId itemId = iterator.next();
                if (!iterator.hasNext()) {
                    sb.append(itemId.getValue() + ")");
                } else {
                    sb.append(itemId.getValue() + " OR ");
                }
            }
        }

        Boolean isStartGrantNull = StringUtils.isEmpty(entitlementSearchParam.getStartGrantTime());
        Boolean isEndGrantNull = StringUtils.isEmpty(entitlementSearchParam.getEndGrantTime());
        Long endGrant = null;
        if (!isEndGrantNull) {
            endGrant = parse(entitlementSearchParam.getEndGrantTime());
        }
        if (isStartGrantNull) {
            if (!isEndGrantNull) {
                sb.append(" AND grantTime:[" + EntitlementConsts.MIN_DATE + " TO " + endGrant + "]");
            }
        } else {
            Long startGrant = parse(entitlementSearchParam.getStartGrantTime());
            if (isEndGrantNull) {
                sb.append(" AND grantTime:[" + startGrant + " TO " + EntitlementConsts.MAX_DATE + "]");
            } else {
                sb.append(" AND grantTime:[" + startGrant + " TO " + endGrant + "]");
            }
        }

        Boolean isStartExpirationNull = StringUtils.isEmpty(entitlementSearchParam.getStartExpirationTime());
        Boolean isEndExpirationNull = StringUtils.isEmpty(entitlementSearchParam.getEndExpirationTime());
        Long endExpiration = null;
        if (!isEndExpirationNull) {
            endExpiration = parse(entitlementSearchParam.getEndExpirationTime());
        }
        if (isStartExpirationNull) {
            if (!isEndExpirationNull) {
                sb.append(" AND expirationTime:[" + EntitlementConsts.MIN_DATE + " TO " + endExpiration + "]");
            }
        } else {
            Long startExpiration = parse(entitlementSearchParam.getStartExpirationTime());
            if (isEndExpirationNull) {
                sb.append(" AND expirationTime:[" + startExpiration + " TO " + EntitlementConsts.MAX_DATE + "]");
            } else {
                sb.append(" AND expirationTime:[" + startExpiration + " TO " + endExpiration + "]");
            }
        }

        if (!StringUtils.isEmpty(entitlementSearchParam.getLastModifiedTime())) {
            sb.append(" AND updatedTime:[" + parse(entitlementSearchParam.getLastModifiedTime()) + " TO " + EntitlementConsts.MAX_DATE + "]");
        }

        return sb.toString();
    }

    @Override
    public EntitlementEntity getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        List<EntitlementEntity> results = queryViewSync("byTrackingUuid", trackingUuid.toString());
        return results.size() == 0 ? null : results.get(0);
    }

    @Override
    public EntitlementEntity get(Long userId, String itemId, String type) {
        String key = userId.toString() + ":" + itemId + ":" + (type == null ? EntitlementConsts.NO_TYPE : type.toUpperCase());
        List<EntitlementEntity> results = queryViewSync("byUserIdAndItemIdAndType", key);
        return results.size() == 0 ? null : results.get(0);
    }

    private Long parse(String date) throws ParseException {
        return EntitlementConsts.DATE_FORMAT.parse(date).getTime();
    }

    private String longToString(Long value) {
        return "'" + value + "'";
    }
}
