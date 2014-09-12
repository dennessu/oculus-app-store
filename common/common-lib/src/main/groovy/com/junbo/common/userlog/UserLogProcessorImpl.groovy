package com.junbo.common.userlog

import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.error.AppErrorException
import com.junbo.common.error.Error
import com.junbo.common.filter.SequenceIdFilter
import com.junbo.common.id.UserId
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.track.TrackContextManager
import com.junbo.langur.core.track.UserLogProcessor
import groovy.transform.CompileStatic
import org.apache.log4j.MDC
import org.springframework.http.HttpMethod

/**
 * Created by x on 9/2/14.
 */
@CompileStatic
class UserLogProcessorImpl implements UserLogProcessor {
    private static boolean isEnabled = "true".equalsIgnoreCase(
            ConfigServiceManager.instance().getConfigValue("common.userlog.enabled"))
    private UserLogRepository userLogRepo

    @Override
    void process(Object entity) {
        if (!isEnabled) {
            return
        }
        try {
            if (JunboHttpContext.getRequestMethod().equalsIgnoreCase(HttpMethod.GET.toString()) ||
                    JunboHttpContext.getRequestUri().getPath().contains("oauth2") || TrackContextManager.isRouted()) {
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

            getUserLogRepo().create(userLog).get()
        } catch (Exception ignore) {
        }
    }

    UserLogRepository getUserLogRepo() {
        return userLogRepo
    }

    void setUserLogRepo(UserLogRepository userLogRepo) {
        this.userLogRepo = userLogRepo
    }
}
