/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.oom.core.MappingContext;
import org.springframework.stereotype.Component;

/**
 * Created by kg on 3/26/2014.
 * TODO: to move to langur
 */
@Component
public class JsonNodeSelfMapper {

    public JsonNode filterJsonNode(JsonNode self, MappingContext context) {
        if (self == null) {
            return null;
        }

        // todo: do the filter later.
        return self.deepCopy();
    }

    public JsonNode mergeJsonNode(JsonNode source, JsonNode base, MappingContext context) {
        if (source == null) {
            return null;
        }

        // todo: do the merge later.
        return source.deepCopy();
    }
}
