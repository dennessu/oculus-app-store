[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.CollectionMappingMethodModel" --]

public [@includeModel model=returnType/] ${name}(
    [@includeModel model=sourceParameter/]
    [#if alternativeSourceParameter??], [@includeModel model=alternativeSourceParameter/][/#if]
    [#if contextParameter??], [@includeModel model=contextParameter/][/#if]
) {

[#if contextParameter??]
    if (${contextParameter.name}.getSkipMapping() == Boolean.TRUE) {
        return null;
    }
[/#if]

    if (${sourceParameter.name} == null
[#if alternativeSourceParameter??]
        && ${alternativeSourceParameter.name} == null
[/#if]) {
        return null;
    }

    [@includeModel model=returnType/] __result = new [@includeModel model=returnType.implementationType!returnType/]();

    for ([@includeModel model=sourceParameter.type.typeParameters[0]/] __sourceItem :
        (${sourceParameter.name}[#if alternativeSourceParameter??] == null ? ${alternativeSourceParameter.name} : ${sourceParameter.name}[/#if])) {

        boolean __skipped = false;

    [#if contextParameter??]
        ElementMappingFilter __filter = ${contextParameter.name}.getElementMappingFilter();
        ElementMappingEvent __event = null;
        if (__filter != null) {
            __event = new ElementMappingEvent();

            __event.setSourceElementType(${sourceParameter.type.typeParameters[0].name}.class);
            __event.setSourceElement(__sourceItem);
            __event.setTargetElementType(${returnType.typeParameters[0].name}.class);

            __skipped = __filter.skipElementMapping(__event, ${contextParameter.name});
        }
    [/#if]

        if (!__skipped) {

    [#if contextParameter??]
            if (__filter != null) {
                __filter.beginElementMapping(__event, ${contextParameter.name});
                __sourceItem = ([@includeModel model=sourceParameter.type.typeParameters[0]/]) __event.getSourceElement();
            }
    [/#if]

            [@includeModel model=returnType.typeParameters[0]/] __targetItem =
                [@includeModel model=elementMappingMethod source="__sourceItem" context=(contextParameter.name)!/];

            __result.add(__targetItem);

    [#if contextParameter??]
            if (__filter != null) {
                __filter.endElementMapping(__event, ${contextParameter.name});
            }
    [/#if]
        }
    }

    return __result;
}
