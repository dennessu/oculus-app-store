/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.mapper;

import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.processor.model.Item1;
import com.junbo.oom.processor.model.Item2;

import java.util.List;
import java.util.Map;

/**.
 * Java doc
 */
@Mapper
public interface SimpleMapper2 {

    List<Item1> fromItem2toItem1(List<Item2> source, MappingContext mappingContext);

    Map<String, Item1> fromItem2stoItem1s(Map<String, Item2> source, MappingContext mappingContext);
}
