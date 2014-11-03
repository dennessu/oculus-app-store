/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.webflow

import com.junbo.common.cloudant.json.annotations.CloudantIgnore
import com.junbo.common.model.ResourceMeta
import com.junbo.langur.core.webflow.state.FlowState
import groovy.transform.CompileStatic
/**
 * ConversationEntity.
 */
@CompileStatic
class ConversationEntity extends ResourceMeta<String> {
    @CloudantIgnore
    String id

    String ipAddress

    String conversationVerifyCode

    Map<String, Object> scope

    List<FlowState> flowStack

    @Override
    String getId() {
        return id
    }

    @Override
    void setId(String id) {
        this.id = id
    }
}
