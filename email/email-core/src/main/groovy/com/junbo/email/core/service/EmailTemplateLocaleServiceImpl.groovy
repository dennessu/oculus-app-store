/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.email.core.service

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.email.core.EmailTemplateLocaleService
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Required

/**
 * Created by dell on 1/16/2015.
 */
public class EmailTemplateLocaleServiceImpl implements EmailTemplateLocaleService {

    private Map<String, List<String>> localeMap

    private UserResource userResource

    private String defaultLocale

    @Override
    public Promise<String> getEmailTemplateLocale(String locale, UserId userId) {
        if (userId == null) {
            return Promise.pure(locale)
        }

        return userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('userId', 'user does not exits').exception()
            }

            if (user.preferredLocale != null) {
                return Promise.pure(user.preferredLocale.toString())
            }

            if (JunboHttpContext.acceptableLanguage != null) {
                return Promise.pure(JunboHttpContext.acceptableLanguage)
            }

            if (!StringUtils.isEmpty(locale)) {
                return Promise.pure(locale)
            }

            return Promise.pure(defaultLocale)
        }.then { String raw ->
            String mappedLocale = null;
            localeMap.entrySet().each { Map.Entry<String, List<String>> entry ->
                if (StringUtils.isEmpty(mappedLocale)) {
                    String key = entry.key
                    List<String> value = entry.value

                    if (value.contains(raw)) {
                        mappedLocale = key
                    }
                }
            }

            return StringUtils.isEmpty(mappedLocale) ? Promise.pure(raw) : Promise.pure(mappedLocale)
        }
    }

    @Required
    public void setLocaleMap(Map<String, List<String>> localeMap) {
        this.localeMap = localeMap;
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale
    }
}
