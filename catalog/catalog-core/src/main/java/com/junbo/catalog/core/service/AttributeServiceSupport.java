/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.AttributeRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.common.error.AppError;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Attribute service support.
 * @param <T> attribute
 */
public abstract class AttributeServiceSupport<T extends Attribute> {
    protected abstract <E extends AttributeRepository<T>> E getRepo();
    protected abstract List<String> getTypes();
    protected abstract String getEntityType();

    public T getAttribute(Long attributeId) {
        T attribute = getRepo().get(attributeId);
        if (attribute == null) {
            throw AppErrors.INSTANCE.notFound(getEntityType(), Utils.encodeId(attributeId)).exception();
        }
        return attribute;
    }

    public T create(T attribute) {
        validateCreation(attribute);
        Long attributeId = getRepo().create(attribute);
        return getRepo().get(attributeId);
    }

    public T update(Long attributeId, T attribute) {
        T oldAttribute = getRepo().get(attributeId);
        if (oldAttribute==null) {
            throw AppErrors.INSTANCE.notFound(getEntityType(), Utils.encodeId(attributeId)).exception();
        }
        validateUpdate(attribute, oldAttribute);

        getRepo().update(attribute);

        return getRepo().get(attributeId);
    }

    public void deleteAttribute(Long attributeId) {
        T attribute = getRepo().get(attributeId);
        if (attribute == null) {
            throw AppErrors.INSTANCE.notFound(getEntityType(), Utils.encodeId(attributeId)).exception();
        }
        getRepo().delete(attributeId);
    }

    private void validateCreation(T attribute) {
        checkRequestNotNull(attribute);
        List<AppError> errors = new ArrayList<>();
        if (attribute.getResourceAge() != null) {
            errors.add(AppErrors.INSTANCE.unnecessaryField("rev"));
        }

        validateCommon(attribute, errors);
        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateUpdate(T attribute, T oldAttribute) {
        checkRequestNotNull(attribute);
        List<AppError> errors = new ArrayList<>();
        if (!oldAttribute.getId().equals(attribute.getId())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("self.id", attribute.getId(), oldAttribute.getId()));
        }
        if (!oldAttribute.getResourceAge().equals(attribute.getResourceAge())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("rev", attribute.getResourceAge(), oldAttribute.getResourceAge()));
        }

        validateCommon(attribute, errors);
        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateCommon(T attribute, List<AppError> errors) {
        if (attribute.getType() == null) {
            errors.add(AppErrors.INSTANCE.missingField("type"));
        } else if (!getTypes().contains(attribute.getType())) {
            errors.add(AppErrors.INSTANCE.fieldNotCorrect("type", "Value types: " + getTypes()));
        }
        if (CollectionUtils.isEmpty(attribute.getLocales())){
            errors.add(AppErrors.INSTANCE.missingField("locales"));
        } else {
            for (Map.Entry<String, SimpleLocaleProperties> entry : attribute.getLocales().entrySet()) {
                String locale = entry.getKey();
                SimpleLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties==null) {
                    errors.add(AppErrors.INSTANCE.missingField("locales." + locale));
                } else {
                    if (StringUtils.isEmpty(properties.getName())) {
                        errors.add(AppErrors.INSTANCE.missingField("locales." + locale + ".name"));
                    }
                }
            }
        }
    }

    private void checkRequestNotNull(Object entity) {
        if (entity == null) {
            throw AppErrors.INSTANCE.invalidJson("Invalid json.").exception();
        }
    }
}
