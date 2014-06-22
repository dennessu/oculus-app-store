/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.iap.db.repo;

import com.junbo.iap.spec.model.Consumption;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by fzhang on 6/20/2014.
 */
public interface ConsumptionRepository {

    Promise<Consumption> create(Consumption consumption);

    Promise<Consumption> get(String trackingGuid);
}
