/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.core.service;

import com.junbo.common.id.UserId;
import com.junbo.drm.spec.model.SignedLicense;
import com.junbo.langur.core.promise.Promise;

/**
 * drm.
 */
public interface DrmService {

    Promise<SignedLicense> createLicense(
            UserId userId,
            String packageName,
            String versionCode,
            String nonce,
            String deviceId);
}
