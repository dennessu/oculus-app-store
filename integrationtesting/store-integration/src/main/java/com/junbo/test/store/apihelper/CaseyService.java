/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.store.spec.model.external.sewer.SewerParam;
import com.junbo.store.spec.model.external.sewer.casey.CaseyResults;

/**
 * The CaseyService class.
 */
public interface CaseyService {

    CaseyResults<JsonNode> getCmsPage(String path, String label, SewerParam sewerParam) throws Exception;

    CaseyResults<JsonNode> searchCmsOffers(String cmsPage, String cmsSlot, String platform, SewerParam sewerParam) throws Exception;
}
