/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import groovy.transform.CompileStatic

/**
 * List Result.
 * @param < T > entity type.
 */
@CompileStatic
@JsonPropertyOrder(value = ['criteria', 'next'])
class ResultList<T> {
    String next
    List<T> criteria
}
