/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.drm;

import com.junbo.drm.spec.model.LicenseRequest;
import com.junbo.drm.spec.model.SignedLicense;

/**
 @author Jason
  * Time: 7/28/2014
  * The interface for Item related APIs
 */
public interface DrmService {

    SignedLicense postLicense(LicenseRequest request) throws Exception;
    SignedLicense postLicense(LicenseRequest request, int expectedResponseCode) throws Exception;

}
