/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.annotation

import groovy.transform.CompileStatic

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * AuthorizeRequired.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CompileStatic
@interface AuthorizeRequired {

}