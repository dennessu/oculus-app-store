/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created by baojing on 3/10/14.
 */
public class AttributesGetOptions {
    // paging params
    @QueryParam("start")
    private Integer start;
    @QueryParam("size")
    private Integer size;

    // if entityIds is specified, paging params will be ignored.
    @QueryParam("id")
    private List<Long> attributeIds;

    @QueryParam("type")
    private String attributeType;
}
