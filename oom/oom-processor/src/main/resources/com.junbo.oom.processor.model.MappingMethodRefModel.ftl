[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.MappingMethodRefModel" --]
[#if declaringMapper??]${declaringMapper.variableName}.[/#if]${name}(
    ${params.source}
    [#if hasAlternativeSourceParameter], [#if params.alternativeSource?has_content]${params.alternativeSource}[#else]null[/#if][/#if]
    [#if hasContextParameter], [#if params.context?has_content]${params.context}[#else]null[/#if][/#if]
)