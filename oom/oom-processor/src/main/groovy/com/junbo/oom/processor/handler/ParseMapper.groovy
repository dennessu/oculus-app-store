/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.handler

import com.junbo.oom.core.MappingContext
import com.junbo.oom.core.builtin.BuiltinMapper
import com.junbo.oom.processor.MapperPrism
import com.junbo.oom.processor.MappingsPrism
import com.junbo.oom.processor.ProcessingException
import com.junbo.oom.processor.model.ParameterModel
import com.junbo.oom.processor.source.MappingMethodInfo
import com.junbo.oom.processor.source.MappingPropertyInfo
import com.junbo.oom.processor.util.Constants
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter

/**
 * Java doc.
 */
@CompileStatic
class ParseMapper implements MapperHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {

        handlerContext.mappingMethods = ElementFilter.methodsIn(mapperType.enclosedElements).collect {
            ExecutableElement method -> getMappingMethod(method, handlerContext)
        }

        mergeProperties(handlerContext)

        handlerContext.mapperRefs = getMapperReferences(mapperType, handlerContext)

        handlerContext.mapperRefs.each { TypeElement usedMapper ->
            handlerContext.mappingMethods.addAll(ElementFilter.methodsIn(usedMapper.enclosedElements).collect {
                ExecutableElement method -> getMappingMethodReference(usedMapper, method, handlerContext)
            })
        }
    }

    private static void mergeProperties(HandlerContext handlerContext) {

        def propertiesByTypePair = new HashMap<Tuple, List<MappingPropertyInfo>>()

        handlerContext.mappingMethods.each { MappingMethodInfo method ->
            def typePair = new Tuple(method.sourceParameter.type, method.returnType)
            def properties = propertiesByTypePair[typePair]

            if (properties != null) {
                // skip if already processed for the given sourceType and targetType
                return
            }

            properties = new ArrayList<MappingPropertyInfo>()

            handlerContext.mappingMethods.findAll { MappingMethodInfo toMatch ->
                toMatch.sourceParameter.type == method.sourceParameter.type &&
                        toMatch.returnType == method.returnType
            }*.properties.flatten().each { MappingPropertyInfo property ->
                properties.add(property)
            }

            if (method.sourceParameter.type != method.returnType) {
                handlerContext.mappingMethods.findAll { MappingMethodInfo toMatch ->
                    toMatch.returnType == method.sourceParameter.type &&
                            toMatch.sourceParameter.type == method.returnType
                }*.properties.flatten().each { MappingPropertyInfo property ->
                    if (property.bidirectional) {
                        properties.add(new MappingPropertyInfo(
                                source: property.target,
                                target: property.source,
                                excluded: property.excluded,
                                explicitMethodName: property.explicitMethodName
                        ))
                    }
                }
            }

            propertiesByTypePair[typePair] = properties
        }

        handlerContext.mappingMethods.each { MappingMethodInfo method ->
            def typePair = new Tuple(method.sourceParameter.type, method.returnType)
            def properties = propertiesByTypePair[typePair]

            if (properties != null) {
                method.properties = (List<MappingPropertyInfo>) properties
            }
        }
    }

    private static List<TypeElement> getMapperReferences(TypeElement mapperType, HandlerContext handlerContext) {

        def builtinMapperRefs = [
                BuiltinMapper
        ].collect { Class<?> type ->
            (TypeMirror) handlerContext.processingEnv.typeUtils.getDeclaredType(
                    handlerContext.processingEnv.elementUtils.getTypeElement(type.canonicalName))
        }

        def mapperPrism = MapperPrism.getInstanceOn(mapperType)
        assert mapperPrism != null: 'mapperPrism is null'

        return (builtinMapperRefs + mapperPrism.uses()).collect { TypeMirror typeMirror ->
            (TypeElement) ((DeclaredType) typeMirror).asElement()
        }
    }

    final int sequenceNumber = 10

    private static MappingMethodInfo getMappingMethod(ExecutableElement method, HandlerContext handlerContext) {

        def parameters = parseParameters(method, handlerContext)
        ParameterModel sourceParameter = parameters[0]
        ParameterModel alternativeSourceParameter = parameters[1]
        ParameterModel contextParameter = parameters[2]

        def returnType = handlerContext.typeFactory.getType(method.returnType)

        def properties = []

        MappingsPrism mappingsPrism = MappingsPrism.getInstanceOn(method)
        if (mappingsPrism != null) {
            properties.addAll(mappingsPrism.value().collect { MappingsPrism.Mapping mapping ->
                new MappingPropertyInfo(
                        source: mapping.source(),
                        target: mapping.target() == '' ? mapping.source() : mapping.target(),
                        excluded: mapping.excluded(),
                        bidirectional: mapping.bidirectional(),
                        explicitMethodName: getExplicitMethodName(mapping.explicitMethod())
                )
            })
        }

        return new MappingMethodInfo(
                name: method.simpleName.toString(),
                sourceParameter: sourceParameter,
                alternativeSourceParameter: alternativeSourceParameter,
                contextParameter: contextParameter,
                returnType: returnType,
                properties: properties
        )
    }

    private static MappingMethodInfo getMappingMethodReference(
            TypeElement type, ExecutableElement method, HandlerContext handlerContext) {

        def parameters = parseParameters(method, handlerContext)

        ParameterModel sourceParameter = parameters[0]
        ParameterModel alternativeSourceParameter = parameters[1]
        ParameterModel contextParameter = parameters[2]

        def returnType = handlerContext.typeFactory.getType(method.returnType)

        def declaringMapper = handlerContext.typeFactory.getType(type.asType())

        return new MappingMethodInfo(
                declaringMapper: declaringMapper,
                name: method.simpleName.toString(),
                sourceParameter: sourceParameter,
                alternativeSourceParameter: alternativeSourceParameter,
                contextParameter: contextParameter,
                returnType: returnType
        )
    }

    private static List<ParameterModel> parseParameters(ExecutableElement method, HandlerContext handlerContext) {

        if (!(method.parameters.size() in 1..3)) {
            throw new ProcessingException('Mapping Method should have 1..3 parameters.', method)
        }

        def parameters = method.parameters.collect { VariableElement parameter ->
            new ParameterModel(
                    name: parameter.simpleName.toString(),
                    type: handlerContext.typeFactory.getType(parameter.asType())
            )
        }

        def sourceParameter = parameters[0]
        ParameterModel alternativeSourceParameter = null
        ParameterModel contextParameter = null

        if (parameters.size() == 2) {

            if (parameters[1].type.qualifiedName == MappingContext.canonicalName) {
                contextParameter = parameters[1]
            } else if (parameters[1].type.qualifiedName == sourceParameter.type.qualifiedName) {
                alternativeSourceParameter = parameters[1]
            } else {
                throw new ProcessingException('2nd parameter of Mapping Method should be of ' +
                        MappingContext.canonicalName + ' type or ' + sourceParameter.type.qualifiedName + 'type.', method)
            }

        } else if (parameters.size() == 3) {
            alternativeSourceParameter = parameters[1]

            if (alternativeSourceParameter.type.qualifiedName != sourceParameter.type.qualifiedName) {
                throw new ProcessingException('2nd parameter of Mapping Method should be of '
                        + sourceParameter.type.qualifiedName + ' type.', method)
            }

            contextParameter = parameters[2]

            if (contextParameter.type.qualifiedName != MappingContext.canonicalName) {
                throw new ProcessingException('3rd parameter of Mapping Method should be of MappingContext type.', method)
            }
        }

        return [sourceParameter, alternativeSourceParameter, contextParameter]
    }

    private static String getExplicitMethodName(String explicitMethodName) {
        if (StringUtils.isEmpty(explicitMethodName)) {
            return explicitMethodName
        }
        return Constants.EXPLICIT_METHOD_NAME_PREFIX + explicitMethodName
    }
}