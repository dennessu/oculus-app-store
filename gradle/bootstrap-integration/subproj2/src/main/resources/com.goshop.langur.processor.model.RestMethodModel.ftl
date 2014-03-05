[#-- @ftlvariable name="" type="com.goshop.langur.processor.model.RestMethodModel" --]

[#list annotations as annotation]
${annotation}
[/#list]
public void ${methodName}([#list parameters as parameter][@includeModel model=parameter/],
[/#list]@javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {

    com.google.common.util.concurrent.ListenableFuture<${returnType}> future = __adaptee.${methodName}(
[#list parameters as parameter]
        ${parameter.paramName}[#if parameter_has_next],[/#if]
[/#list]
    );

    com.google.common.util.concurrent.Futures.addCallback(future, new com.google.common.util.concurrent.FutureCallback<${returnType}>() {
        @Override
        public void onSuccess(${returnType} result) {
            __asyncResponse.resume(result);
        }

        @Override
        public void onFailure(Throwable t) {
            __asyncResponse.resume(t);
        }
    });
}