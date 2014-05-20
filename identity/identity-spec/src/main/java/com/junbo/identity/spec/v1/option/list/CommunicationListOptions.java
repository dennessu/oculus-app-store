/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.identity.spec.options.list.PagingGetOptions;

import javax.ws.rs.QueryParam;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class CommunicationListOptions extends PagingGetOptions {
    @QueryParam("region")
    private CountryId region;

    @QueryParam("translation")
    private LocaleId translation;

    public CountryId getRegion() {
        return region;
    }

    public void setRegion(CountryId region) {
        this.region = region;
    }

    public LocaleId getTranslation() {
        return translation;
    }

    public void setTranslation(LocaleId translation) {
        this.translation = translation;
    }
}
