package com.junbo.apphost.core

import org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.FactoryBean

/**
 * Created by contractor5 on 7/20/2014.
 */
class JunboLazyInitTargetSourceCreator extends LazyInitTargetSourceCreator {

    @Override
    protected AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(
            Class<?> beanClass, String beanName) {
        System.out.println("createBeanFactoryBasedTargetSource" + beanClass + " " + beanName);

        return super.createBeanFactoryBasedTargetSource(beanClass, beanName)
    }
}
