/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.filter

/**
 * Created by kg on 3/19/2014.
 */
interface ResourceFilter<T> {

    T filterForCreate(T resource)

    T filterForPut(T resource, T oldResource)

    T filterForPatch(T resource, T oldResource)

    T filterForGet(T resource, List<String> propertiesToInclude)
}
