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

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
@javax.inject.Singleton
class AutowiredInjectResolver implements InjectionResolver<Autowired> {

    private final ApplicationContext ctx

    AutowiredInjectResolver(ApplicationContext ctx) {
        this.ctx = ctx
    }

    @Override
    Object resolve(Injectee injectee, ServiceHandle<?> root) {
        if (ApplicationContext.isAssignableFrom((Class<?>) injectee.requiredType)) {
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

        return getBeanFromSpringContext(beanName, injectee.requiredType)
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