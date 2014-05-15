/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.service

import groovy.transform.CompileStatic

/**
 * ConditionEvaluator.
 */
@CompileStatic
interface ConditionEvaluator {

    String getScriptType()

    Boolean evaluate(String condition, Object object)
}
