/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor

import com.junbo.langur.processor.handler.RestAdapterGenerator
import com.junbo.langur.processor.handler.RestAdapterParser
import com.junbo.langur.processor.handler.RestResourceHandler
import groovy.transform.CompileStatic

import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
/**
 * Created by kevingu on 11/28/13.
 */
@SupportedAnnotationTypes('com.junbo.langur.core.RestResource')
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@CompileStatic
class RestAdapterProcessor extends AbstractRestResourceProcessor {

    @Override
    protected List<RestResourceHandler> findRestResourceHandlers() {
        return [new RestAdapterParser(), new RestAdapterGenerator()]
    }
}
