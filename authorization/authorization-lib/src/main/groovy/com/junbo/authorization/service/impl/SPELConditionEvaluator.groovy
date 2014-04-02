/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.service.impl

import com.junbo.authorization.service.ConditionEvaluator
import groovy.transform.CompileStatic
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
 * SPELConditionEvaluator.
 */
@CompileStatic
class SPELConditionEvaluator implements ConditionEvaluator {
    private final static ExpressionParser PARSER = new SpelExpressionParser()

    @Override
    Boolean evaluate(String condition, Object object) {
        StandardEvaluationContext context = new StandardEvaluationContext(object)

        return PARSER.parseExpression(condition).getValue(context, Boolean)
    }
}
