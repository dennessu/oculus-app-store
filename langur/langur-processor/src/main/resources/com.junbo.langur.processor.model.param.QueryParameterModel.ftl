[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.param.QueryParameterModel" --]

if (${paramName} != null) {
    __requestBuilder.addQueryParameter("${queryName}", __queryParamTranscoder.encode(${paramName}));
}
