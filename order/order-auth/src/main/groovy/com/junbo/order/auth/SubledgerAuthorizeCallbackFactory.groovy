/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.auth

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.SubledgerId
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.resource.SubledgerResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * SubledgerAuthorizeCallbackFactory.
 */
@CompileStatic
class SubledgerAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Subledger> {
    private SubledgerResource subledgerResource

    @Required
    void setSubledgerResource(SubledgerResource subledgerResource) {
        this.subledgerResource = subledgerResource
    }

    @Override
    AuthorizeCallback<Subledger> create(Subledger entity) {
        return new SubledgerAuthorizeCallback(this, entity)
    }

    AuthorizeCallback<Subledger> create(OrganizationId seller) {
        return create(new Subledger(seller:seller))
    }

    AuthorizeCallback<Subledger> create(SubledgerId subledgerId) {
        return create(subledgerResource.getSubledger(subledgerId).get())
    }

    AuthorizeCallback<Subledger> create() {
        return new SubledgerAuthorizeCallback(this, new Subledger())
    }
}
