/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserTosAgreementEntity
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import groovy.transform.CompileStatic

/**
 * User tos acceptance DAO is used to fetch/update/delete/get user tos Acceptance(eg, legal) from the database
 */
@CompileStatic
interface UserTosDAO {

    UserTosAgreementEntity save(UserTosAgreementEntity entity)

    UserTosAgreementEntity update(UserTosAgreementEntity entity)

    UserTosAgreementEntity get(Long id)

    List<UserTosAgreementEntity> search(Long userId, UserTosAgreementListOptions getOption)

    void delete(Long id)
}
