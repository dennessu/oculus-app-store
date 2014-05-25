/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OfferId
import com.junbo.common.id.SubledgerId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerParam
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.repo.BaseCloudantRepository
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Required
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class SubledgerRepositoryCloudantImpl extends BaseCloudantRepository<Subledger, SubledgerId> implements SubledgerRepository {

    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    protected SubledgerId generateId() {
        return new SubledgerId(idGenerator.nextId());
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<Subledger>> list(SubledgerParam subledgerParam, PageParam pageParam) {
        throw new RuntimeException("Not implemented.")
    }

    @Override
    Promise<Subledger> find(UserId sellerId, String payoutStatus, OfferId offerId, Date startTime, CurrencyId currency, CountryId country) {
        throw new RuntimeException("Not implemented.")
    }

    private CloudantViews views = new CloudantViews(
        views: [:]
    )
}
