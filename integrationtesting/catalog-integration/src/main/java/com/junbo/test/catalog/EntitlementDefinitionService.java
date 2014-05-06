/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.test.catalog.enums.EntitlementType;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 4/22/2014
 * The interface for Entitlement definitionrelated APIs
 */
public interface EntitlementDefinitionService {

    EntitlementDefinition getEntitlementDefinition(Long edId) throws Exception;
    EntitlementDefinition getEntitlementDefinition(Long edId, int expectedResponseCode) throws Exception;

    Results<EntitlementDefinition> getEntitlementDefinitions(HashMap<String, List<String>> httpPara) throws Exception;
    Results<EntitlementDefinition> getEntitlementDefinitions(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception;

    EntitlementDefinition postDefaultEntitlementDefinition(EntitlementType entitlementDefinitionType) throws Exception;
    EntitlementDefinition postEntitlementDefinition(EntitlementDefinition ed) throws Exception;
    EntitlementDefinition postEntitlementDefinition(EntitlementDefinition ed, int expectedResponseCode) throws Exception;

    EntitlementDefinition updateEntitlementDefinition(EntitlementDefinition ed) throws Exception;
    EntitlementDefinition updateEntitlementDefinition(EntitlementDefinition ed, int expectedResponseCode) throws Exception;

    void deleteEntitlementDefinition(Long edId) throws Exception;
    void deleteEntitlementDefinition(Long edId, int expectedResponseCode) throws Exception;
}
