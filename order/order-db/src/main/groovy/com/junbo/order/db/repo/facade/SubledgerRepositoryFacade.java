/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.facade;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.*;
import com.junbo.order.spec.model.*;

import java.util.Date;
import java.util.List;

/**
 * Created by fzhang on 4/2/2014.
 */
public interface SubledgerRepositoryFacade {

    Subledger createSubledger(Subledger subledger);

    Subledger updateSubledger(Subledger subledger);

    Subledger getSubledger(SubledgerId subledgerId);

    List<Subledger> getSubledgers(SubledgerParam subledgerParam,
                                  PageParam pageParam);

    List<Subledger> getSubledgersOrderBySeller(int dataCenterId, int shardId, String payOutStatus, Date startDate, Date endDate,
                                  PageParam pageParam);

    List<Subledger> getSubledgersByPayouId(PayoutId payoutId, PageParam pageParam);

    Subledger findSubledger(OrganizationId sellerId, String payoutStatus,
                                          OfferId offerId, Date startTime,  CurrencyId currency,
                                          CountryId country);

    SubledgerItem createSubledgerItem(SubledgerItem subledgerItem);

    SubledgerItem getSubledgerItem(SubledgerItemId subledgerItemId);

    List<SubledgerItem> getSubledgerItem(Integer dataCenterId, Object shardKey, String status, OfferId offerId, Date endTime, PageParam pageParam);

    List<SubledgerItem> getSubledgerItemByOrderItemId(OrderItemId orderItemId);

    List<OfferId> getDistinctSubledgerItemOfferIds(Integer dataCenterId, Object shardKey, String status, PageParam pageParam);

    SubledgerItem updateSubledgerItem(SubledgerItem subledgerItem, SubledgerItem oldSubledgerItem);

    SubledgerEvent createSubledgerEvent(SubledgerEvent subledgerEvent);

    List<SubledgerEvent> getSubledgerEvents(SubledgerId subledgerId);
}


