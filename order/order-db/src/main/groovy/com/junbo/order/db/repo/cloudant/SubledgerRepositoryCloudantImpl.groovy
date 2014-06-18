/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OfferId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.SubledgerId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerParam
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

import java.text.DateFormat
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class SubledgerRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<Subledger, SubledgerId> implements SubledgerRepository {

    private final static String DATE_STRING_MAX = 'A' // String that larger than any formatted date text
    private DateFormat dateFormat = new ISO8601DateFormat()

    @Override
    Promise<List<Subledger>> list(SubledgerParam subledgerParam, PageParam pageParam) {
        String fromDateStr = subledgerParam.fromDate == null ? "" : dateFormat.format(subledgerParam.fromDate)
        String endDateStr = subledgerParam.toDate == null ? DATE_STRING_MAX : dateFormat.format(subledgerParam.toDate)
        String startKey = [subledgerParam.sellerId.toString(), subledgerParam.payOutStatus,
                                            fromDateStr].join(';')

        String endKey = [subledgerParam.sellerId.toString(), subledgerParam.payOutStatus,
                                          endDateStr].join(';')
        return super.queryView('by_seller_status_offer_time_cc', startKey, endKey,
            pageParam?.count, pageParam?.start, false, true)
    }

    @Override
    Promise<Subledger> find(OrganizationId sellerId, String payoutStatus, OfferId offerId,
                            Date startTime, CurrencyId currency, CountryId country) {
        return super.queryView('by_seller_status_offer_time_cc',
                [sellerId.toString(), payoutStatus, dateFormat.format(startTime),
                offerId.toString(), currency.toString(), country.toString()].join(';')).then { List<Subledger> list ->

            return Promise.pure(list.isEmpty() ? null : list.iterator().next())
        }
    }
}

