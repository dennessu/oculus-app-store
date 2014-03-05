[#-- @ftlvariable name="" type="com.goshop.langur.processor.model.ClientMethodModel" --]

public ListenableFuture<${returnType}> ${methodName}([#list parameters as parameter]${parameter.paramType} ${parameter.paramName}[#if parameter_has_next], [/#if][/#list]) {

    javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
    __uriBuilder.path("${path}");

    AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepare${httpMethodName}(__uriBuilder.toTemplate());

    for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
        for (Object value : entry.getValue()) {
            __requestBuilder.addHeader(entry.getKey(), __transcoder.encode(value));
        }
    }

    [#list accepts as accept]
    __requestBuilder.addHeader("Accept", "${accept}");
    [/#list]

    [#if contentType??]
    __requestBuilder.addHeader("Content-Type", "${contentType}");
    [/#if]

    [#list parameters as parameter]
        [@includeModel model=parameter indent=true/]
    [/#list]

    __requestBuilder.setUrl(__uriBuilder.toTemplate());

    ListenableFuture<Response> __future;

    try {
        __future = asGuavaFuture(__requestBuilder.execute());
    } catch (java.io.IOException ex) {
        throw new RuntimeException(ex);
    }

    return Futures.transform(__future, new Function<Response, ${returnType}>() {
        @Override
        public ${returnType} apply(Response response) {
            if (response.getStatusCode() / 100 == 2) {
                try {
                    return __transcoder.decode(new TypeReference<${returnType}>() {}, response.getResponseBody());
                } catch (java.io.IOException ex) {
                     throw new RuntimeException(ex);
                }
            } else {
                throw new ClientResponseException(response);
            }
        }
    });
}