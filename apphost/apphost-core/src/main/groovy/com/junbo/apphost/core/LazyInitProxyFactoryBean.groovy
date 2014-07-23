package com.junbo.apphost.core

import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.aop.target.LazyInitTargetSource
import org.springframework.beans.factory.BeanFactory

/**
 * Created by contractor5 on 7/23/2014.
 */
class LazyInitProxyFactoryBean extends ProxyFactoryBean {

    LazyInitProxyFactoryBean() {
        this.targetSource = new LazyInitTargetSource()
        this.proxyTargetClass = true
    }

    @Override
    void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory)

        ((LazyInitTargetSource) targetSource).setBeanFactory(beanFactory)
    }

    void setTargetBeanName(String name) {
        ((LazyInitTargetSource) targetSource).setTargetBeanName(name)
    }
}
