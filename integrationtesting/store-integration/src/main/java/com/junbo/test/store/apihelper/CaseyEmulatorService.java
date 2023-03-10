/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper;

import com.junbo.emulator.casey.spec.model.CaseyEmulatorData;
import com.junbo.store.spec.model.external.sewer.casey.CaseyResults;
import com.junbo.store.spec.model.external.sewer.casey.cms.CmsPage;

/**
 * The CaseyEmulatorService class.
 */
public interface CaseyEmulatorService {

    CaseyEmulatorData postEmulatorData(CaseyEmulatorData caseyEmulatorData) throws Exception;

    CaseyResults<CmsPage> getCmsPages(String path, String label, int expectedResponseCode) throws Exception;

    void resetEmulatorData() throws Exception;
}
