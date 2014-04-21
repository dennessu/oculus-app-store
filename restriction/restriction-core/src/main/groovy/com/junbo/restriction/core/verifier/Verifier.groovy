/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.core.verifier

import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.spec.model.AgeCheck

/**
 * Created by Wei on 4/19/14.
 */
interface Verifier {
    void setCountry(String country)
    boolean isMatch()
    Promise<AgeCheck> verify(AgeCheck ageCheck)
}