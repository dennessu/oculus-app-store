package com.goshop.catalog.db.converter

import com.goshop.catalog.db.entity.EntityId
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.data.mapping.model.MappingException
import org.springframework.stereotype.Component

import java.lang.reflect.Constructor

/**
 * Created by kevingu on 11/21/13.
 */

@CompileStatic
@Component
class EntityIdConverter implements GenericConverter {

    @Override
    Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return [new GenericConverter.ConvertiblePair(ObjectId, EntityId),
                new GenericConverter.ConvertiblePair(EntityId, ObjectId)] as Set
    }

    @Override
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> targetClass = targetType.getType()

        if (source == null) {
            return null
        }

        if (source instanceof ObjectId) {
            try {
                Constructor constructor = targetClass.getConstructor(ObjectId)
                return constructor.newInstance(source)
            } catch (Throwable e) {
                throw new MappingException("Unable to create an EntityId of type " + targetClass, e)
            }
        }

        if (source instanceof EntityId) {
            return ((EntityId) source).toObjectId()
        }

        throw new MappingException("Unknown source type " + sourceType)
    }
}
