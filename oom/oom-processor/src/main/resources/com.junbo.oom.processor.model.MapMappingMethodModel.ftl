[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.MapMappingMethodModel" --]
[#import "/com.junbo.oom.processor.model.common.ftl" as common]

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

    Set<[@includeModel model=sourceParameter.type.typeParameters[0]/]> __sourceKeySet =
        new HashSet<[@includeModel model=sourceParameter.type.typeParameters[0]/]>();

    if (${sourceParameter.name} != null) {
        __sourceKeySet.addAll(${sourceParameter.name}.keySet());
    }

[#if alternativeSourceParameter??]
    if (${alternativeSourceParameter.name} != null) {
        __sourceKeySet.addAll(${alternativeSourceParameter.name}.keySet());
    }
[/#if]

    for ([@includeModel model=sourceParameter.type.typeParameters[0]/] __sourceKey : __sourceKeySet) {

        [@includeModel model=returnType.typeParameters[0]/] __key =
            [@includeModel model=keyMappingMethod source="__sourceKey" context=(contextParameter.name)!/];

        boolean __skipped = false;

        [@includeModel model=sourceParameter.type.typeParameters[1]/] __sourceValue =
            ${sourceParameter.name} == null ? null : ${sourceParameter.name}.get(__sourceKey);

    [#if alternativeSourceParameter??]
        [@includeModel model=sourceParameter.type.typeParameters[1]/] __alternativeSourceValue =
            ${alternativeSourceParameter.name} == null ? null : ${alternativeSourceParameter.name}.get(__sourceKey);
    [/#if]

    [#if contextParameter??]
        PropertyMappingFilter __filter = ${contextParameter.name}.getPropertyMappingFilter();
        PropertyMappingEvent __event = null;
        if (__filter != null) {
            __event = new PropertyMappingEvent();

            __event.setSourceType(${(sourceParameter.type.implementationType!sourceParameter.type).name}.class);
            __event.setSourcePropertyType(${sourceParameter.type.typeParameters[1].name}.class);
            __event.setSourcePropertyName(__sourceKey.toString());

            __event.setSource(${sourceParameter.name});
            __event.setSourceProperty(__sourceValue);

        [#if alternativeSourceParameter??]
            __event.setAlternativeSource(${alternativeSourceParameter.name});
            __event.setAlternativeSourceProperty(__alternativeSourceValue);
        [/#if]

            __event.setTargetType(${(returnType.implementationType!returnType).name}.class);
            __event.setTargetPropertyType(${returnType.typeParameters[1].name}.class);
            __event.setTargetPropertyName(__key.toString());

            __skipped = __filter.skipPropertyMapping(__event, ${contextParameter.name});
        }
    [/#if]

        if (!__skipped) {

    [#if contextParameter??]
            if (__filter != null) {
                __filter.beginPropertyMapping(__event, ${contextParameter.name});

                __sourceValue = ([@includeModel model=sourceParameter.type.typeParameters[1]/]) __event.getSourceProperty();

            [#if alternativeSourceParameter??]
                __alternativeSourceValue = ([@includeModel model=sourceParameter.type.typeParameters[1]/]) __event.getAlternativeSourceProperty();
            [/#if]
            }
    [/#if]

            [@includeModel model=returnType.typeParameters[1]/] __value =
            [#if alternativeSourceParameter??]
                [@includeModel model=valueMappingMethod source="__sourceValue" alternativeSource="__alternativeSourceValue" context=(contextParameter.name)!/];
            [#else]
                [@includeModel model=valueMappingMethod source="__sourceValue" context=(contextParameter.name)!/];
            [/#if]

            __result.put(__key, __value);

    [#if contextParameter??]
            if (__filter != null) {
                __filter.endPropertyMapping(__event, ${contextParameter.name});
            }
    [/#if]
        }
    }

    return __result;
}

