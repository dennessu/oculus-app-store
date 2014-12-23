/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.junbo.catalog.spec.model.item.EntitlementDef;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.UserId;
import com.junbo.common.util.IdFormatter;
import com.junbo.entitlement.clientproxy.catalog.ItemFacade;
import com.junbo.entitlement.common.cache.CommonCache;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.Entitlement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Base service.
 */
public class BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);
    @Autowired
    protected ItemFacade itemFacade;

    protected void fillCreate(Entitlement entitlement) {
        if (entitlement.getIsBanned() == null) {
            entitlement.setIsBanned(false);
        }
        if (entitlement.getGrantTime() == null) {
            entitlement.setGrantTime(EntitlementContext.current().getNow());
        }
        if (entitlement.getFutureExpansion() == null) {
            entitlement.setFutureExpansion(new HashMap<String, JsonNode>());
        }
    }

    protected void fillUpdate(Entitlement entitlement, Entitlement existingEntitlement) {
        existingEntitlement.setUseCount(entitlement.getUseCount());
        existingEntitlement.setExpirationTime(entitlement.getExpirationTime());
        if (entitlement.getFutureExpansion() != null) {
            existingEntitlement.setFutureExpansion(entitlement.getFutureExpansion());
        }
        if (entitlement.getIsBanned() != null) {
            existingEntitlement.setIsBanned(entitlement.getIsBanned());
        }
    }

    protected void validateCreate(Entitlement entitlement) {
        checkUser(entitlement.getUserId());
        checkItem(entitlement.getItemId());
        checkOauth(entitlement);
        if (entitlement.getRev() != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull("rev").exception();
        }
        if (Boolean.TRUE.equals(entitlement.getIsBanned())) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable("isSuspended", entitlement.getIsBanned(), Boolean.FALSE).exception();
        }
        if (entitlement.getType() != null) {
            if (!EntitlementConsts.ALLOWED_TYPE.contains(entitlement.getType().toUpperCase())) {
                throw AppCommonErrors.INSTANCE.fieldInvalidEnum("type", Joiner.on(", ").join(EntitlementConsts.ALLOWED_TYPE)).exception();
            }
        }
        validateNotNull(entitlement.getGrantTime(), "grantTime");
        if (entitlement.getUseCount() != null && entitlement.getUseCount() < 0) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("useCount", "useCount should not be negative").exception();
        }
        validateConsumable(entitlement);
        validateGrantTimeBeforeExpirationTime(entitlement);
    }

    protected void validateUpdateId(String entitlementId, Entitlement entitlement) {
        if (entitlement.getId() == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("id").exception();
        }
        if (!entitlementId.equals(entitlement.getId())) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable("id",
                    entitlement.getId(),
                    entitlementId).exception();
        }
    }

    protected void validateUpdate(Entitlement entitlement, Entitlement existingEntitlement) {
        checkOauth(existingEntitlement);
        validateEquals(entitlement.getRev(), existingEntitlement.getRev(), "rev");
        validateEquals(formatId(entitlement.getUserId()), formatId(existingEntitlement.getUserId()), "user");
        validateEquals(entitlement.getType(), existingEntitlement.getType(), "entitlementType");
        validateEquals(entitlement.getItemId(), existingEntitlement.getItemId(), "item");
        validateEquals(entitlement.getGrantTime(), existingEntitlement.getGrantTime(), "grantTime");
        if (entitlement.getUseCount() != null && entitlement.getUseCount() < 0) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("useCount", "useCount should not be negative").exception();
        }
        validateConsumable(entitlement);
        validateGrantTimeBeforeExpirationTime(existingEntitlement);
    }

    private void validateGrantTimeBeforeExpirationTime(Entitlement entitlement) {
        if (entitlement.getExpirationTime() != null) {
            if (entitlement.getGrantTime().after(entitlement.getExpirationTime())) {
                throw AppCommonErrors.INSTANCE.fieldInvalid("expirationTime", "ExpirationTime should not be before grantTime").exception();
            }
        }
    }

    private void validateConsumable(Entitlement entitlement) {
        ItemRevision item = getItem(entitlement.getItemId());
        EntitlementDef def = filter(item.getEntitlementDefs(), entitlement.getType());
        if (def == null) {
            List<String> validTypes = new ArrayList<>();
            for (EntitlementDef entitlementDef : item.getEntitlementDefs()) {
                validTypes.add(entitlementDef.getType());
            }

            if (CollectionUtils.isEmpty(validTypes)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid("There is no entitlementDef in itemRevision " + item.getId()).exception();
            }

            throw AppCommonErrors.INSTANCE.fieldInvalidEnum("type", Joiner.on(", ").join(validTypes)).exception();
        }
        if (def.getConsumable() && entitlement.getUseCount() == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("useCount",
                    "useCount should not be null when entitlementDefinition is consumable").exception();
        } else if (!def.getConsumable() && entitlement.getUseCount() != null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("useCount",
                    "useCount should be null when entitlementDefinition is not consumable").exception();
        }
    }

    protected void validateEquals(Object actual, Object expected, String fieldName) {
        if (expected == actual) {
            return;
        } else if (expected == null || actual == null) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable(fieldName, actual, expected).exception();
        }
        Boolean equals = true;
        if (actual instanceof String) {
            if (!((String) expected).equalsIgnoreCase((String) actual)) {
                equals = false;
            }
        } else if (actual instanceof Date) {
            if (Math.abs(((Date) actual).getTime() - ((Date) expected).getTime()) > 1000) {
                equals = false;
            }
        } else if (!expected.equals(actual)) {
            equals = false;
        }

        if (!equals) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable(fieldName, actual, expected).exception();
        }
    }

    protected void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired(fieldName).exception();
        }
    }

    protected void checkUser(Long userId) {
        validateNotNull(userId, "user");
    }

    protected void checkOauth(final Entitlement entitlement) {
        ItemRevision item = getItem(entitlement.getItemId());
        //TODO: check clientId
    }

    protected void checkDateFormat(String field, String date) {
        if (!StringUtils.isEmpty(date)) {
            try {
                EntitlementConsts.DATE_FORMAT.parse(date);
            } catch (ParseException e) {
                throw AppCommonErrors.INSTANCE.fieldInvalid(field, "Date should be in yyyy-MM-ddTHH:mm:ssZ format").exception();
            }
        }
    }

    protected void checkItem(String itemId) {
        validateNotNull(itemId, "item");
        ItemRevision item = getItem(itemId);
        if (item == null) {
            throw AppErrors.INSTANCE.itemNotFound("item", itemId).exception();
        }
    }

    protected ItemRevision getItem(final String itemId) {
        if (itemId == null) {
            return null;
        }
        ItemRevision existing = (ItemRevision) CommonCache.ITEM_REVISION.get(itemId);
        if (existing != null) {
            return existing;
        }
        ItemRevision item = itemFacade.getItem(itemId);
        if (item == null) {
            return null;
        }
        CommonCache.ITEM_REVISION.put(itemId, item);
        return item;
    }

    protected String formatId(long id) {
        return IdFormatter.encodeId(new UserId(id));
    }

    protected EntitlementDef filter(List<EntitlementDef> defs, String type) {
        if (defs == null) {
            return null;
        }
        for (EntitlementDef def : defs) {
            if (type == null) {
                if (def.getType() == null) {
                    return def;
                }
            } else if (type.equalsIgnoreCase(def.getType())) {
                return def;
            }
        }
        return null;
    }

    protected Boolean isConsumable(Entitlement entitlement) {
        ItemRevision item = getItem(entitlement.getItemId());
        EntitlementDef def = filter(item.getEntitlementDefs(), entitlement.getType());
        return def.getConsumable();
    }
}
