/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.core.service;

import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.drm.spec.model.License;

/**
 * drm.
 */
public interface DrmService {
    License createLicense(UserId userId, ItemId itemId, String machineHash);
}
