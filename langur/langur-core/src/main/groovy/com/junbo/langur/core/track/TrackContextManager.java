/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.track;

import com.junbo.configuration.ConfigServiceManager;
import org.springframework.util.StringUtils;

/**
 * The track context manager to get the singleton TrackContext.
 */
public class TrackContextManager {
    public static TrackContext get() {
        return instance;
    }

    public static void set(TrackContext trackContext) {
        instance = trackContext;
    }

    private static TrackContext instance = new TrackContext() {
        private boolean isDebugEnabled = resolveDebugEnabled();

        private boolean resolveDebugEnabled() {
            String globalDebugMode = ConfigServiceManager.instance().getConfigValue("common.conf.debugMode");
            if (!StringUtils.isEmpty(globalDebugMode)) {
                return Boolean.parseBoolean(globalDebugMode);
            }

            return false;
        }

        @Override
        public Long getCurrentUserId() {
            return null;
        }

        @Override
        public String getCurrentClientId() {
            return null;
        }

        @Override
        public String getCurrentScopes() {
            return null;
        }

        @Override
        public boolean getDebugEnabled() {
            return this.isDebugEnabled;
        }
    };
}
