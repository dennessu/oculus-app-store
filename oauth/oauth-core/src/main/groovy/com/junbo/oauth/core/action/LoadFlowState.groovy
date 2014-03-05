/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.db.repo.FlowStateRepository
import com.junbo.oauth.spec.model.FlowState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class LoadFlowState implements Action {
    private FlowStateRepository flowStateRepository

    @Required
    void setFlowStateRepository(FlowStateRepository flowStateRepository) {
        this.flowStateRepository = flowStateRepository
    }

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)

        String flowStateId = parameterMap.getFirst(OAuthParameters.FLOW_STATE)

        if (!StringUtils.hasText(flowStateId)) {
            return true
        }

        FlowState flowState = flowStateRepository.get(flowStateId)
        if (flowState == null) {
            throw AppExceptions.INSTANCE.invalidFlowState(flowStateId).exception()
        }

        ServiceContextUtil.setFlowState(context, flowState)
        ServiceContextUtil.setLoginState(context, flowState.loginState)

        return true
    }
}
