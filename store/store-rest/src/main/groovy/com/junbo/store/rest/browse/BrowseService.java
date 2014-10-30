/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.browse;

import com.junbo.common.id.ItemId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.browse.*;

/**
 * The BrowseService class.
 */
public interface BrowseService {

    Promise<DetailsResponse> getItem(ItemId itemId, boolean includeDetails, ApiContext apiContext);

    Promise<TocResponse> getToc(ApiContext apiContext);

    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext);

    Promise<ListResponse> getList(ListRequest request, ApiContext apiContext);

    Promise<LibraryResponse> getLibrary(boolean isIAP, ItemId hostItemId, ApiContext apiContext);

    Promise<DeliveryResponse> getDelivery(DeliveryRequest request, ApiContext apiContext);

    Promise<ReviewsResponse> getReviews(ReviewsRequest request, ApiContext apiContext);

    Promise<AddReviewResponse> addReview(AddReviewRequest request, ApiContext apiContext);
}
