package com.goshop.langur.processor.handler

import com.goshop.langur.processor.model.ClientProxyModel
import com.goshop.langur.processor.model.RestAdapterModel
import groovy.transform.CompileStatic

import javax.annotation.processing.ProcessingEnvironment

@CompileStatic
class HandlerContext {

    ProcessingEnvironment processingEnv

    RestAdapterModel restAdapter

    ClientProxyModel clientProxy
}
