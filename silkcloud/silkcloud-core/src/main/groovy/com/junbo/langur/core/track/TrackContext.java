/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.track;

/**
 * The context to use by user tracker.
 */
public interface TrackContext {
    Long getCurrentUserId();
    String getCurrentClientId();
    String getCurrentScopes();
    boolean getDebugEnabled();
}
