/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.cloudant;

import com.junbo.common.id.ItemId;
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

import java.util.*;

/**
 * cloudantImpl of entitlementDao.
 */
public class EntitlementDaoImpl extends EntitlementCloudantClient<EntitlementEntity> implements EntitlementDao {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    protected Long generateId(Long shardId) {
        return idGenerator.nextId(shardId);
    }

    @Override
    protected EntitlementCloudantViews getCloudantViews() {
        return views;
    }

    @Override
    public EntitlementEntity insert(EntitlementEntity entitlement) {
        entitlement.setpId(generateId(entitlement.getShardMasterId()));
        entitlement.setUpdatedBy(123L); //TODO
        entitlement.setUpdatedTime(new Date()); //TODO
        entitlement.setIsDeleted(false);
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
    public List<EntitlementEntity> getBySearchParam(EntitlementSearchParam entitlementSearchParam, PageMetadata pageMetadata) {
        StringBuilder sb = new StringBuilder("userId:" + entitlementSearchParam.getUserId());
        sb.append(" AND isDeleted:false");
        if (entitlementSearchParam.getIsBanned() != null) {
            sb.append(" AND isBanned:" + entitlementSearchParam.getIsBanned());
        }
        if (!StringUtils.isEmpty(entitlementSearchParam.getType())) {
            sb.append("AND type:" + entitlementSearchParam.getType().toUpperCase());
        }

        String now = format(EntitlementContext.current().getNow());
        if (Boolean.FALSE.equals(entitlementSearchParam.getIsActive())) {
            sb.append(" AND ( grantTime:{" + now + " TO " + EntitlementConsts.MAX_DATE + "}" +
                    " OR expirationTime:{" + EntitlementConsts.MIN_DATE + " TO " + now + "}" +
                    " OR useCount:0" +
                    " OR isBanned:true )");
        } else if (Boolean.TRUE.equals(entitlementSearchParam.getIsActive())) {
            sb.append(
                    " AND useCount:[{1 TO " + EntitlementConsts.UNCONSUMABLE_USECOUNT + "}" +
                            " AND grantTime:{" + EntitlementConsts.MIN_DATE + " TO " + now + "}" +
                            " AND expirationTime:{" + now + " TO " + EntitlementConsts.MAX_DATE + "}" +
                            " AND isBanned:false");
        }

        Set<ItemId> itemIds = entitlementSearchParam.getItemIds();
        if (!CollectionUtils.isEmpty(itemIds)) {
            sb.append(" AND itemId:(");
            Iterator<ItemId> iterator = itemIds.iterator();
            while (iterator.hasNext()) {
                if (!iterator.hasNext()) {
                    sb.append(iterator + ")");
                } else {
                    sb.append(iterator + " OR ");
                }
            }
        }

        String startGrant = entitlementSearchParam.getStartGrantTime();
        String endGrant = entitlementSearchParam.getEndGrantTime();
        Boolean isStartGrantNull = StringUtils.isEmpty(startGrant);
        Boolean isEndGrantNull = StringUtils.isEmpty(endGrant);
        if (isStartGrantNull) {
            if (!isEndGrantNull) {
                sb.append(" AND grantTime:{" + EntitlementConsts.MIN_DATE + " TO " + endGrant + "}");
            }
        } else {
            if (isEndGrantNull) {
                sb.append(" AND grantTime:{" + startGrant + " TO " + EntitlementConsts.MAX_DATE + "}");
            } else {
                sb.append(" AND grantTime:{" + startGrant + " TO " + endGrant + "}");
            }
        }

        String startExpiration = entitlementSearchParam.getStartExpirationTime();
        String endExpiration = entitlementSearchParam.getEndExpirationTime();
        Boolean isStartExpirationNull = StringUtils.isEmpty(startExpiration);
        Boolean isEndExpirationNull = StringUtils.isEmpty(endExpiration);
        if (isStartExpirationNull) {
            if (!isEndExpirationNull) {
                sb.append(" AND expirationTime:{" + EntitlementConsts.MIN_DATE + " TO " + endExpiration + "}");
            }
        } else {
            if (isEndExpirationNull) {
                sb.append(" AND expirationTime:{" + startExpiration + " TO " + EntitlementConsts.MAX_DATE + "}");
            } else {
                sb.append(" AND expirationTime:{" + startExpiration + " TO " + endExpiration + "}");
            }
        }

        if (!StringUtils.isEmpty(entitlementSearchParam.getLastModifiedTime())) {
            sb.append(" AND updatedTime:{" + entitlementSearchParam.getLastModifiedTime() + " TO " + EntitlementConsts.MAX_DATE + "}");
        }

        int size = pageMetadata.getCount() == null ||
                pageMetadata.getCount() > EntitlementConsts.MAX_PAGE_SIZE
                ? EntitlementConsts.DEFAULT_PAGE_SIZE : pageMetadata.getCount();
        String bookmark = pageMetadata.getBookmark();

        return super.searchView("search", sb.toString(), size, bookmark);
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

    protected EntitlementCloudantViews views = new EntitlementCloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();
        Map<String, CloudantView> indexMap = new HashMap<>();

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

        CloudantView searchIndex = new CloudantView();
        searchIndex.setIndex("function(doc) {" +
                "index(\'userId\', doc.userId);" +
                "index(\'isDeleted\', doc.isDeleted);" +
                "index(\'type\', doc.type);" +
                "index(\'isBanned\', doc.isBanned);" +
                "index(\'itemId\', doc.itemId);" +
                "index(\'grantTime\', doc.grantTime);" +
                "index(\'expirationTime\', doc.expirationTime);" +
                "index(\'updatedTime\', doc.updatedTime);}");
        searchIndex.setResultClass(String.class);
        indexMap.put("search", searchIndex);

        setViews(viewMap);
        setIndexes(indexMap);
    }};

    private String format(Date date) {
        return EntitlementConsts.DATE_FORMAT.format(date);
    }
}
