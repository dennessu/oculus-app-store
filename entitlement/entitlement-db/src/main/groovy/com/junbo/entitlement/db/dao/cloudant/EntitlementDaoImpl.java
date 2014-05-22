/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.cloudant;

import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantSearchResult;
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.common.id.ItemId;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.dao.EntitlementDao;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;

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
        entitlement.setUpdatedBy(123L);
        entitlement.setUpdatedTime(new Date());
        return super.cloudantPost(entitlement);
    }

    @Override
    public EntitlementEntity get(Long entitlementId) {
        return super.cloudantGet(entitlementId.toString());
    }

    @Override
    public EntitlementEntity update(EntitlementEntity entitlement) {
        entitlement.setRev(entitlement.getRev() + 1);
        entitlement.setIsDeleted(false);
        return super.cloudantPut(entitlement);
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

        CloudantSearchResult<EntitlementEntity> searchResult = super.search("search", query, size, bookmark);
        Results<EntitlementEntity> results = new Results<>();
        results.setItems(searchResult.getResults());
        //use next to store bookmark
        Link next = new Link();
        next.setHref(searchResult.getBookmark());
        results.setNext(next);
        return results;
    }

    private String addSearchParams(StringBuilder sb, EntitlementSearchParam entitlementSearchParam) throws ParseException {
        if (entitlementSearchParam.getIsBanned() != null) {
            sb.append(" AND isBanned:" + entitlementSearchParam.getIsBanned());
        }
        if (!StringUtils.isEmpty(entitlementSearchParam.getType())) {
            sb.append(" AND type:" + entitlementSearchParam.getType().toUpperCase());
        }

        Long now = EntitlementContext.current().getNow().getTime();
        if (Boolean.FALSE.equals(entitlementSearchParam.getIsActive())) {
            sb.append(" AND ( grantTime:{" + now + " TO " + EntitlementConsts.MAX_DATE + "}" +
                    " OR expirationTime:{" + EntitlementConsts.MIN_DATE + " TO " + now + "}" +
                    " OR useCount:0" +
                    " OR isBanned:true )");
        } else if (Boolean.TRUE.equals(entitlementSearchParam.getIsActive())) {
            sb.append(
                    " AND useCount:{1 TO " + EntitlementConsts.UNCONSUMABLE_USECOUNT + "}" +
                            " AND grantTime:{" + EntitlementConsts.MIN_DATE + " TO " + now + "}" +
                            " AND expirationTime:{" + now + " TO " + EntitlementConsts.MAX_DATE + "}" +
                            " AND isBanned:false");
        }

        Set<ItemId> itemIds = entitlementSearchParam.getItemIds();
        if (!CollectionUtils.isEmpty(itemIds)) {
            sb.append(" AND itemId:(");
            Iterator<ItemId> iterator = itemIds.iterator();
            while (iterator.hasNext()) {
                ItemId itemId = iterator.next();
                if (!iterator.hasNext()) {
                    sb.append(longToString(itemId.getValue()) + ")");
                } else {
                    sb.append(longToString(itemId.getValue()) + " OR ");
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
                sb.append(" AND grantTime:{" + EntitlementConsts.MIN_DATE + " TO " + endGrant + "}");
            }
        } else {
            Long startGrant = parse(entitlementSearchParam.getStartGrantTime());
            if (isEndGrantNull) {
                sb.append(" AND grantTime:{" + startGrant + " TO " + EntitlementConsts.MAX_DATE + "}");
            } else {
                sb.append(" AND grantTime:{" + startGrant + " TO " + endGrant + "}");
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
                sb.append(" AND expirationTime:{" + EntitlementConsts.MIN_DATE + " TO " + endExpiration + "}");
            }
        } else {
            Long startExpiration = parse(entitlementSearchParam.getStartExpirationTime());
            if (isEndExpirationNull) {
                sb.append(" AND expirationTime:{" + startExpiration + " TO " + EntitlementConsts.MAX_DATE + "}");
            } else {
                sb.append(" AND expirationTime:{" + startExpiration + " TO " + endExpiration + "}");
            }
        }

        if (!StringUtils.isEmpty(entitlementSearchParam.getLastModifiedTime())) {
            sb.append(" AND updatedTime:{" + parse(entitlementSearchParam.getLastModifiedTime()) + " TO " + EntitlementConsts.MAX_DATE + "}");
        }

        return sb.toString();
    }

    @Override
    public EntitlementEntity getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        List<EntitlementEntity> results = super.queryView("byTrackingUuid", trackingUuid.toString());
        return results.size() == 0 ? null : results.get(0);
    }

    @Override
    public EntitlementEntity get(Long userId, Long itemId, String type) {
        String key = userId.toString() + ":" + itemId.toString() + ":" + (type == null ? EntitlementConsts.NO_TYPE : type.toUpperCase());
        List<EntitlementEntity> results = super.queryView("byUserIdAndItemIdAndType", key);
        return results.size() == 0 ? null : results.get(0);
    }

    protected CloudantViews views = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();
        Map<String, CloudantIndex> indexMap = new HashMap<>();

        CloudantView byTrackingUuid = new CloudantView();
        byTrackingUuid.setMap("function(doc) {" +
                "emit(doc.trackingUuid.toString(), doc._id)" +
                "}");
        byTrackingUuid.setResultClass(String.class);
        viewMap.put("byTrackingUuid", byTrackingUuid);

        CloudantView byUserIdAndItemIdAndType = new CloudantView();
        byUserIdAndItemIdAndType.setMap("function(doc) {" +
                "emit(doc.userId.toString() + \':\' + " +
                "doc.itemId.toString() + \':\' + doc.type, doc._id)" +
                "}");
        byUserIdAndItemIdAndType.setResultClass(String.class);
        viewMap.put("byUserIdAndItemIdAndType", byUserIdAndItemIdAndType);

        CloudantIndex searchIndex = new CloudantIndex();
        searchIndex.setIndex("function(doc) {" +
                "index(\'userId\', doc.userId);" +
                "index(\'isDeleted\', doc.isDeleted);" +
                "index(\'type\', doc.type);" +
                "index(\'isBanned\', doc.isBanned);" +
                "index(\'itemId\', doc.itemId);" +
                "index(\'useCount\', doc.useCount);" +
                "index(\'grantTime\', doc.grantTime);" +
                "index(\'expirationTime\', doc.expirationTime);" +
                "index(\'updatedTime\', doc.updatedTime);}");
        searchIndex.setResultClass(String.class);
        indexMap.put("search", searchIndex);

        setViews(viewMap);
        setIndexes(indexMap);
    }};

    private Long parse(String date) throws ParseException {
        return EntitlementConsts.DATE_FORMAT.parse(date).getTime();
    }

    private String longToString(Long value) {
        return "'" + value + "'";
    }
}
