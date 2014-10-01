/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 6/5/14.
 */
@IdResourcePath(value = "/payout-tax-profiles/{0}",
                resourceType = "payout-tax-profiles",
                regex = "/payout-tax-profiles/(?<id>[0-9A-Za-z]+)")
public class PayoutTaxProfileId  extends CloudantId {

    public PayoutTaxProfileId() {} {
    }

    public PayoutTaxProfileId(String value) {
        super(value);
    }
}
