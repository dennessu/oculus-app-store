/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.mapper;

import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;
import com.junbo.oom.processor.model.Item1;
import com.junbo.oom.processor.model.Item2;

import java.util.List;

/**.
 * Java doc
 */
@Mapper(uses={
        CommonMapper.class
})
public interface SimpleMapper {

    @Mappings({
            @Mapping(source = "source1", target = "target1", explicitMethod = "toString"),
            @Mapping(source = "prop1", target = "prop1", explicitMethod = "toString2")
    })
    Item2 fromItem1toItem2_single(Item1 source, MappingContext mappingContext);

    Item1 fromItem2toItem1_single(Item2 source, MappingContext mappingContext);

    Item1 fromItem1toItem1(Item1 source);

    Item1 fromItem1toItem1(Item1 source, MappingContext mappingContext);

    List<Item2> fromItem1toItem2(List<Item1> source, MappingContext mappingContext);
}
