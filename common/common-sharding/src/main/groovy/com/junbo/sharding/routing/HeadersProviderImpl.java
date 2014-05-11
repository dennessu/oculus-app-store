package com.junbo.sharding.routing;

import com.junbo.common.util.Context;
import com.junbo.langur.core.client.HeadersProvider;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class HeadersProviderImpl implements HeadersProvider {
    @Override
    public MultivaluedMap<String, String> getHeaders() {
        // forward all headers
        ContainerRequestContext requestContext = Context.get().getRequestContext();
        if (requestContext != null) {
            return requestContext.getHeaders();
        }
        return new MultivaluedHashMap<>();
    }
}
