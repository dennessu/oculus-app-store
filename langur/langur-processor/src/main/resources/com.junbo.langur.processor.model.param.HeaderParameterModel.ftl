[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.param.HeaderParameterModel" --]

if (${paramName} != null) {
[#if collection]
for (${innerParamType} __item : ${paramName}) {
__requestBuilder.addHeader("${headerName}", __item);
}
[#else]
__requestBuilder.addHeader("${headerName}", ${paramName});
[/#if]
}