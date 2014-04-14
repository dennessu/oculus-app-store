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

    int count = 0;
    if (${sourceParameter.name} != null && ${sourceParameter.name}.size() > count) {
        count = ${sourceParameter.name}.size();
    }

[#if alternativeSourceParameter??]
    if (${alternativeSourceParameter.name} != null && ${alternativeSourceParameter.name}.size() > count) {
        count = ${alternativeSourceParameter.name}.size();
    }
[/#if]

    Iterator<[@includeModel model=sourceParameter.type.typeParameters[0]/]> __sourceIter =
        ${sourceParameter.name} == null ? null : ${sourceParameter.name}.iterator();

[#if alternativeSourceParameter??]
    Iterator<[@includeModel model=sourceParameter.type.typeParameters[0]/]> __alternativeSourceIter =
        ${alternativeSourceParameter.name} == null ? null : ${alternativeSourceParameter.name}.iterator();
[/#if]


    for (int i = 0; i < count; i++) {
        [@includeModel model=sourceParameter.type.typeParameters[0]/] __sourceItem = null;
        if (__sourceIter != null && __sourceIter.hasNext()) {
            __sourceItem = __sourceIter.next();
        }

[#if alternativeSourceParameter??]
        [@includeModel model=sourceParameter.type.typeParameters[0]/] __alternativeSourceItem = null;
        if (__alternativeSourceIter != null && __alternativeSourceIter.hasNext()) {
            __alternativeSourceItem = __alternativeSourceIter.next();
        }
[/#if]

        boolean __skipped = false;

    [#if contextParameter??]
        ItemMappingFilter __filter = ${contextParameter.name}.getItemMappingFilter();
        ItemMappingEvent __event = null;
        if (__filter != null) {
            __event = new ItemMappingEvent();

            __event.setSourceType(${sourceParameter.type.name}.class);
            __event.setSourceItemType(${sourceParameter.type.typeParameters[0].name}.class);
            __event.setSourceItemIndex(i);

            __event.setSource(${sourceParameter.name});
            __event.setSourceItem(__sourceItem);

[#if alternativeSourceParameter??]
            __event.setAlternativeSource(${alternativeSourceParameter.name});
            __event.setAlternativeSourceItem(__alternativeSourceItem);
[/#if]

            __event.setTargetType(${returnType.name}.class);
            __event.setTargetItemType(${returnType.typeParameters[0].name}.class);
            __event.setTargetItemIndex(i);

            __skipped = __filter.skipItemMapping(__event, ${contextParameter.name});
        }
    [/#if]

        if (!__skipped) {

    [#if contextParameter??]
            if (__filter != null) {
                __filter.beginItemMapping(__event, ${contextParameter.name});
                __sourceItem = ([@includeModel model=sourceParameter.type.typeParameters[0]/]) __event.getSourceItem();

            [#if alternativeSourceParameter??]
                __alternativeSourceItem = ([@includeModel model=sourceParameter.type.typeParameters[0]/]) __event.getAlternativeSourceItem();
            [/#if]
           }
    [/#if]

            [@includeModel model=returnType.typeParameters[0]/] __targetItem =
        [#if alternativeSourceParameter??]
            [@includeModel model=elementMappingMethod source="__sourceItem" alternativeSource="__alternativeSourceItem" context=(contextParameter.name)!/];
        [#else]
            [@includeModel model=elementMappingMethod source="__sourceItem" context=(contextParameter.name)!/];
        [/#if]

            if (__targetItem != null) {
                __result.add(__targetItem);
            }

    [#if contextParameter??]
            if (__targetItem == null && ${contextParameter.name}.getAddsNull() == Boolean.TRUE) {
                __result.add(null);
            }
    [/#if]

    [#if contextParameter??]
            if (__filter != null) {
                __filter.endItemMapping(__event, ${contextParameter.name});
            }
    [/#if]
        }
    }

    return __result;
}
