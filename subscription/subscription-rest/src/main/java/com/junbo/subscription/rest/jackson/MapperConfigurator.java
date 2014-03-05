package com.junbo.subscription.rest.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.junbo.common.jackson.deserializer.ResourceAwareDeserializationContext;
import com.junbo.common.jackson.serializer.ResourceAwareSerializerProvider;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class MapperConfigurator implements ContextResolver<ObjectMapper> {
    private ObjectMapper mapper;

    public MapperConfigurator() {
        mapper = new ObjectMapper(null,
                new ResourceAwareSerializerProvider(),
                new ResourceAwareDeserializationContext());

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        mapper.setDateFormat(new ISO8601DateFormat());

    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
