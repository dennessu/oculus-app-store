/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.core;

import com.junbo.common.id.SubledgerId;
import com.junbo.order.spec.model.PageParam;
import com.junbo.order.spec.model.Subledger;
import com.junbo.order.spec.model.SubledgerItem;
import com.junbo.order.spec.model.SubledgerParam;

import java.util.List;

/**
 * Created by fzhang on 4/2/2014.
 */
public interface SubledgerService {

    Subledger createSubledger(Subledger subledger);

    Subledger updateSubledger(Subledger subledger);

    Subledger getSubledger(SubledgerId subledgerId);

    Subledger aggregateSubledger(SubledgerId subledgerId, List<SubledgerItem> items);

    List<Subledger> getSubledgers(SubledgerParam subledgerParam, PageParam pageParam);

    SubledgerItem createSubledgerItem(SubledgerItem subledgerItem);

    SubledgerItem updateSubledgerItem(SubledgerItem subledgerItem);
}
