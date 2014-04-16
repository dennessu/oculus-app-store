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

/**
 * ValidateRegister.
 */
@CompileStatic
class ValidateRegister implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateRegister)

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String username = parameterMap.getFirst(OAuthParameters.USERNAME)

        if (StringUtils.isEmpty(username)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingUsername().error())
        }

        String nickname = parameterMap.getFirst(OAuthParameters.NICK_NAME)
        if (StringUtils.isEmpty(nickname)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingNickName().error())
        }

        String password = parameterMap.getFirst(OAuthParameters.PASSWORD)

        if (StringUtils.isEmpty(password)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingPassword().error())
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

        if (StringUtils.isEmpty(genderStr)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingGender().error())
        } else {
            try {
                contextWrapper.gender = Gender.valueOf(genderStr.toLowerCase())
            } catch (IllegalArgumentException e) {
                LOGGER.debug('Error parsing the gender', e)
                contextWrapper.errors.add(AppExceptions.INSTANCE.invalidGender().error())
            }
        }

        String dobStr = parameterMap.getFirst(OAuthParameters.DOB)

        if (StringUtils.isEmpty(dobStr)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingDob().error())
        } else {
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

