/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.processor

import com.junbo.oom.processor.handler.HandlerContext
import groovy.transform.CompileStatic
/**
 * Java doc.
 */
@CompileStatic
class ProcessorContext {

    HandlerContext handlerContext

    Closure onNewMappingMethod
}
