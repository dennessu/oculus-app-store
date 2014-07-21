[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.SyncWrapperModel" --]

// CHECKSTYLE:OFF

package ${packageName};

public class ${className} {
    private ${interfaceType} __wrapped;

    public ${className}(${interfaceType} wrapped) {
        this.__wrapped = wrapped;
    }

    [#list clientMethods as clientMethod]
        [@includeModel model=clientMethod indent=true/]

    [/#list]
}
