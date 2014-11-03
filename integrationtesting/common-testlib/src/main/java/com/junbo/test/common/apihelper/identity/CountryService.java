/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Country;

/**
 * Created by xiali_000 on 2014/11/1.
 */
public interface CountryService {
    Results<Country> getAllCountries() throws Exception;
}
