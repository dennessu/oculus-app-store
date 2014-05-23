[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.param.BeanParameterModel" --]

[#if !(params.includeBean?has_content) || params.includeBean]

[#list parameters as parameter]
    [@includeModel model=parameter
        includePath=params.includePath
        includeHeader=params.includeHeader
        includeQuery=params.includeQuery
        includeForm=params.includeForm
        includeEntity=params.includeEntity
    /]
[/#list]

[/#if]