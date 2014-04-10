[#-- @ftlvariable name="" type="com.junbo.oom.processor.model.BeanMappingMethodModel" --]

public [@includeModel model=returnType/] ${name}([@includeModel model=sourceParameter/][#if contextParameter??], [@includeModel model=contextParameter/][/#if]) {

    if (${sourceParameter.name} == null) {
        return null;
    }

[#if contextParameter??]
    if (${contextParameter.name}.getSkipMapping() == Boolean.TRUE) {
        return null;
    }
[/#if]

    ${returnType.name} __result = new ${returnType.name}();

    [#list propertyMappings as propertyMapping]
        [@includeModel model=propertyMapping sourceBeanName=sourceParameter.name targetBeanName="__result" context=(contextParameter.name)! indent=true/]
    [/#list]

    return __result;
}