/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.UserInfo;

/**
 * user info facade.
 */
public interface UserInfoFacade {
    Promise<UserInfo> getUserInfo(Long userId);
    Promise<Void> updateDefaultPI(Long userId, Long existingPiId, Long newPiId);
}
