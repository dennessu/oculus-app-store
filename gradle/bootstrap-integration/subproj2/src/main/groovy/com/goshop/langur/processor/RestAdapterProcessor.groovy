package com.goshop.langur.processor

import com.goshop.langur.processor.handler.RestAdapterGenerator
import com.goshop.langur.processor.handler.RestAdapterParser
import com.goshop.langur.processor.handler.RestResourceHandler
import groovy.transform.CompileStatic

import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@SupportedAnnotationTypes("com.goshop.langur.core.RestResource")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@CompileStatic
class RestAdapterProcessor extends AbstractRestResourceProcessor {

    @Override
    protected List<RestResourceHandler> getRestResourceHandlers() {
        return [new RestAdapterParser(), new RestAdapterGenerator()]
    }
}
