/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.model.Gender
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Pattern

/**
 * ValidateRegister.
 */
@CompileStatic
class ValidateRegister implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateRegister)

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$', Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_PIN_REGEX = Pattern.compile('\\d{4}')

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String username = parameterMap.getFirst(OAuthParameters.USERNAME)

        if (StringUtils.isEmpty(username)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingUsername().error())
        }

        String password = parameterMap.getFirst(OAuthParameters.PASSWORD)

        if (StringUtils.isEmpty(password)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingPassword().error())
        }

        String pin = parameterMap.getFirst(OAuthParameters.PIN)

        if (StringUtils.isEmpty(pin)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingPin().error())
        } else {
            if (!VALID_PIN_REGEX.matcher(pin).find()) {
                contextWrapper.errors.add(AppExceptions.INSTANCE.invalidPin().error())
            }
        }

        String email = parameterMap.getFirst(OAuthParameters.EMAIL)

        if (StringUtils.isEmpty(email)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingEmail().error())
        } else {
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                contextWrapper.errors.add(AppExceptions.INSTANCE.invalidEmail(email).error())
            }
            contextWrapper.userDefaultEmail = email
        }

        String firstName = parameterMap.getFirst(OAuthParameters.FIRST_NAME)

        if (StringUtils.isEmpty(firstName)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingFirstName().error())
        }

        String lastName = parameterMap.getFirst(OAuthParameters.LAST_NAME)

        if (StringUtils.isEmpty(lastName)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingLastName().error())
        }

        String genderStr = parameterMap.getFirst(OAuthParameters.GENDER)

        if (!StringUtils.isEmpty(genderStr)) {
            try {
                contextWrapper.gender = Gender.valueOf(genderStr.toUpperCase())
            } catch (IllegalArgumentException e) {
                LOGGER.debug('Error parsing the gender', e)
                contextWrapper.errors.add(AppExceptions.INSTANCE.invalidGender().error())
            }
        }

        String dobStr = parameterMap.getFirst(OAuthParameters.DOB)

        if (!StringUtils.isEmpty(dobStr)) {
            try {
                DateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd', Locale.US)
                contextWrapper.dob = dateFormat.parse(dobStr)
            } catch (ParseException e) {
                LOGGER.debug('Error parsing the date of birth', e)
                contextWrapper.errors.add(AppExceptions.INSTANCE.invalidDob().error())
            }
        }

        if (!contextWrapper.errors.isEmpty()) {
            return Promise.pure(new ActionResult('error'))
        }

        return Promise.pure(new ActionResult('success'))
    }
}

