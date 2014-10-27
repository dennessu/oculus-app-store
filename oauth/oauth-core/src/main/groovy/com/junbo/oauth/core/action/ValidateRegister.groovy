/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.core.util.ExceptionUtil
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.spec.model.Gender
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
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

    private UserService userService

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String username = parameterMap.getFirst(OAuthParameters.USERNAME)

        if (StringUtils.isEmpty(username)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.parameterRequired('login').error())
        }

        String password = parameterMap.getFirst(OAuthParameters.PASSWORD)

        if (StringUtils.isEmpty(password)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.parameterRequired('password').error())
        }

        String pin = parameterMap.getFirst(OAuthParameters.PIN)

        if (StringUtils.isEmpty(pin)) {
            // ignore if pin is empty.
        } else {
            if (!VALID_PIN_REGEX.matcher(pin).find()) {
                contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldInvalid('pin').error())
            }
        }

        String email = parameterMap.getFirst(OAuthParameters.EMAIL)

        if (StringUtils.isEmpty(email)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldRequired('email').error())
        } else {
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldInvalid('email', email).error())
            }
            contextWrapper.userDefaultEmail = email
        }

        String firstName = parameterMap.getFirst(OAuthParameters.FIRST_NAME)

        if (StringUtils.isEmpty(firstName)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldRequired('first_name').error())
        }

        String lastName = parameterMap.getFirst(OAuthParameters.LAST_NAME)

        if (StringUtils.isEmpty(lastName)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldRequired('last_name').error())
        }

        String genderStr = parameterMap.getFirst(OAuthParameters.GENDER)

        if (!StringUtils.isEmpty(genderStr)) {
            try {
                contextWrapper.gender = Gender.valueOf(genderStr.toUpperCase())
            } catch (IllegalArgumentException e) {
                LOGGER.debug('Error parsing the gender', e)
                contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldInvalid('gender').error())
            }
        }

        String dobStr = parameterMap.getFirst(OAuthParameters.DOB)

        if (!StringUtils.isEmpty(dobStr)) {
            try {
                DateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd', Locale.US)
                contextWrapper.dob = dateFormat.parse(dobStr)
            } catch (ParseException e) {
                LOGGER.debug('Error parsing the date of birth', e)
                contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldInvalid('dob').error())
            }
        }

        if (!contextWrapper.errors.isEmpty()) {
            return Promise.pure(new ActionResult('error'))
        }

        return Promise.pure(new ActionResult('success'))
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }
}

