/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.test.common.libs.EnumHelper;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 4/22/2014
 * The interface for Entitlement definitionrelated APIs
 */
public interface EntitlementDefinitionService {

    String getEntitlementDefinition(String entitlementDefinitionId) throws Exception;
    String getEntitlementDefinition(String entitlementDefinitionId, int expectedResponseCode) throws Exception;

    List<String> getEntitlementDefinitions(HashMap<String, String> httpPara) throws Exception;
    List<String> getEntitlementDefinitions(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception;

    String postDefaultEntitlementDefinition(EnumHelper.EntitlementType entitlementDefinitionType) throws Exception;
    String postEntitlementDefinition(EntitlementDefinition entitlementDefinition) throws Exception;
    String postEntitlementDefinition(EntitlementDefinition entitlementDefinition, int expectedResponseCode) throws Exception;

    String updateEntitlementDefinition(EntitlementDefinition entitlementDefinition) throws Exception;
    String updateEntitlementDefinition(EntitlementDefinition entitlementDefinition, int expectedResponseCode) throws Exception;

    void deleteEntitlementDefinition(String entitlementDefinitionId) throws Exception;
    void deleteEntitlementDefinition(String entitlementDefinitionId, int expectedResponseCode) throws Exception;
}
