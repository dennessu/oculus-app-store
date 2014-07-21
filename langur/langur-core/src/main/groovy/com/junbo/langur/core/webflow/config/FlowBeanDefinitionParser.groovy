package com.junbo.langur.core.webflow.config

import com.junbo.langur.core.webflow.definition.ActionStateDef
import com.junbo.langur.core.webflow.definition.EndStateDef
import com.junbo.langur.core.webflow.definition.FlowDef
import com.junbo.langur.core.webflow.definition.SubflowStateDef
import com.junbo.langur.core.webflow.definition.TransitionDef
import com.junbo.langur.core.webflow.definition.ViewStateDef
import groovy.transform.CompileStatic
import org.springframework.beans.factory.BeanDefinitionStoreException
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.parsing.BeanComponentDefinition
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils
import org.springframework.beans.factory.support.ManagedList
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
import org.springframework.beans.factory.xml.ParserContext
import org.w3c.dom.Element

/**
 * Created by Shenhua on 3/1/14.
 */
@CompileStatic
class FlowBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    private static final String NAMESPACE = 'http://www.junbo.com/schema/webflow'

    @Override
    protected Class<?> getBeanClass(Element element) {
        return FlowDef
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
            throws BeanDefinitionStoreException {
        return 'flow_' + super.resolveId(element, definition, parserContext)
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyValue('id', element.getAttribute('id'))

        ManagedList<RuntimeBeanReference> states = []

        def childNodes = element.childNodes
        for (int i = 0; i < childNodes.length; i++) {
            def childNode = childNodes.item(i)
            if (childNode instanceof Element) {
                def childElement = (Element) childNode
                if (childElement.localName == 'action-state') {
                    states.add(parseActionState(childElement, parserContext))
                } else if (childElement.localName == 'view-state') {
                    states.add(parseViewState(childElement, parserContext))
                } else if (childElement.localName == 'subflow-state') {
                    states.add(parseSubflowState(childElement, parserContext))
                } else if (childElement.localName == 'end-state') {
                    states.add(parseEndState(childElement, parserContext))
                } else {
                    parserContext.readerContext.error("unknown element ${childElement.tagName}", childElement)
                }
            }
        }

        builder.addPropertyValue('states', states)

        parseElement(builder, element, 'on-start', 'startActions')

        parseElement(builder, element, 'on-end', 'endActions')
    }

    private static RuntimeBeanReference parseActionState(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ActionStateDef)
        builder.rawBeanDefinition.source = parserContext.extractSource(element)

        builder.addPropertyValue('id', element.getAttribute('id'))

        parseElement(builder, element, 'on-entry', 'entryActions')

        builder.addPropertyValue('actions', parseActions(element))

        builder.addPropertyValue('transitions', parseTransitions(element, parserContext))

        parseElement(builder, element, 'on-exit', 'exitActions')

        return beanReference(builder, parserContext)
    }

    private static RuntimeBeanReference parseViewState(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ViewStateDef)
        builder.rawBeanDefinition.source = parserContext.extractSource(element)

        builder.addPropertyValue('id', element.getAttribute('id'))

        parseElement(builder, element, 'on-entry', 'entryActions')

        parseElement(builder, element, 'on-render', 'renderActions')

        builder.addPropertyValue('view', element.getAttribute('view'))

        builder.addPropertyValue('model', element.getAttribute('model'))

        builder.addPropertyValue('transitions', parseTransitions(element, parserContext))

        parseElement(builder, element, 'on-exit', 'exitActions')

        return beanReference(builder, parserContext)
    }

    private static RuntimeBeanReference parseSubflowState(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SubflowStateDef)
        builder.rawBeanDefinition.source = parserContext.extractSource(element)

        builder.addPropertyValue('id', element.getAttribute('id'))

        parseElement(builder, element, 'on-entry', 'entryActions')

        builder.addPropertyValue('subflow', element.getAttribute('subflow'))

        builder.addPropertyValue('transitions', parseTransitions(element, parserContext))

        parseElement(builder, element, 'on-exit', 'exitActions')

        return beanReference(builder, parserContext)
    }

    private static RuntimeBeanReference parseEndState(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(EndStateDef)
        builder.rawBeanDefinition.source = parserContext.extractSource(element)

        builder.addPropertyValue('id', element.getAttribute('id'))

        parseElement(builder, element, 'on-entry', 'entryActions')

        builder.addPropertyValue('view', element.getAttribute('view'))

        builder.addPropertyValue('model', element.getAttribute('model'))

        return beanReference(builder, parserContext)
    }

    private static List<RuntimeBeanReference> parseActions(Element element) {

        ManagedList<RuntimeBeanReference> actions = []

        def actionElements = element.getElementsByTagNameNS(NAMESPACE, 'action')
        for (int i = 0; i < actionElements.length; i++) {
            def actionElement = (Element) actionElements.item(i)
            def ref = actionElement.getAttribute('ref')

            actions.add(new RuntimeBeanReference(ref))
        }

        return actions
    }

    private static List<RuntimeBeanReference> parseTransitions(Element element, ParserContext parserContext) {

        ManagedList<RuntimeBeanReference> transitions = []

        def transitionElements = element.getElementsByTagNameNS(NAMESPACE, 'transition')
        for (int i = 0; i < transitionElements.length; i++) {
            def transitionElement = (Element) transitionElements.item(i)
            def on = transitionElement.getAttribute('on')
            def to = transitionElement.getAttribute('to')

            def actions = parseActions(transitionElement)

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(TransitionDef)
            builder.rawBeanDefinition.source = parserContext.extractSource(transitionElement)

            builder.addPropertyValue('on', on)
            builder.addPropertyValue('to', to)
            builder.addPropertyValue('actions', actions)

            transitions.add(beanReference(builder, parserContext))
        }

        return transitions
    }

    private static RuntimeBeanReference beanReference(BeanDefinitionBuilder builder, ParserContext parserContext) {

        def beanDefinition = builder.beanDefinition
        def generatedName = BeanDefinitionReaderUtils.generateBeanName(
                beanDefinition, parserContext.readerContext.registry, true)

        parserContext.registerBeanComponent(new BeanComponentDefinition(beanDefinition, generatedName))
        return new RuntimeBeanReference(generatedName)
    }

    private static void parseElement(BeanDefinitionBuilder builder, Element element, String tagName,
                                     String propertyName) {
        def childElements = element.getElementsByTagNameNS(NAMESPACE, tagName)
        if (childElements.length != 0) {
            def childElement = (Element) childElements.item(0)
            builder.addPropertyValue(propertyName, parseActions(childElement))
        } else {
            builder.addPropertyValue(propertyName, [])
        }
    }
}
