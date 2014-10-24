/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.entitlement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.entitlement.spec.model.Entitlement;

/**
 * The SewerEntitlement class.
 */
public class SewerEntitlement extends Entitlement {

    @JsonProperty("item")
    private JsonNode itemNode;

    public JsonNode getItemNode() {
        return itemNode;
    }

    public void setItemNode(JsonNode itemNode) {
        this.itemNode = itemNode;
    }
}
