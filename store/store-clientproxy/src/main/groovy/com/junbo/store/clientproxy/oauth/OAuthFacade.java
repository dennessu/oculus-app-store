/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.oauth;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.ViewModel;

/**
 * The OAuthFacade class.
 */
public interface OAuthFacade {

    Promise<ViewModel> verifyEmail(String evc, String locale);

    Promise sendVerifyEmail(String locale, String country, UserId userId, UserPersonalInfoId targetMail);

    Promise sendWelcomeEmail(String locale, String country, UserId userId);
}
