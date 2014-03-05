/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.writer

import freemarker.ext.beans.BeanModel
import freemarker.ext.beans.BeansWrapper
import freemarker.ext.beans.SimpleMapModel
import freemarker.template.*
import groovy.transform.CompileStatic


/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class ModelWriterImpl implements ModelWriter {

    private final Configuration freemarkerConfig

    ModelWriterImpl(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig
    }

    @Override
    void write(Object model, Writer out) {
        write(model, null, out)
    }

    @Override
    void write(Object rawModel, Map<String, Object> params, Writer out) {
        Template template = freemarkerConfig.getTemplate(getTemplateName(rawModel))
        Object model = params == null ? getModel(rawModel) : getModelWithParams(rawModel, params)
        template.process(model, out)
    }

    private static String getTemplateName(Object model) {
        return model.getClass().name + '.ftl'
    }

    private static Object getModel(Object wrappedModel) {
        return new BeanModel(wrappedModel, BeansWrapper.defaultInstance)
    }

    private static Object getModelWithParams(Object wrappedModel, Map<String, Object> params) {
        BeanModel objectModel = new BeanModel(wrappedModel, BeansWrapper.defaultInstance)
        SimpleMapModel paramsModel = new SimpleMapModel(params, BeansWrapper.defaultInstance)

        return new TemplateHashModel() {
            @Override
            TemplateModel get(String key) throws TemplateModelException {
                return 'params' == (key) ? paramsModel : objectModel.get(key)
            }

            @Override
            boolean isEmpty() throws TemplateModelException {
                return objectModel.isEmpty() && paramsModel.isEmpty()
            }
        }
    }
}
