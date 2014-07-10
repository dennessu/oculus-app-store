/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.facade;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.*;
import com.junbo.order.spec.model.PageParam;
import com.junbo.order.spec.model.Subledger;
import com.junbo.order.spec.model.SubledgerItem;
import com.junbo.order.spec.model.SubledgerParam;

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

    Subledger findSubledger(OrganizationId sellerId, String payoutStatus,
                                          OfferId offerId, Date startTime,  CurrencyId currency,
                                          CountryId country);

    SubledgerItem createSubledgerItem(SubledgerItem subledgerItem);

    SubledgerItem getSubledgerItem(SubledgerItemId subledgerItemId);

    List<SubledgerItem> getSubledgerItem(Integer dataCenterId, Object shardKey, String status, PageParam pageParam);

    List<SubledgerItem> getSubledgerItemByOrderItemId(OrderItemId orderItemId);

    SubledgerItem updateSubledgerItem(SubledgerItem subledgerItem);
}


