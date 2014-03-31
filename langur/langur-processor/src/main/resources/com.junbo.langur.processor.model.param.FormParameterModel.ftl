[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.param.FormParameterModel" --]

if (${paramName} != null) {
    __requestBuilder.addParameter("${formName}", ${paramName});
}
