/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.spec.model.user.UserTosAcceptance
/**
 * User tos acceptance DAO is used to fetch/update/delete/get user tos Acceptance(eg, legal) from the database
 */
interface UserTosAcceptanceDAO {

    UserTosAcceptance save(UserTosAcceptance entity)

    UserTosAcceptance update(UserTosAcceptance entity)

    UserTosAcceptance get(Long id)

    List<UserTosAcceptance> findByUserId(Long id, String tos)

    void delete(Long id)
}
