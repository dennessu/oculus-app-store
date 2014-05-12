[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.RestMethodModel" --]

[#list annotations as annotation]
${annotation}
[/#list]
@SuppressWarnings("deprecation")
public void ${methodName}([#list parameters as parameter][@includeModel model=parameter/],[/#list]
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {

    com.junbo.langur.core.promise.Promise<${returnType}> future;

    try {
        ${adapteeType} adaptee = __adaptee;

        // check whether routing is needed
        if (__router != null) {
            String url = __router.getTargetUrl(${adapteeType}.class, new Object[] {
                [#list routeParamExprs as paramExpr]
                ${paramExpr}[#if paramExpr_has_next],[/#if]
                [/#list]
            }, ${routeFallbackToAnyLocal?c});
            if (url != null) {
                adaptee = __clientFactory.create(url);
            }
        }

        future = adaptee.${methodName}(
            [#list parameters as parameter]
            ${parameter.paramName}[#if parameter_has_next],[/#if]
            [/#list]
        );
    } catch (Throwable ex) {
        __asyncResponse.resume(ex);
        return;
    }

    future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<${returnType}>() {
        @Override
        public void invoke(${returnType} result) {
            __asyncResponse.resume(result);
        }
    });

    future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
        @Override
        public void invoke(Throwable result) {
            __asyncResponse.resume(result);
        }
    });
}