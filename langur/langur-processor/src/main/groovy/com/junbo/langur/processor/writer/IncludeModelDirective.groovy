/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.writer

import freemarker.core.Environment
import freemarker.ext.beans.BeanModel
import freemarker.template.*
import groovy.transform.CompileStatic

/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class IncludeModelDirective implements TemplateDirectiveModel {

    private static final String INDENT = 'indent'
    private static final String MODEL = 'model'

    @Override
    void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
    throws TemplateException, IOException {

        Object model = getWrappedModel(params)
        boolean indent = hasIndent(params)

        StringWriter stringWriter = new StringWriter()
        new ModelWriterImpl(env.configuration).write(model, params, stringWriter)

        if (indent) {
            env.out.write('    ' + stringWriter.toString().replaceAll('\\n', '\n    '))
        } else {
            env.out.write(stringWriter.toString())
        }
    }

    private static boolean hasIndent(Map params) {
        TemplateBooleanModel indent = (TemplateBooleanModel) params.get(INDENT)
        params.remove(INDENT)

        return indent != null && indent.asBoolean
    }

    private static Object getWrappedModel(Map params) {
        if (!params.containsKey(MODEL)) {
            throw new IllegalArgumentException('model parameter is required')
        }

        BeanModel objectModel = (BeanModel) params.get(MODEL)
        if (objectModel == null) {
            throw new IllegalArgumentException('model parameter is null')
        }

        params.remove(MODEL)
        return objectModel.wrappedObject
    }
}
