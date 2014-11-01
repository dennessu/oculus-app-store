/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.login;

import java.util.List;

/**
 * Created by xiali_000 on 2014/11/1.
 */
public class GetSupportedCountriesResponse {
    private List<String> supportedCountries;

    public List<String> getSupportedCountries() {
        return supportedCountries;
    }

    public void setSupportedCountries(List<String> supportedCountries) {
        this.supportedCountries = supportedCountries;
    }
}
