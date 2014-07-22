package com.junbo.common.cloudant.model

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
/**
 * CloudantSearchResult.
 */
class CloudantSearchResult<T> {
    String bookmark
    List<T> results
    Long total
}
