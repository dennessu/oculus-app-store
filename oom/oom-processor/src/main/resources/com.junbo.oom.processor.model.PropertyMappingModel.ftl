[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.PropertyMappingModel" --]
{

    [@includeModel model=sourceProperty.type/] __sourceProperty = ${params.sourceBeanName}.${sourceProperty.getterString}();

[#if params.context?has_content]
    PropertyMappingFilter __filter = ${params.context}.getPropertyMappingFilter();
    if (__filter == null) {
        ${params.targetBeanName}.${targetProperty.setterString}([@includeModel model=mappingMethodRef source="__sourceProperty" context=params.context/]);
    } else {
        PropertyMappingEvent __event = new PropertyMappingEvent();

        __event.setSourceType(${sourceProperty.owner.name}.class);
        __event.setSourcePropertyType(${sourceProperty.type.name}.class);
        __event.setSourcePropertyName("${sourceProperty.name}");
        __event.setSourceProperty(__sourceProperty);

        __event.setTargetType(${targetProperty.owner.name}.class);
        __event.setTargetPropertyType(${targetProperty.type.name}.class);
        __event.setTargetPropertyName("${targetProperty.name}");

        boolean __skipped = __filter.skipPropertyMapping(__event, ${params.context});

        if (!__skipped) {
            __filter.beginPropertyMapping(__event, ${params.context});

            ${params.targetBeanName}.${targetProperty.setterString}([@includeModel model=mappingMethodRef source="__sourceProperty" context=params.context/]);

            __filter.endPropertyMapping(__event, ${params.context});
        }
    }
[#else]
    ${params.targetBeanName}.${targetProperty.setterString}([@includeModel model=mappingMethodRef source="__sourceProperty"/]);
[/#if]

}
