/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.casey;

import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.browse.AddReviewRequest;
import com.junbo.store.spec.model.browse.Images;
import com.junbo.store.spec.model.browse.ReviewsResponse;
import com.junbo.store.spec.model.browse.document.AggregatedRatings;
import com.junbo.store.spec.model.browse.document.Item;
import com.junbo.store.spec.model.browse.document.Review;
import com.junbo.store.spec.model.browse.document.SectionInfoNode;
import com.junbo.store.spec.model.external.casey.CaseyResults;
import com.junbo.store.spec.model.external.casey.cms.CmsPage;

import java.util.Map;

/**
 * The CaseyFacade interface.
 */
public interface CaseyFacade {

    Promise<CaseyResults<Item>> search(SectionInfoNode sectionInfoNode, String cursor, Integer count, Images.BuildType imageBuildType, ApiContext apiContext);

    Promise<CaseyResults<Item>> search(String cmsPage, String cmsSlot, String cursor, Integer count, Images.BuildType imageBuildType, ApiContext apiContext);
;
    Promise<CaseyResults<Item>> search(ItemId itemId, Images.BuildType imageBuildType, ApiContext apiContext);

    Promise<Map<String, AggregatedRatings>> getAggregatedRatings(ItemId itemId, ApiContext apiContext);

    Promise<ReviewsResponse> getReviews(String itemId, UserId userId, String cursor, Integer count);

    Promise<CmsPage> getCmsPage(String path, String label, String country, String locale);

    Promise<Review> addReview(AddReviewRequest request, ApiContext apiContext);
}
