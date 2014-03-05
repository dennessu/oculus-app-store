/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.hk2;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.spi.ComponentProvider;

import javax.annotation.Priority;
import java.util.Set;

/**
 * Java doc for the service locator.
 */
@Priority(50000)
public class ServiceLocatorComponentProvider implements ComponentProvider {

    private static ServiceLocator serviceLocator;

    public static ServiceLocator getServiceLocator() {
        if (serviceLocator == null) {
            throw new IllegalStateException("serviceLocator is null");
        }

        return serviceLocator;
    }

    @Override
    public void initialize(ServiceLocator locator) {
        if (locator == null) {
            throw new IllegalArgumentException("locator is null");
        }

        if (serviceLocator != null) {
            throw new IllegalStateException("serviceLocator is not null");
        }

        serviceLocator = locator;
    }

    @Override
    public boolean bind(Class<?> component, Set<Class<?>> providerContracts) {
        return false;
    }

    @Override
    public void done() {
        serviceLocator = null;
    }
}
