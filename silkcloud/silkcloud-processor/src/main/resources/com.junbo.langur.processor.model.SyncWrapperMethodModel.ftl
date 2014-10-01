[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.SyncWrapperMethodModel" --]

public ${returnType} ${methodName}([#list parameters as parameter]final ${parameter.paramType} ${parameter.paramName}[#if parameter_has_next], [/#if][/#list]) {
    return __wrapped.${methodName}([#list parameters as parameter]${parameter.paramName}[#if parameter_has_next], [/#if][/#list]).get();
}
