package com.junbo.apphost.core

import groovy.transform.CompileStatic

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created by contractor5 on 7/21/2014.
 */
@CompileStatic
class CachedResourceClassLoader extends ClassLoader {

    private final ConcurrentMap<String, URL> cachedResources = new ConcurrentHashMap<>()

    private final String resourcePrefix

    CachedResourceClassLoader(ClassLoader classLoader, String resourcePrefix) {
        super(classLoader)

        this.resourcePrefix = resourcePrefix
    }

    @Override
    URL getResource(String name) {
        if (name == null) {
            return null
        }

        if (!name.startsWith(resourcePrefix)) {
            return null
        }

        URL resource = cachedResources.get(name)
        if (resource == null) {

            resource = super.getResource(name)
            if (resource != null) {
                cachedResources.putIfAbsent(name, resource)
            }
        }

        return resource
    }

    @Override
    Enumeration<URL> getResources(String name) throws IOException {
        throw new IllegalStateException("Not supported.")
    }
}
