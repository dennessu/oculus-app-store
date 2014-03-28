/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.clientproxy.model

import com.junbo.catalog.spec.model.item.Item
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by chriszhu on 3/21/14.
 */
@CompileStatic
@TypeChecked
class OrderOfferItem {
    Item catalogItem
}
