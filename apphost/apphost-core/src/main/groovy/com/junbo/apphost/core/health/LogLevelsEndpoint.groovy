package com.junbo.apphost.core.health
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import groovy.transform.CompileStatic
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
/**
 * Created by kgu on 6/3/14.
 */
@Path("/loggers")
@Produces(["application/json"])
@CompileStatic
class LogLevelsEndpoint {

    @Autowired
    private ApplicationContext applicationContext

    private static final ConcurrentMap<String, Object> overrides = new ConcurrentHashMap<>()
    private static final Object none = new Object()

    static class LogInfo {
        String name;
        String level;
        String override;

        LogInfo(String name, Level level, Level override) {
            this.name = name
            this.level = toString(level)
            this.override = toString(override)
        }

        String toString(Level level) {
            if (level == null) return null
            return level.toString()
        }
    }

    @GET
    List<LogInfo> getLogInfo(@QueryParam("showall") Boolean showall) {
        if (showall == null) showall = false

        List<LogInfo> result = new ArrayList<>();

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (ch.qos.logback.classic.Logger log : lc.getLoggerList()) {
            Level level = log.getLevel()
            Level override = null

            Level originalLevel = getOverrideLevel(log.getName())
            if (originalLevel != null) {
                override = level;
                level = originalLevel
            }

            if (showall || level != null || originalLevel != null ||
                    overrides.containsKey(log.getName())) {
                result.add(new LogInfo(log.getName(), level, override));
            }
        }
        return result;
    }

    @GET
    @Path("/override")
    LogInfo overrideLogInfo(@QueryParam("name") String name, @QueryParam("level") String level) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = lc.getLogger(name);
        if (logger == null) {
            throw new RuntimeException("Invalid logger name: " + name)
        }
        putOverrideLevel(name, logger.getLevel());
        logger.setLevel(parseLevel(level));

        return new LogInfo(logger.getName(), getOverrideLevel(name), logger.getLevel());
    }

    @GET
    @Path("/reset")
    List<LogInfo> overrideLogInfo() {
        List<LogInfo> result = new ArrayList<>();
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (String key : overrides.keySet()) {
            Level level = getOverrideLevel(key);
            Logger logger = lc.getLogger(key);
            logger.setLevel(level);

            result.add(new LogInfo(logger.getName(), logger.getLevel(), null))
        }
        overrides.clear()
        return result
    }

    private static Level parseLevel(String level) {
        return Level.valueOf(level)
    }

    private static Level getOverrideLevel(String name) {
        Object o = overrides.get(name);
        if (none.is(o)) {
            return null
        }
        return (Level)o
    }

    private static void putOverrideLevel(String name, Level level) {
        if (level == null) {
            overrides.put(name, none);
        } else {
            overrides.put(name, level);
        }
    }
}
