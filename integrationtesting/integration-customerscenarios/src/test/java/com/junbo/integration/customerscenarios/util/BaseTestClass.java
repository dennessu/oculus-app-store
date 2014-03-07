/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.integration.customerscenarios.util;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Created by Jason on 3/7/14.
 */
public class BaseTestClass {

    protected final String weakPassword = "password";
    protected final String strongPassword = "Welcome123";

    public BaseTestClass() {
        //set loggging info
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");
        System.setProperty("logback.configurationFile", "logback-test.xml");
    }

    public enum PasswordStrength {
        WEAK,
        STRONG
    }

    public enum UserStatus {
        ACTIVE,
        SUSPENDED,
        BANNED,
        DELETED
    }

    public enum UserPara {
        userName,
        password,
        passwordStrength,
        status
    }

}
