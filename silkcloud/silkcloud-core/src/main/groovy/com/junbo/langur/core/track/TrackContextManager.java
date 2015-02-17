/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.track;

import com.junbo.configuration.ConfigServiceManager;

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

    public static boolean isRouted() {
        return Boolean.TRUE.equals(isRouted.get());
    }

    public static void setIsRouted(boolean isRouted) {
        TrackContextManager.isRouted.set(isRouted);
    }

    private static TrackContext instance = new TrackContext() {
        private boolean isDebugEnabled = resolveDebugEnabled();

        private boolean resolveDebugEnabled() {
            return ConfigServiceManager.instance().getConfigValueAsBool("common.conf.debugMode", false);
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

    private static ThreadLocal<Boolean> isRouted = new ThreadLocal<>();
}
