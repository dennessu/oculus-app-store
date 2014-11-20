/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.factory

import com.junbo.oom.processor.ProcessingException
import com.junbo.oom.processor.model.PropertyModel
import com.junbo.oom.processor.model.TypeModel
import groovy.transform.CompileStatic

import javax.annotation.processing.ProcessingEnvironment
/**
 * Java doc.
 */
@CompileStatic
class PropertyFactoryImpl implements PropertyFactory {

    private final ProcessingEnvironment processingEnv

    private final TypeFactory typeFactory

    PropertyFactoryImpl(ProcessingEnvironment processingEnv, TypeFactory typeFactory) {
        this.processingEnv = processingEnv
        this.typeFactory = typeFactory
    }

    @Override
    PropertyModel getProperty(TypeModel owner, String propertyName) {

        def getters = PropertyUtil.getGetters(owner, processingEnv)
        def setters = PropertyUtil.getSetters(owner, processingEnv)

        def getter = getters[propertyName]
        def setter = setters[propertyName]

        try {
            TypeModel returnType
            if (getter != null) {
                returnType = typeFactory.getType(PropertyUtil.getGetterType(owner, getter, processingEnv))
            }
            else if (setter != null) {
                returnType = typeFactory.getType(PropertyUtil.getSetterType(owner, setter, processingEnv))
            }
            else {
                throw new ProcessingException("Unknown property $propertyName in type $owner.")
            }

            return new PropertyModel(
                    owner:owner,
                    name:propertyName,
                    type:returnType,
                    getterString:getter?.simpleName?.toString(),
                    setterString:setter?.simpleName?.toString()
            )
        } catch (ProcessingException e) {
            throw new ProcessingException("Error processing property $propertyName for class $owner.name", e)
        }

    }
}
