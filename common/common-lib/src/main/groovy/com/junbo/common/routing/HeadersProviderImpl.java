package com.junbo.common.routing;

import com.junbo.langur.core.client.HeadersProvider;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class HeadersProviderImpl implements HeadersProvider {
    @Override
    public MultivaluedMap<String, Object> getHeaders() {
        return new MultivaluedHashMap<>();
    }
}
