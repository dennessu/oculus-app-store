/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.handler

import com.junbo.langur.processor.model.ClientProxyFactoryModel
import com.junbo.langur.processor.model.ClientProxyModel
import com.junbo.langur.processor.model.RestAdapterModel
import com.junbo.langur.processor.model.SyncWrapperModel
import groovy.transform.CompileStatic

import javax.annotation.processing.ProcessingEnvironment
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class HandlerContext {

    ProcessingEnvironment processingEnv

    RestAdapterModel restAdapter

    ClientProxyModel clientProxy

    ClientProxyFactoryModel clientProxyFactory

    SyncWrapperModel syncWrapper
}
