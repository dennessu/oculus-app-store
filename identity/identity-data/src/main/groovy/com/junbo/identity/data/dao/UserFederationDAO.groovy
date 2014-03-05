/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.spec.model.user.UserFederation

/**
 * User Federation DAO is used to fetch/update/delete/get user
 * federation data(such as google account, facebook account) from the database
 */
interface UserFederationDAO {

    UserFederation save(UserFederation entity)

    UserFederation update(UserFederation entity)

    UserFederation get(Long id)

    List<UserFederation> findByUser(Long id, String type)

    void delete(Long id)
}
