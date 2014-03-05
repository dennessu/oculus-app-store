[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.ClientMethodModel" --]

public Promise<${returnType}> ${methodName}([#list parameters as parameter]${parameter.paramType} ${parameter.paramName}[#if parameter_has_next], [/#if][/#list]) {

    javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
    __uriBuilder.path("${path}");

    AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepare${httpMethodName}("http://127.0.0.1"); // the url will be overwritten later

    for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
        for (Object value : entry.getValue()) {
            __requestBuilder.addHeader(entry.getKey(), value.toString());
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

    Promise<Response> __future;

    try {
        __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
    } catch (java.io.IOException ex) {
        throw new RuntimeException(ex);
    }

    return __future.then(new Promise.Func<Response, Promise<${returnType}>>() {
        @Override
        public Promise<${returnType}> apply(Response response) {
            if (response.getStatusCode() / 100 == 2) {
                try {
                    return Promise.pure(__transcoder.<${returnType}>decode(new TypeReference<${returnType}>() {}, response.getResponseBody()));
                } catch (java.io.IOException ex) {
                     throw new RuntimeException(ex);
                }
            } else {
                throw new ClientResponseException(response);
            }
        }
    });
}
