[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.param.FormParameterModel" --]

[#if !(params.includeForm?has_content) || params.includeForm]

if (${paramName} != null) {
[#if collection]
for (${innerParamType} __item : ${paramName}) {
__requestBuilder.addParameter("${formName}", __queryParamTranscoder.encode(__item));
}
[#else]
__requestBuilder.addParameter("${formName}", __queryParamTranscoder.encode(${paramName}));
[/#if]
}

[/#if]