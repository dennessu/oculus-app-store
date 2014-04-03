[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.param.QueryParameterModel" --]

if (${paramName} != null) {
[#if collection]
for (${innerParamType} __item : ${paramName}) {
__requestBuilder.addQueryParameter("${queryName}", __queryParamTranscoder.encode(__item));
}
[#else]
    __requestBuilder.addQueryParameter("${queryName}", __queryParamTranscoder.encode(${paramName}));
[/#if]
}
