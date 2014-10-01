package com.junbo.apphost.core

import groovy.transform.CompileStatic
import org.glassfish.hk2.api.Injectee
import org.glassfish.hk2.api.InjectionResolver
import org.glassfish.hk2.api.ServiceHandle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
@javax.inject.Singleton
class AutowiredInjectResolver implements InjectionResolver<Autowired> {

    private final ApplicationContext ctx

    private final ConcurrentMap<String, Object> beanCache;

    AutowiredInjectResolver(ApplicationContext ctx) {
        this.ctx = ctx
        this.beanCache = new ConcurrentHashMap<>();
    }

    @Override
    Object resolve(Injectee injectee, ServiceHandle<?> root) {
        if (injectee.requiredType instanceof Class &&
                ApplicationContext.isAssignableFrom((Class<?>) injectee.requiredType)) {

            return ctx
        }

        AnnotatedElement parent = injectee.parent
        String beanName = null

        if (parent != null) {
            Qualifier an = parent.getAnnotation(Qualifier)

            if (an != null) {
                beanName = an.value()
            }
        }

        return getBeanFromSpringContextWithCache(beanName, injectee.requiredType)
    }

    private Object getBeanFromSpringContextWithCache(String beanName, Type beanType) {
        def key = beanType.toString() + ":" + beanName
        def obj = beanCache.get(key)
        if (obj == null) {
            obj = getBeanFromSpringContext(beanName, beanType)
            beanCache.putIfAbsent(key, obj)
        }

        return obj
    }

    private Object getBeanFromSpringContext(String beanName, Type beanType) {

        Class<?> bt = getClassFromType(beanType)
        if (beanName != null) {
            return ctx.getBean(beanName, bt)
        }

        // check if it wants to autowire a collection of beans of the given type
        if (Collection.isAssignableFrom(bt)) {
            if (beanType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) beanType
                bt = (Class<?>) pt.actualTypeArguments[0]

                Map<String, ?> beans = ctx.getBeansOfType(bt, true, false)
                return new ArrayList(beans.values())
            }
        }

        Map<String, ?> beans = ctx.getBeansOfType(bt, true, false)

        if (beans == null || beans.size() != 1) {
            throw new IllegalArgumentException("No (or multiple) beans found. Resolution failed for type ${beanType}.")
        }

        return beans.values().iterator().next()
    }

    private static Class<?> getClassFromType(Type type) {

        if (type instanceof Class) {
            return (Class<?>) type
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type

            return (Class<?>) pt.rawType
        }

        return null
    }

    @Override
    boolean isConstructorParameterIndicator() {
        return false
    }

    @Override
    boolean isMethodParameterIndicator() {
        return false
    }
}