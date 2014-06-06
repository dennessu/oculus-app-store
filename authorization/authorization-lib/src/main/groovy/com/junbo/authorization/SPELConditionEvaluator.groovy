/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import groovy.transform.CompileStatic
import org.springframework.expression.Expression
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

import java.util.concurrent.ConcurrentHashMap

/**
 * SPELConditionEvaluator.
 */
@CompileStatic
class SPELConditionEvaluator implements ConditionEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser()

    @SuppressWarnings('ImplementationAsType')
    private final ConcurrentHashMap<String, Expression> parsedExpressions = new ConcurrentHashMap<>()

    @Override
    @SuppressWarnings('GetterMethodCouldBeProperty')
    String getScriptType() {
        return 'SPEL'
    }

    @Override
    Boolean evaluate(String condition, Object object) {
        StandardEvaluationContext context = new StandardEvaluationContext(object)

        return parseExpression(condition).getValue(context, Boolean)
    }

    private Expression parseExpression(String expStr) {
        Expression exp = parsedExpressions.get(expStr)
        if (exp == null) {
            exp = parser.parseExpression(expStr)
            exp = parsedExpressions.putIfAbsent(expStr, exp) ?: exp
        }

        return exp
    }
}
