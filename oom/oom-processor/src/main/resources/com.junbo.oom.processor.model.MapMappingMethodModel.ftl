[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.MapMappingMethodModel" --]
[#import "/com.junbo.oom.processor.model.common.ftl" as common]

public [@includeModel model=returnType/] ${name}([@includeModel model=sourceParameter/][#if contextParameter??], [@includeModel model=contextParameter/][/#if]) {

    if (${sourceParameter.name} == null) {
        return null;
    }

[#if contextParameter??]
    if (${contextParameter.name}.getSkipMapping() == Boolean.TRUE) {
        return null;
    }
[/#if]

    [@includeModel model=returnType/] __result = new [@includeModel model=returnType.implementationType!returnType/]();

    for (Map.Entry<[@common.includeModels models=sourceParameter.type.typeParameters/]> __entry : ${sourceParameter.name}.entrySet()) {

        [@includeModel model=returnType.typeParameters[0]/] __key =
            [@includeModel model=keyMappingMethod source="__entry.getKey()" context=(contextParameter.name)!/];

    [#if contextParameter??]
        PropertyMappingFilter __filter = ${contextParameter.name}.getPropertyMappingFilter();
        if (__filter == null) {

            [@includeModel model=returnType.typeParameters[1]/] __value;
            __value = [@includeModel model=valueMappingMethod source="__entry.getValue()" context=contextParameter.name/];
            __result.put(__key, __value);

        } else {
            PropertyMappingEvent __event = new PropertyMappingEvent();

            __event.setSourceType(${(sourceParameter.type.implementationType!sourceParameter.type).name}.class);
            __event.setSourcePropertyType(${sourceParameter.type.typeParameters[1].name}.class);
            __event.setSourcePropertyName(__entry.getKey().toString());
            __event.setSourceProperty(__entry.getValue());

            __event.setTargetType(${(returnType.implementationType!returnType).name}.class);
            __event.setTargetPropertyType(${returnType.typeParameters[1].name}.class);
            __event.setTargetPropertyName(__key.toString());

            boolean __skipped = __filter.skipPropertyMapping(__event, ${contextParameter.name});

            if (!__skipped) {
                __filter.beginPropertyMapping(__event, ${contextParameter.name});

                [@includeModel model=returnType.typeParameters[1]/] __value;
                __value = [@includeModel model=valueMappingMethod source="__entry.getValue()" context=contextParameter.name/];
                __result.put(__key, __value);

                __filter.endPropertyMapping(__event, ${contextParameter.name});
            }
        }
    [#else]

        [@includeModel model=returnType.typeParameters[1]/] __value;
        __value = [@includeModel model=valueMappingMethod source="__entry.getValue()"/];
        __result.put(__key, __value);

    [/#if]

    }

    return __result;
}

