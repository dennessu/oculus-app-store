[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.CollectionMappingMethodModel" --]

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

    for ([@includeModel model=sourceParameter.type.typeParameters[0]/] __sourceItem : ${sourceParameter.name}) {

    [#if contextParameter??]

        ElementMappingFilter __filter = ${contextParameter.name}.getElementMappingFilter();
        if (__filter == null) {

            [@includeModel model=returnType.typeParameters[0]/] __targetItem =
                [@includeModel model=elementMappingMethod source="__sourceItem" context=contextParameter.name/];

            __result.add(__targetItem);

        } else {
            ElementMappingEvent __event = new ElementMappingEvent();

            __event.setSourceElementType(${sourceParameter.type.typeParameters[0].name}.class);
            __event.setSourceElement(__sourceItem);
            __event.setTargetElementType(${returnType.typeParameters[0].name}.class);

            boolean __skipped = __filter.skipElementMapping(__event, ${contextParameter.name});

            if (!__skipped) {
                __filter.beginElementMapping(__event, ${contextParameter.name});

                [@includeModel model=returnType.typeParameters[0]/] __targetItem =
                    [@includeModel model=elementMappingMethod source="__sourceItem" context=contextParameter.name/];

                __result.add(__targetItem);

                __filter.endElementMapping(__event, ${contextParameter.name});
            }
        }

    [#else]
        [@includeModel model=returnType.typeParameters[0]/] __targetItem =
            [@includeModel model=elementMappingMethod source="__sourceItem"/];

        __result.add(__targetItem);
    [/#if]
    }

    return __result;
}
