/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.catalog.spec.model.item.EntitlementDef;
import com.junbo.catalog.spec.model.item.ItemRevision;
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
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

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
        checkItem(entitlement.getItemId());
        checkOauth(entitlement);
        if (entitlement.getRev() != null) {
            throw AppErrors.INSTANCE.fieldNotCorrect("rev",
                    "rev can not be set when created").exception();
        }
        if (Boolean.TRUE.equals(entitlement.getIsBanned())) {
            throw AppErrors.INSTANCE.fieldNotCorrect("isSuspended",
                    "isSuspended can not be true when created").exception();
        }
        if (entitlement.getType() != null) {
            if (!EntitlementConsts.ALLOWED_TYPE.contains(entitlement.getType().toUpperCase())) {
                throw AppErrors.INSTANCE.fieldNotCorrect("entitlementType",
                        "entitlementType [" + entitlement.getType() + "] not supported").exception();
            }
        }
        validateNotNull(entitlement.getGrantTime(), "grantTime");
        if (entitlement.getUseCount() != null && entitlement.getUseCount() < 0) {
            throw AppErrors.INSTANCE.fieldNotCorrect("useCount", "useCount should not be negative").exception();
        }
        validateConsumable(entitlement);
        validateGrantTimeBeforeExpirationTime(entitlement);
    }

    protected void validateUpdateId(String entitlementId, Entitlement entitlement) {
        if (entitlement.getId() == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!entitlementId.equals(entitlement.getId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id",
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
            throw AppErrors.INSTANCE.fieldNotCorrect("useCount", "useCount should not be negative").exception();
        }
        validateConsumable(entitlement);
        validateGrantTimeBeforeExpirationTime(existingEntitlement);
    }

    private void validateGrantTimeBeforeExpirationTime(Entitlement entitlement) {
        if (entitlement.getExpirationTime() != null) {
            if (entitlement.getGrantTime().after(entitlement.getExpirationTime())) {
                throw AppErrors.INSTANCE.expirationTimeBeforeGrantTime().exception();
            }
        }
    }

    private void validateConsumable(Entitlement entitlement) {
        ItemRevision item = getItem(entitlement.getItemId());
        EntitlementDef def = filter(item.getEntitlementDefs(), entitlement.getType());
        if (def == null) {
            throw AppErrors.INSTANCE.common("there is no entitlementDef with type [" +
                    entitlement.getType() + "] in item [" + entitlement.getItemId() + "]").exception();
        }
        if (def.getConsumable() && entitlement.getUseCount() == null) {
            throw AppErrors.INSTANCE.fieldNotCorrect("useCount",
                    "useCount should not be null when entitlementDefinition is consumable").exception();
        } else if (!def.getConsumable() && entitlement.getUseCount() != null) {
            throw AppErrors.INSTANCE.fieldNotCorrect("useCount",
                    "useCount should be null when entitlementDefinition is not consumable").exception();
        }
    }

    private void validateEquals(Object actual, Object expected, String fieldName) {
        if (expected == actual) {
            return;
        } else if (expected == null || actual == null) {
            throw AppErrors.INSTANCE.fieldNotMatch(fieldName, actual, expected).exception();
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
            throw AppErrors.INSTANCE.fieldNotMatch(fieldName, actual, expected).exception();
        }
    }

    protected void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw AppErrors.INSTANCE.missingField(fieldName).exception();
        }
    }

    protected void checkUser(Long userId) {
        validateNotNull(userId, "user");
    }

    protected void checkOauth(final Entitlement entitlement) {
        ItemRevision item = (ItemRevision) CommonCache.ITEM_REVISION.get(
                entitlement.getItemId(), new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return itemFacade.getItem(entitlement.getItemId());
            }
        });
        //TODO: check clientId
    }

    protected void checkDateFormat(String date) {
        if (!StringUtils.isEmpty(date)) {
            try {
                EntitlementConsts.DATE_FORMAT.parse(date);
            } catch (ParseException e) {
                throw AppErrors.INSTANCE.
                        common("date should be in yyyy-MM-ddTHH:mm:ssZ format").exception();
            }
        }
    }

    protected void checkItem(String itemId) {
        validateNotNull(itemId, "item");
        ItemRevision item = getItem(itemId);
        if (item == null) {
            throw AppErrors.INSTANCE.fieldNotCorrect("item",
                    "item [" +
                            itemId +
                            "] not found").exception();
        }
    }

    protected ItemRevision getItem(final String itemId) {
        if (itemId == null) {
            return null;
        }
        return (ItemRevision) CommonCache.ITEM_REVISION.get(
                itemId, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return itemFacade.getItem(itemId);
            }
        });
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
}
