package com.junbo.langur.core.transaction

import groovy.transform.CompileStatic
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.transaction.interceptor.TransactionInterceptor

/**
 * Created by kg on 3/5/14.
 */
@CompileStatic
class TransactionInterceptorPostProcessor implements BeanFactoryPostProcessor {

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForType(TransactionInterceptor, true, false)

        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName)
            beanDefinition.setBeanClassName(AsyncTransactionInterceptor.name)
        }
    }
}
