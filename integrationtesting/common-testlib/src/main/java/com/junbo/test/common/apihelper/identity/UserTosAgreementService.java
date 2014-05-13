/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity;

import com.junbo.identity.spec.v1.model.UserTosAgreement;
import com.junbo.common.id.UserTosAgreementId;
import com.junbo.common.model.Results;
import com.junbo.common.id.UserId;
import com.junbo.common.id.TosId;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * time 5/13/2014
 * Interface for User TOS Agreement related API, including get/post/put user, update password and so on.
 */
public interface UserTosAgreementService {

    //Create a User TOS Agreement
    UserTosAgreement postUserTosAgreement(UserId userId, UserTosAgreement uta) throws Exception;
    UserTosAgreement postUserTosAgreement(UserId userId, UserTosAgreement uta, int expectedResponseCode) throws Exception;

    //Get a User TOS Agreement info
    UserTosAgreement getUserTosAgreement(UserId userId, UserTosAgreementId utaId) throws Exception;
    UserTosAgreement getUserTosAgreement(UserId userId, UserTosAgreementId utaId, int expectedResponseCode) throws Exception;

    //Get User TOS Agreement info list
    Results<UserTosAgreement> getUserTosAgreementList(UserId userId, TosId tosId) throws Exception;
    Results<UserTosAgreement> getUserTosAgreementList(UserId userId, TosId tosId,
                                                      int expectedResponseCode) throws Exception;

    Results<UserTosAgreement> getUserTosAgreementList(UserId userId, HashMap<String, List<String>> getOptions) throws Exception;
    Results<UserTosAgreement> getUserTosAgreementList(UserId userId, HashMap<String, List<String>> getOptions,
                                                      int expectedResponseCode) throws Exception;

    //Update a User TOS Agreement
    UserTosAgreement putUserTosAgreement(UserId userId, UserTosAgreementId utaId, UserTosAgreement uta) throws Exception;
    UserTosAgreement putUserTosAgreement(UserId userId, UserTosAgreementId utaId, UserTosAgreement uta, int expectedResponseCode)
            throws Exception;

    //Delete a User TOS Agreement
    void deleteUserTosAgreement(UserId userId, UserTosAgreementId utaId) throws Exception;
    void deleteUserTosAgreement(UserId userId, UserTosAgreementId utaId, int expectedResponseCode) throws Exception;

}
