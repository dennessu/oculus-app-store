/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.validator;

import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.browse.AddReviewRequest;

/**
 * The ReviewValidator class.
 */
public interface ReviewValidator {

    Promise validateAddReview(AddReviewRequest request, ApiContext apiContext);

}
