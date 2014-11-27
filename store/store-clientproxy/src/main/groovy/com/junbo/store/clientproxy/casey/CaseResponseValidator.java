/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.casey;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ValidationProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The CaseResponseValidator class.
 */
@Component("store.caseResponseValidator")
public class CaseResponseValidator {

    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = javax.validation.Validation.byDefaultProvider()
                .providerResolver(new HibernateValidationProviderResolver())
                .configure()
                .buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    static class HibernateValidationProviderResolver implements ValidationProviderResolver {

        @Override
        public List<ValidationProvider<?>> getValidationProviders() {
            List<ValidationProvider<?>> result = new ArrayList<>();
            ValidationProvider<?> provider = new HibernateValidator();
            result.add(provider);
            return result;
        }
    }

    public String validate(Object obj) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
        if (CollectionUtils.isEmpty(constraintViolations)) {
            return null;
        }

        List<String> message = new ArrayList<>();
        for (ConstraintViolation c : constraintViolations) {
            message.add(c.getMessage());
        }
        return "[" + StringUtils.join(message, ",") + "]";
    }
}
