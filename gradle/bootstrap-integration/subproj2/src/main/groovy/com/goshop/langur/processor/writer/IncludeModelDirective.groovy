package com.goshop.langur.processor.writer

import freemarker.core.Environment
import freemarker.ext.beans.BeanModel
import freemarker.template.*
import groovy.transform.CompileStatic

@CompileStatic
class IncludeModelDirective implements TemplateDirectiveModel {

    @Override
    void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
    throws TemplateException, IOException {

        Object model = getWrappedModel(params)
        boolean indent = hasIndent(params)

        StringWriter stringWriter = new StringWriter()
        new ModelWriterImpl(env.getConfiguration()).write(model, params, stringWriter)

        if (indent) {
            env.getOut().write("    " + stringWriter.toString().replaceAll("\\n", "\n    "))
        } else {
            env.getOut().write(stringWriter.toString())
        }
    }

    private static boolean hasIndent(Map params) {
        TemplateBooleanModel indent = (TemplateBooleanModel) params.get("indent")
        params.remove("indent")

        return indent != null && indent.getAsBoolean()
    }

    private static Object getWrappedModel(Map params) {
        if (!params.containsKey("model")) {
            throw new IllegalArgumentException("model parameter is required")
        }

        final BeanModel objectModel = (BeanModel) params.get("model")
        if (objectModel == null) {
            throw new IllegalArgumentException("model parameter is null")
        }

        params.remove("model")
        return objectModel.getWrappedObject()
    }
}
