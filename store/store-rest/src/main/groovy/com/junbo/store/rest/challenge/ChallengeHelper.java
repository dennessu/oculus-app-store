/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.challenge;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.Challenge;
import com.junbo.store.spec.model.ChallengeAnswer;

/**
 * The ChallengeHelper class.
 */
public interface ChallengeHelper {

    Promise<Challenge> checkTosChallenge(UserId userId, String tosTitle, CountryId countryId, ChallengeAnswer challengeAnswer);

    Promise<Challenge> checkPurchasePINChallenge(UserId userId, ChallengeAnswer challengeAnswer);
}
