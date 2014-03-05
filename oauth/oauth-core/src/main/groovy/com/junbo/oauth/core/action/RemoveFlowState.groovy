/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.db.repo.FlowStateRepository
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
class RemoveFlowState implements Action {

    private FlowStateRepository flowStateRepository

    @Required
    void setFlowStateRepository(FlowStateRepository flowStateRepository) {
        this.flowStateRepository = flowStateRepository
    }

    @Override
    boolean execute(ServiceContext context) {
        def flowState = ServiceContextUtil.getFlowState(context)

        if (flowState != null) {
            flowStateRepository.delete(flowState.id)
        }

        return true
    }
}
