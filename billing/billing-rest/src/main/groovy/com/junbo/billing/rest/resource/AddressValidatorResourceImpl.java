/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.rest.resource;

import com.junbo.billing.core.service.TaxService;
import com.junbo.billing.spec.resource.AddressValidatorResource;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xmchen on 14-8-14.
 */
public class AddressValidatorResourceImpl implements AddressValidatorResource {

    @Autowired
    private TaxService taxService;

    @Override
    public Promise<Address> validateAddress(Address address) {
        return taxService.validateAddress(address);
    }
}
