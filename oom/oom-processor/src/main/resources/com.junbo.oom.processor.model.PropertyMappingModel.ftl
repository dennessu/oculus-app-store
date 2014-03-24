[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.PropertyMappingModel" --]
{

    [@includeModel model=sourceProperty.type/] __sourceProperty =
        ${params.sourceBeanName} == null ? null : ${params.sourceBeanName}.${sourceProperty.getterString}();

[#if params.alternativeSourceBeanName?has_content]
    [@includeModel model=sourceProperty.type/] __alternativeSourceProperty =
        ${params.alternativeSourceBeanName} == null ? null : ${params.alternativeSourceBeanName}.${sourceProperty.getterString}();
[/#if]

    boolean __skipped = false;

[#if params.context?has_content]
    PropertyMappingFilter __filter = ${params.context}.getPropertyMappingFilter();
    PropertyMappingEvent __event = null;
    if (__filter != null) {
        __event = new PropertyMappingEvent();

        __event.setSourceType(${sourceProperty.owner.name}.class);
        __event.setSourcePropertyType(${sourceProperty.type.name}.class);
        __event.setSourcePropertyName("${sourceProperty.name}");

        __event.setSource(${params.sourceBeanName});
        __event.setSourceProperty(__sourceProperty);

[#if params.alternativeSourceBeanName?has_content]
        __event.setAlternativeSource(${params.alternativeSourceBeanName});
        __event.setAlternativeSourceProperty(__alternativeSourceProperty);
[/#if]

        __event.setTargetType(${targetProperty.owner.name}.class);
        __event.setTargetPropertyType(${targetProperty.type.name}.class);
        __event.setTargetPropertyName("${targetProperty.name}");

        __skipped = __filter.skipPropertyMapping(__event, ${params.context});
    }
[/#if]

    if (!__skipped) {

[#if params.context?has_content]
        if (__filter != null) {
            __filter.beginPropertyMapping(__event, ${params.context});

            __sourceProperty = ([@includeModel model=sourceProperty.type/]) __event.getSourceProperty();

    [#if params.alternativeSourceBeanName?has_content]
            __alternativeSourceProperty = ([@includeModel model=sourceProperty.type/]) __event.getAlternativeSourceProperty();
    [/#if]
        }
[/#if]

        [@includeModel model=targetProperty.type/] __targetProperty =
            [#if params.alternativeSourceBeanName?has_content]
                [@includeModel model=mappingMethodRef source="__sourceProperty" alternativeSource="__alternativeSourceProperty" context=params.context!/];
            [#else]
                [@includeModel model=mappingMethodRef source="__sourceProperty" context=params.context!/];
            [/#if]

        if (__targetProperty != null) {
            ${params.targetBeanName}.${targetProperty.setterString}(__targetProperty);
        }

[#if params.context?has_content]
        if (__targetProperty == null && ${params.context}.getSetsNull() == Boolean.TRUE) {
            ${params.targetBeanName}.${targetProperty.setterString}(null);
        }
[/#if]

[#if params.context?has_content]
        if (__filter != null) {
            __filter.endPropertyMapping(__event, ${params.context});
        }
[/#if]
    }
}
