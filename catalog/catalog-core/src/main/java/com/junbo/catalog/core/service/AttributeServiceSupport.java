/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.google.common.base.Joiner;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.AttributeRepository;
import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(AttributeServiceSupport.class);
    protected abstract <E extends AttributeRepository<T>> E getRepo();
    protected abstract List<String> getTypes();
    protected abstract String getEntityType();

    public T getAttribute(String attributeId) {
        T attribute = getRepo().get(attributeId);
        if (attribute == null) {
            AppErrorException exception = AppCommonErrors.INSTANCE.resourceNotFound(getEntityType(), attributeId).exception();
            LOGGER.error("Not found " + getEntityType(), exception);
            throw exception;
        }
        return attribute;
    }

    public T create(T attribute) {
        validateCreation(attribute);
        return getRepo().create(attribute);
    }

    public T update(String attributeId, T attribute) {
        T oldAttribute = getRepo().get(attributeId);
        if (oldAttribute == null) {
            AppErrorException exception = AppCommonErrors.INSTANCE.resourceNotFound(getEntityType(), attributeId).exception();
            LOGGER.error("Error updating " + getEntityType(), exception);
            throw exception;
        }
        validateUpdate(attribute, oldAttribute);
        return getRepo().update(attribute, oldAttribute);
    }

    public void deleteAttribute(String attributeId) {
        T attribute = getRepo().get(attributeId);
        if (attribute == null) {
            AppErrorException exception = AppCommonErrors.INSTANCE.resourceNotFound(getEntityType(), attributeId).exception();
            LOGGER.error("Not found " + getEntityType(), exception);
            throw exception;
        }
        getRepo().delete(attributeId);
    }

    private void validateCreation(T attribute) {
        checkRequestNotNull(attribute);
        List<AppError> errors = new ArrayList<>();
        if (attribute.getId() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("self"));
        }
        if (attribute.getRev() != null) {
            errors.add(AppCommonErrors.INSTANCE.fieldMustBeNull("rev"));
        }

        validateCommon(attribute, errors);
        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error creating " + getEntityType(), exception);
            throw exception;
        }
    }

    private void validateUpdate(T attribute, T oldAttribute) {
        checkRequestNotNull(attribute);
        List<AppError> errors = new ArrayList<>();
        if (!oldAttribute.getType().equals(attribute.getType())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("type", attribute.getType(), oldAttribute.getType()));
        }
        if (!oldAttribute.getId().equals(attribute.getId())) {
            errors.add(AppCommonErrors.INSTANCE.fieldNotWritable("self.id", attribute.getId(), oldAttribute.getId()));
        }
        if (!oldAttribute.getRev().equals(attribute.getRev())) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("rev"));
        }
        validateCommon(attribute, errors);
        if (!errors.isEmpty()) {
            AppErrorException exception = Utils.invalidFields(errors).exception();
            LOGGER.error("Error updating " + getEntityType(), exception);
            throw exception;
        }
    }

    private void validateCommon(T attribute, List<AppError> errors) {
        if (attribute.getType() == null) {
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("type"));
        } else if (!getTypes().contains(attribute.getType())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalidEnum("type", Joiner.on(", ").join(getTypes())));
        }
        if (CollectionUtils.isEmpty(attribute.getLocales())){
            errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales"));
        } else {
            for (Map.Entry<String, SimpleLocaleProperties> entry : attribute.getLocales().entrySet()) {
                String locale = entry.getKey();
                SimpleLocaleProperties properties = entry.getValue();
                // TODO: check locale is a valid locale
                if (properties==null) {
                    errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale));
                } else {
                    if (StringUtils.isEmpty(properties.getName())) {
                        errors.add(AppCommonErrors.INSTANCE.fieldRequired("locales." + locale + ".name"));
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(attribute.getFutureExpansion())) {
            errors.add(AppCommonErrors.INSTANCE.fieldInvalid("futureExpansion", "you should leave this property empty"));
        }
    }

    private void checkRequestNotNull(Object entity) {
        if (entity == null) {
            AppErrorException exception = AppCommonErrors.INSTANCE.invalidJson("cause", "Json is null").exception();
            LOGGER.error("Invalid json for " + getEntityType(), exception);
            throw exception;
        }
    }
}
