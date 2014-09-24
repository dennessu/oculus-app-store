/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repo.cloudant;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.db.repo.FacebookPaymentAccountRepository;
import com.junbo.payment.spec.internal.FacebookPaymentAccountMapping;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

import java.util.List;

/**
 * Facebook Payment Account Repository Cloudant Impl.
 */
public class FacebookPaymentAccountRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<FacebookPaymentAccountMapping, Long>
                                                          implements FacebookPaymentAccountRepository {
    @Override
    public Promise<List<FacebookPaymentAccountMapping>> getByUserId(Long userId) {
        return super.queryView("by_user_id", userId.toString());
    }
}
