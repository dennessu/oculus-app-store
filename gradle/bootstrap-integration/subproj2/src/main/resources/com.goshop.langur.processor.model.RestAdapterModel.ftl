[#-- @ftlvariable name="" type="com.goshop.langur.processor.model.RestAdapterModel" --]

package ${packageName};

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
[#list annotations as annotation]
${annotation}
[/#list]
public class ${className} {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("default${adapteeName}")
    private ${adapteeType} __adaptee;

    public ${adapteeType} getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(${adapteeType} adaptee) {
        __adaptee = adaptee;
    }

    [#list restMethods as restMethod]
        [@includeModel model=restMethod indent=true/]

    [/#list]
}
