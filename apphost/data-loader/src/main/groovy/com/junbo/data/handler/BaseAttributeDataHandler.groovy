package com.junbo.data.handler

import com.junbo.data.model.AttributeData
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource
/**
 * The BaseAttributeDataHandler class.
 */
@CompileStatic
abstract class BaseAttributeDataHandler extends BaseDataHandler {

    protected String nameLocale = 'en_US'

    private String attributeType

    @Required
    void setAttributeType(String attributeType) {
        this.attributeType = attributeType
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        Map<String, Resource> nameToResourceMap = new HashMap<>()
        Map<String, AttributeData> attributeMap = new HashMap<>()
        List<String> resultNames = []
        resources.each { Resource resource ->
            String content = IOUtils.toString(resource.URI, "UTF-8")
            try {
                AttributeData attributeData = transcoder.decode(new TypeReference<AttributeData>() {}, content) as AttributeData
                String name = attributeData.locales[nameLocale].name
                assert !StringUtils.isEmpty(name), 'attribute name could not be empty, type=${attributeType}'
                assert !nameToResourceMap.containsKey(name), "duplicate names in attribute, type=${attributeType}"
                nameToResourceMap.put(name, resource)
                attributeMap.put(name, attributeData)
            } catch (Exception e) {
                logger.error("Error parsing attribute $content", e)
                exit()
            }
            attributeMap.keySet().each { String name ->
                resolve(name, attributeMap, resultNames)
            }
        }

        return resultNames.collect { String name ->
            nameToResourceMap[name]
        }.toArray(new Resource[0])
    }

    @Override
    void handle(String content) {
        AttributeData attributeData
        try {
            attributeData = transcoder.decode(new TypeReference<AttributeData>() {}, content) as AttributeData
        } catch (Exception e) {
            logger.error("Error parsing offer attribute\n $content", e)
            exit()
        }

        String parentId = null
        if (!StringUtils.isEmpty(attributeData.parent)) {
            parentId = getIdByName(attributeData.parent)
            assert parentId != null, "attribute not found with name=${attributeData.parent}, type=${attributeType}"
        }
        save(attributeData, parentId)
    }

    private static void resolve(String name, Map<String, AttributeData> attributeMap, List<String> results) {
        List<String> names = []
        while (true) {
            if (StringUtils.isEmpty(name) || results.contains(name)) {
                break
            }
            names << name
            name = attributeMap[name].parent
        }
        results.addAll(names.reverse())
    }

    protected abstract void save(AttributeData attributeData, String parentId)

    protected abstract String getIdByName(String name)

}
