/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.FlowStateDAO
import com.junbo.oauth.db.entity.FlowStateEntity
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.FlowStateRepository
import com.junbo.oauth.spec.model.FlowState
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Javadoc.
 */
@CompileStatic
class FlowStateRepositoryImpl implements FlowStateRepository {
    private FlowStateDAO flowStateDAO

    private TokenGenerator tokenGenerator

    private long defaultFlowStateExpiration

    @Required
    void setFlowStateDAO(FlowStateDAO flowStateDAO) {
        this.flowStateDAO = flowStateDAO
    }

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Required
    void setDefaultFlowStateExpiration(long defaultFlowStateExpiration) {
        this.defaultFlowStateExpiration = defaultFlowStateExpiration
    }

    @Override
    FlowState get(String id) {
        Assert.notNull(id)
        return wrap(flowStateDAO.get(id))
    }

    @Override
    void saveOrUpdate(FlowState flowState) {
        if (flowState.id == null) {
            flowState.id = tokenGenerator.generateFlowStateId()
        }

        if (flowState.expiredBy == null) {
            flowState.expiredBy = new Date(System.currentTimeMillis() + defaultFlowStateExpiration * 1000)
        }

        flowStateDAO.update(unwrap(flowState))
    }

    @Override
    void delete(String id) {
        flowStateDAO.delete(id)
    }

    private static FlowStateEntity unwrap(FlowState flowState) {
        if (flowState == null) {
            return null
        }

        return new FlowStateEntity(
                id: flowState.id,
                oAuthInfo: flowState.oAuthInfo,
                loginState: flowState.loginState,
                redirectUri: flowState.redirectUri,
                expiredBy: flowState.expiredBy
        )
    }

    private static FlowState wrap(FlowStateEntity entity) {
        if (entity == null) {
            return null
        }

        return new FlowState(
                id: entity.id,
                oAuthInfo: entity.oAuthInfo,
                loginState: entity.loginState,
                redirectUri: entity.redirectUri,
                expiredBy: entity.expiredBy
        )
    }
}
