/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao;

import com.junbo.order.db.entity.SubledgerEntity;
import com.junbo.order.spec.model.enums.PayoutStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by linyi on 14-2-7.
 */
public interface SubledgerDao extends BaseDao<SubledgerEntity> {

    List<SubledgerEntity> getBySellerId(long sellerId, PayoutStatus payoutStatus, Date fromDate, Date toDate,
                                               int start, int count);

    List<SubledgerEntity> getByStatusOrderBySeller(int dataCenterId, int shardId, PayoutStatus payoutStatus, Date fromDate, Date toDate,
                                        int start, int count);

    List<SubledgerEntity> getByPayoutId(long payoutId, int start, int count);

    SubledgerEntity find(long sellerId, PayoutStatus payoutStatus, Date startTime,
                                               String productItemId, String currency, String country);

}
