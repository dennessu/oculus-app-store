/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.rest.resource

import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.core.RestrictionService
import com.junbo.restriction.spec.model.AgeCheck
import com.junbo.restriction.spec.resource.RestrictionResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Impl of RestrictionResource.
 */
@CompileStatic
class RestrictionResourceImpl implements RestrictionResource {
    @Autowired
    private RestrictionService restrictionService

    Promise<AgeCheck> getAgeCheck(AgeCheck ageCheck) {
        return restrictionService.getAgeCheck(ageCheck)
    }
}
