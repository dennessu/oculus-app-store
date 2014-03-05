package com.goshop.catalog.rest.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class MapperConfigurator implements ContextResolver<ObjectMapper> {
    private ObjectMapper mapper;

    public MapperConfigurator() {
        mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        mapper.setDateFormat(new ISO8601DateFormat());
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
