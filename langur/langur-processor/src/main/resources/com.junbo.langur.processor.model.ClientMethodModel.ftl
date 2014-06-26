[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.ClientMethodModel" --]

public Promise<${returnType}> ${methodName}([#list parameters as parameter]final ${parameter.paramType} ${parameter.paramName}[#if parameter_has_next], [/#if][/#list]) {
    final long __startTime = System.currentTimeMillis();
    final Date __startDate = new Date();
    final String __pathTemplate = __target + "${path}";

    javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
    __uriBuilder.path("${path}");

    AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepare${httpMethodName}("http://127.0.0.1"); // the url will be overwritten later

    __addHeadersFromHeadersProvider(__requestBuilder);

    for (java.util.Map.Entry<String, java.util.List<String>> entry : __headers.entrySet()) {
        // skip headers already handled by ClientProxy
        if (entry.getKey().equalsIgnoreCase("Accept") || entry.getKey().equalsIgnoreCase("Content-Type")) {
            continue;
        }
        for (String value : entry.getValue()) {
            __requestBuilder.addHeader(entry.getKey(), value);
        }
    }

    [#list accepts as accept]
    __requestBuilder.addHeader("Accept", "${accept}");
    [/#list]

    [#if contentType??]
    __requestBuilder.setHeader("Content-Type", "${contentType}");
    [/#if]


    if (__accessTokenProvider != null) {
        String __accessToken = __accessTokenProvider.getAccessToken();
        __requestBuilder.addHeader("Authorization", "Bearer " + __accessToken);
    }

[#if inProcessCallable]
    if (__inProcessCallable && __checkService() != null) {

        [#list parameters as parameter]
            [@includeModel model=parameter indent=true
                includeEntity=false
                includeForm=false
                includeQuery=false
            /]
        [/#list]

        __requestBuilder.setUrl(__uriBuilder.toTemplate());
        com.ning.http.client.Request __request = __requestBuilder.build();

        com.junbo.langur.core.context.JunboHttpContext.JunboHttpContextData __httpContextData =
            __createJunboHttpContextData(__request);

        return com.junbo.langur.core.context.JunboHttpContextScope.with(__httpContextData, __junboHttpContextScopeListeners, new Promise.Func0<Promise<${returnType}>>() {
            @Override
            public Promise<${returnType}> apply() {
                return __service.${methodName}([#list parameters as parameter]${parameter.paramName}[#if parameter_has_next], [/#if][/#list]).then(new Promise.Func<${returnType}, Promise<${returnType}>>() {
                    @Override
                    public Promise<${returnType}> apply(${returnType} __result) {
                        LOGGER.info("(InProc) Method: ${methodName} uri: " + __pathTemplate +" duration: " + (System.currentTimeMillis() - __startTime) + "ms " + __startDate.toString());
                        return Promise.pure(__result);
                    }
                });
            }
        });
    }
[/#if]

    [#list parameters as parameter]
        [@includeModel model=parameter indent=true/]
    [/#list]

    __requestBuilder.setUrl(__uriBuilder.toTemplate());
    com.ning.http.client.Request __request = __requestBuilder.build();

    Promise<Response> __future;

    try {
        __future = Promise.wrap(asGuavaFuture(__client.executeRequest(__request)));
    } catch (java.io.IOException ex) {
        LOGGER.error("(Remote) Method: ${methodName} uri: " + __pathTemplate +" duration: " + (System.currentTimeMillis() - __startTime) + "ms " + __startDate.toString());
        throw new RuntimeException(ex);
    }

    return __future.then(new Promise.Func<Response, Promise<${returnType}>>() {
        @Override
        public Promise<${returnType}> apply(Response response) {
            if (__responseHandler != null) {
                __responseHandler.onResponse(response);
            }

        LOGGER.info("(Remote) Method: ${methodName} uri: " + __pathTemplate +" duration: " + (System.currentTimeMillis() - __startTime) + "ms " + __startDate.toString());
        if (response.getStatusCode() / 100 == 2) {
                try {
                    return Promise.pure(__transcoder.<${returnType}>decode(new TypeReference<${returnType}>() {}, response.getResponseBody()));
                } catch (java.io.IOException ex) {
                     throw new RuntimeException(ex);
                }
            } else {
                __exceptionHandler.handleExceptionResponse(response);
                return Promise.pure(null);
            }
        }
    });
}
