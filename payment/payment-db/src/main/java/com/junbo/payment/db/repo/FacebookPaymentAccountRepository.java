/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repo;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.internal.FacebookPaymentAccountMapping;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Facebook Payment Account Repository.
 */
public interface FacebookPaymentAccountRepository extends BaseRepository<FacebookPaymentAccountMapping, Long> {
    @ReadMethod
    Promise<List<FacebookPaymentAccountMapping>> getByUserId(Long userId);
}
