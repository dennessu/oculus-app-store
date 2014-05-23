[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.param.EntityParameterModel" --]

[#if !(params.includeEntity?has_content) || params.includeEntity]

__requestBuilder.setBody(__transcoder.encode(${paramName}));

[/#if]