/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.db;

import com.junbo.drm.spec.model.License;
import com.junbo.sharding.repo.BaseCloudantRepository;

/**
 * drm.
 */
public class LicenseRepositoryImpl extends BaseCloudantRepository<License, String> implements LicenseRepository {
}
