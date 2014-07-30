[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.RestMethodModel" --]

[#list annotations as annotation]
${annotation}
[/#list]
@SuppressWarnings("deprecation")
public void ${methodName}([#list parameters as parameter][@includeModel model=parameter/],[/#list]
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {

    com.junbo.langur.core.context.JunboHttpContext.JunboHttpContextData __junboHttpContextData = __createJunboHttpContextData(${adapteeType}.class);
    final com.junbo.langur.core.context.JunboHttpContextScope __scope = new com.junbo.langur.core.context.JunboHttpContextScope(__junboHttpContextData, __junboHttpContextScopeListeners);

    [#if !authorizationNotRequired]
    __resourceScopeValidator.validateScope("${adapteeName}.${methodName}");
    [/#if]

    try {
        ${adapteeType} adaptee = __adaptee;

        // check whether routing is needed
        if (__router != null) {
            String url = __router.getTargetUrl(${adapteeType}.class, new Object[] {
                [#list routeParamExprs as paramExpr]
                ${paramExpr}[#if paramExpr_has_next],[/#if]
                [/#list]
            });
            if (url != null) {
                adaptee = __clientFactory.create(url);
           }
        }

        Promise<${returnType}> future = adaptee.${methodName}(
            [#list parameters as parameter]
            ${parameter.paramName}[#if parameter_has_next],[/#if]
            [/#list]
        ).then(new Promise.Func<${returnType}, Promise<${returnType}>>() {
            @Override
            public Promise<${returnType}> apply(final ${returnType} result) {
                return __afterInvocationFilter.process().then(new Promise.Func<Void, Promise<${returnType}>>() {
                    @Override
                    public Promise<${returnType}> apply(Void value) {
                        return Promise.pure(result);
                    }
                });
            }
        });

        future.onSuccess(new Promise.Callback<${returnType}>() {
            @Override
            public void invoke(${returnType} result) {
                __processResponseData();

                __asyncResponse.resume(result);
                __scope.close();
            }
        });

        future.onFailure(new Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
                __scope.close();
            }
        });
    } catch (Throwable ex) {
        __asyncResponse.resume(ex);
        __scope.close();
        return;
    }
}