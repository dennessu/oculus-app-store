/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.userlog

import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.error.AppErrorException
import com.junbo.common.error.Error
import com.junbo.common.filter.SequenceIdFilter
import com.junbo.common.id.UserId
import com.junbo.common.util.Context
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.track.TrackContextManager
import com.junbo.langur.core.track.UserLogProcessor
import groovy.transform.CompileStatic
import org.slf4j.MDC
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod

/**
 * Created by x on 9/2/14.
 */
@CompileStatic
class UserLogProcessorImpl implements UserLogProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLogProcessor.class)
    private static boolean isEnabled = ConfigServiceManager.instance().getConfigValueAsBool("common.userlog.enabled", false)
    private UserLogRepository userLogRepo

    @Override
    void process(Object entity) {
        if (!isEnabled) {
            return
        }
        try {
            String requestPath = JunboHttpContext.getRequestUri().getPath();
            if (!Context.get().isInitialRestCall() ||
                    JunboHttpContext.getRequestMethod().equalsIgnoreCase(HttpMethod.GET.toString()) ||
                    requestPath.contains("oauth2") ||
                    requestPath.contains("crypto") ||
                    (
                            requestPath.contains("horizon-api/id") &&
                            !requestPath.contains("horizon-api/id/register")) ||
                    TrackContextManager.isRouted()) {
                return
            }

            String entityId
            Error error
            if (entity instanceof CloudantEntity) {
                CloudantEntity cloudantEntity = (CloudantEntity) entity
                entityId = cloudantEntity.getId()?.toString()
            } else if (entity instanceof EntityLoggable) {
                entityId = ((EntityLoggable) entity).getEntityLogId()
            } else if (entity instanceof AppErrorException) {
                error = ((AppErrorException) entity).error.error()
            }
            String path = JunboHttpContext.getRequestUri().getPath()
            if (JunboHttpContext.getRequestMethod().equalsIgnoreCase(HttpMethod.DELETE.toString())) {
                String[] paths = path.split("/")
                if (paths.length > 3)
                    entityId = paths[3]
            }

            Long userId = TrackContextManager.get().currentUserId
            UserLog userLog = new UserLog(
                    api: path,
                    httpMethod: JunboHttpContext.getRequestMethod(),
                    entityId: entityId,
                    logTime: new Date(),
                    sequenceId: MDC.get(SequenceIdFilter.X_REQUEST_ID)?.toString(),
                    clientId: TrackContextManager.get().currentClientId,
                    userId: userId == null ? null : new UserId(userId),
                    error: error
            )

            if (JunboHttpContext.getRequestMethod().equalsIgnoreCase(HttpMethod.POST.toString())) {
                LOGGER.info("Output Entity ID: $entityId");
            }

            Context.get().registerAsyncTask(new Promise.Func0<Promise>() {
                @Override
                Promise apply() {
                    return getUserLogRepo().create(userLog)
                }
            })
        } catch (Exception e) {
            LOGGER.error("Error occurred while logging user action of [$JunboHttpContext.requestUri.path].", e)
        }
    }

    UserLogRepository getUserLogRepo() {
        return userLogRepo
    }

    void setUserLogRepo(UserLogRepository userLogRepo) {
        this.userLogRepo = userLogRepo
    }
}
