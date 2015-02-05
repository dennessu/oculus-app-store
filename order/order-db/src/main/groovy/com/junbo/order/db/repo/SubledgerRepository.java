/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.*;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.PageParam;
import com.junbo.order.spec.model.Subledger;
import com.junbo.order.spec.model.SubledgerParam;
import com.junbo.order.spec.model.enums.SubledgerType;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by fzhang on 4/2/2014.
 */
public interface SubledgerRepository extends BaseRepository<Subledger, SubledgerId> {

    @ReadMethod
    Promise<List<Subledger>> list(SubledgerParam subledgerParam, PageParam pageParam);

    @ReadMethod
    Promise<List<Subledger>> listOrderBySeller(int dataCenterId, int shardId, String payOutStatus, Date startDate, Date endDate,
                                               PageParam pageParam);

    @ReadMethod
    Promise<List<Subledger>> listByTime(int dataCenterId, int shardId, Date startDate, Date endDate,
                                               PageParam pageParam);


    @ReadMethod
    Promise<List<Subledger>> listByPayoutId(PayoutId payoutId, PageParam pageParam);

    @ReadMethod
    Promise<Subledger> find(OrganizationId sellerId, String payoutStatus,
                            ItemId itemId, Date startTime, SubledgerType subledgerType, String subledgerKey, CurrencyId currency,
                            CountryId country);
}
