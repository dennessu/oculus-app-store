package com.junbo.apphost.core

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate
import org.springframework.beans.factory.xml.DocumentDefaultsDefinition
import org.springframework.beans.factory.xml.XmlReaderContext
import org.springframework.core.env.Environment
import org.w3c.dom.Element

/**
 * Created by contractor5 on 7/20/2014.
 */
class JunboBeanDefinitionParserDelegate extends BeanDefinitionParserDelegate {

    JunboBeanDefinitionParserDelegate(XmlReaderContext readerContext, Environment environment) {
        super(readerContext, environment)
    }

    @Override
    protected void populateDefaults(DocumentDefaultsDefinition defaults, DocumentDefaultsDefinition parentDefaults, Element root) {
        super.populateDefaults(defaults, parentDefaults, root)

        defaults.lazyInit = TRUE_VALUE
    }
}
