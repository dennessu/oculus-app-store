[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.param.PathParameterModel" --]

[#if !(params.includePath?has_content) || params.includePath]

__uriBuilder.resolveTemplate("${pathName}", __pathParamTranscoder.encode(${paramName}));

[/#if]