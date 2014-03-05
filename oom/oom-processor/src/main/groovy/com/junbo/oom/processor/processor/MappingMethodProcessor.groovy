/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.processor

import com.junbo.oom.processor.model.MappingMethodModel
import com.junbo.oom.processor.source.MappingMethodInfo
/**
 * Java doc.
 */
interface MappingMethodProcessor {

    boolean canProcess(MappingMethodInfo mappingMethodInfo, ProcessorContext processorContext)

    MappingMethodModel process(MappingMethodInfo mappingMethodInfo, ProcessorContext processorContext)

    int getSequenceNumber()
}