/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.rest.resource;

import com.junbo.billing.core.service.TaxService;
import com.junbo.billing.spec.model.VatIdValidationResponse;
import com.junbo.billing.spec.resource.VatResource;
import com.junbo.langur.core.promise.Promise;

import javax.annotation.Resource;

/**
 * Created by LinYi on 2014/6/16.
 */
class VatResourceImpl implements VatResource {
    @Resource
    private TaxService taxService;

    @Override
    public Promise<VatIdValidationResponse> validateVatId(String vatId, String country) {
        return taxService.validateVatId(vatId, country);
    }
}
