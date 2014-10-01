package com.junbo.apphost.core

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader
import org.springframework.beans.factory.xml.XmlReaderContext
import org.springframework.core.env.Environment
import org.w3c.dom.Element

/**
 * Created by contractor5 on 7/20/2014.
 */
class JunboBeanDefinitionDocumentReader extends DefaultBeanDefinitionDocumentReader {

    private Environment environment

    @Override
    void setEnvironment(Environment environment) {
        super.setEnvironment(environment)

        this.environment = environment
    }

    @Override
    protected BeanDefinitionParserDelegate createDelegate(XmlReaderContext readerContext, Element root, BeanDefinitionParserDelegate parentDelegate) {

        BeanDefinitionParserDelegate delegate = new JunboBeanDefinitionParserDelegate(readerContext, this.environment);
        delegate.initDefaults(root, parentDelegate);
        return delegate;
    }
}
