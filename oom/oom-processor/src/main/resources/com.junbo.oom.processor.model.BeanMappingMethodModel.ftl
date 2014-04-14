[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.BeanMappingMethodModel" --]

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

    [@includeModel model=returnType/] __result = new [@includeModel model=returnType/]();

    [#list propertyMappings as propertyMapping]
        [@includeModel model=propertyMapping
            sourceBeanName=sourceParameter.name
            alternativeSourceBeanName=(alternativeSourceParameter.name)!
            targetBeanName="__result"
            context=(contextParameter.name)!
            indent=true/]

    [/#list]

    return __result;
}