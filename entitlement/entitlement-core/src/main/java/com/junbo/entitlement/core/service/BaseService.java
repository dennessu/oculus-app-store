/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefSearchParams;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.common.util.IdFormatter;
import com.junbo.entitlement.clientproxy.catalog.EntitlementDefinitionFacade;
import com.junbo.entitlement.common.cache.PermanentCache;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Base service.
 */
public class BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);
    @Autowired
    protected EntitlementDefinitionFacade definitionFacade;

    protected void fillCreate(Entitlement entitlement) {
        if (entitlement.getIsBanned() == null) {
            entitlement.setIsBanned(false);
        }
        if (entitlement.getGrantTime() == null) {
            entitlement.setGrantTime(EntitlementContext.current().getNow());
        }
        fillType(entitlement);
    }

    private void fillType(final Entitlement entitlement) {
        if (entitlement.getEntitlementDefinitionId() == null) {
            entitlement.setType(EntitlementConsts.NO_TYPE);
        }
        EntitlementDefinition def = getDef(entitlement.getEntitlementId());
        if (def == null || StringUtils.isEmpty(def.getType())) {
            entitlement.setType(EntitlementConsts.NO_TYPE);
        } else {
            entitlement.setType(def.getType());
        }
    }

    protected void fillUpdate(Entitlement entitlement, Entitlement existingEntitlement) {
        existingEntitlement.setUseCount(entitlement.getUseCount());
        existingEntitlement.setExpirationTime(entitlement.getExpirationTime());
        if (entitlement.getIsBanned() != null) {
            existingEntitlement.setIsBanned(entitlement.getIsBanned());
        } else {
            existingEntitlement.setIsBanned(false);
        }
    }

    protected void validateCreate(Entitlement entitlement) {
        checkOauth(entitlement);
        checkDefinition(entitlement.getEntitlementDefinitionId());
        if (entitlement.getRev() != null) {
            throw AppErrors.INSTANCE.fieldNotCorrect("rev",
                    "rev can not be set when created").exception();
        }
        if (Boolean.TRUE.equals(entitlement.getIsBanned())) {
            throw AppErrors.INSTANCE.fieldNotCorrect("isSuspended",
                    "isSuspended can not be true when created").exception();
        }
        validateNotNull(entitlement.getGrantTime(), "grantTime");
        if (entitlement.getUseCount() != null && entitlement.getUseCount() < 0) {
            throw AppErrors.INSTANCE.fieldNotCorrect("useCount", "useCount should not be negative").exception();
        }
        validateConsumable(entitlement);
        validateGrantTimeBeforeExpirationTime(entitlement);
    }

    protected void validateUpdateId(Long entitlementId, Entitlement entitlement) {
        if (entitlement.getEntitlementId() == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!entitlementId.equals(entitlement.getEntitlementId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id",
                    formatId(entitlement.getEntitlementId()),
                    formatId(entitlementId)).exception();
        }
    }

    protected void validateUpdate(Entitlement entitlement, Entitlement existingEntitlement) {
        checkOauth(existingEntitlement);
        validateEquals(entitlement.getRev(), existingEntitlement.getRev(), "rev");
        validateEquals(formatId(entitlement.getUserId()), formatId(existingEntitlement.getUserId()), "user");
        validateEquals(formatId(entitlement.getEntitlementDefinitionId()),
                formatId(existingEntitlement.getEntitlementDefinitionId()), "definition");
        validateEquals(entitlement.getGrantTime(), existingEntitlement.getGrantTime(), "grantTime");
        if (entitlement.getUseCount() != null && entitlement.getUseCount() < 0) {
            throw AppErrors.INSTANCE.fieldNotCorrect("useCount", "useCount should not be negative").exception();
        }
        validateConsumable(entitlement);
        validateGrantTimeBeforeExpirationTime(existingEntitlement);
    }

    protected void validateTransfer(EntitlementTransfer transfer, Entitlement existingEntitlement) {
        validateNotNull(transfer.getEntitlementId(), "entitlement");
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement",
                    formatId(transfer.getEntitlementId())).exception();
        }
        checkUser(existingEntitlement.getUserId());
        checkTargetUser(transfer.getTargetUserId());
        if (existingEntitlement.getIsBanned()) {
            LOGGER.error("Entitlement [{}] can not be transferred.", existingEntitlement.getEntitlementId());
            throw AppErrors.INSTANCE.notTransferable(
                    formatId(existingEntitlement.getEntitlementId()),
                    "Banned entitlement can not be transferred.")
                    .exception();
        }
    }

    private void validateGrantTimeBeforeExpirationTime(Entitlement entitlement) {
        if (entitlement.getExpirationTime() != null) {
            if (entitlement.getGrantTime().after(entitlement.getExpirationTime())) {
                throw AppErrors.INSTANCE.expirationTimeBeforeGrantTime().exception();
            }
        }
    }

    private void validateConsumable(Entitlement entitlement) {
        EntitlementDefinition def = getDef(entitlement.getEntitlementDefinitionId());
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
        //TODO: check userId
    }

    protected void checkTargetUser(Long userId) {
        validateNotNull(userId, "targetUser");
    }

    protected void checkOauth(final Entitlement entitlement) {
        EntitlementDefinition definition = (EntitlementDefinition) PermanentCache.ENTITLEMENT_DEFINITION.get(
                "id#" + entitlement.getEntitlementDefinitionId().toString(), new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return definitionFacade.getDefinition(entitlement.getEntitlementDefinitionId());
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

    protected void checkDefinition(Long entitlementDefinitionId) {
        EntitlementDefinition def = getDef(entitlementDefinitionId);
        if (def == null) {
            throw AppErrors.INSTANCE.fieldNotCorrect("entitlementDefinition",
                    "entitlementDefinition [" +
                            formatId(entitlementDefinitionId) +
                            "] not found").exception();
        }
    }

    protected EntitlementDefinition getDef(final Long entitlementDefId) {
        if (entitlementDefId == null) {
            return null;
        }
        return (EntitlementDefinition) PermanentCache.ENTITLEMENT_DEFINITION.get(
                "id#" + entitlementDefId, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return definitionFacade.getDefinition(entitlementDefId);
            }
        });
    }

    protected EntitlementDefinition getDevDef() {
        return (EntitlementDefinition) PermanentCache.ENTITLEMENT_DEFINITION.get("developer", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                EntitlementDefSearchParams params = new EntitlementDefSearchParams();
                params.setTypes(Collections.singleton(EntitlementType.DEVELOPER.toString()));
                return definitionFacade.getDefinitions(params).get(0);
            }
        });
    }

    protected EntitlementDefinition getDownloadDef(final Long itemId) {
        return (EntitlementDefinition) PermanentCache.ENTITLEMENT_DEFINITION.get("download#" + itemId.toString(), new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                EntitlementDefSearchParams params = new EntitlementDefSearchParams();
                params.setTypes(Collections.singleton(EntitlementType.DOWNLOAD.toString()));
                params.setItemId(new ItemId(itemId));
                List<EntitlementDefinition> result = definitionFacade.getDefinitions(params);
                return CollectionUtils.isEmpty(result) ? null : result.get(0);
            }
        });
    }

    protected EntitlementDefinition getAccessDef(final Long itemId) {
        return (EntitlementDefinition) PermanentCache.ENTITLEMENT_DEFINITION.get("access#" + itemId.toString(), new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                EntitlementDefSearchParams params = new EntitlementDefSearchParams();
                params.setTypes(Collections.singleton(EntitlementType.ONLINE_ACCESS.toString()));
                params.setItemId(new ItemId(itemId));
                List<EntitlementDefinition> result = definitionFacade.getDefinitions(params);
                return CollectionUtils.isEmpty(result) ? null : result.get(0);
            }
        });
    }

    protected String formatId(Long id) {
        return IdFormatter.encodeId(new EntitlementId(id));
    }
}
