package com.junbo.apphost.core

import com.junbo.configuration.ConfigServiceManager
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate
import org.springframework.beans.factory.xml.DocumentDefaultsDefinition
import org.springframework.beans.factory.xml.XmlReaderContext
import org.springframework.core.env.Environment
import org.w3c.dom.Element

/**
 * Created by contractor5 on 7/20/2014.
 */
class JunboBeanDefinitionParserDelegate extends BeanDefinitionParserDelegate {

    private final String defaultLazyInit

    JunboBeanDefinitionParserDelegate(XmlReaderContext readerContext, Environment environment) {
        super(readerContext, environment)

        defaultLazyInit = ConfigServiceManager.instance().getConfigValue("apphost.fastboot")
    }

    @Override
    protected void populateDefaults(DocumentDefaultsDefinition defaults, DocumentDefaultsDefinition parentDefaults, Element root) {
        super.populateDefaults(defaults, parentDefaults, root)

        defaults.lazyInit = defaultLazyInit ?: TRUE_VALUE
    }
}
