/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.repository;

import com.junbo.subscription.db.dao.SubscriptionEventDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 14-3-28.
 */
public class SubscriptionEventRepository {
    @Autowired
    private SubscriptionEventDao subscriptionEventDao;
}
