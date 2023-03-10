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
import com.junbo.store.spec.model.browse.document.Item;
import com.junbo.store.spec.model.iap.HostItemInfo;

import java.util.List;
import java.util.Set;

/**
 * The BrowseService class.
 */
public interface BrowseService {

    Promise<DetailsResponse> getItem(ItemId itemId, boolean includeDetails, ApiContext apiContext);

    Promise<TocResponse> getToc(ApiContext apiContext);

    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext);

    Promise<ListResponse> getList(ListRequest request, ApiContext apiContext);

    Promise<InitialDownloadItemsResponse> getInitialDownloadItems(Integer version, ApiContext apiContext);

    Promise<LibraryResponse> getLibrary(boolean isIAP, HostItemInfo hostItemInfo, ApiContext apiContext);

    Promise<DeliveryResponse> getDelivery(DeliveryRequest request, ApiContext apiContext);

    Promise<ReviewsResponse> getReviews(ReviewsRequest request, ApiContext apiContext);

    Promise<AddReviewResponse> addReview(AddReviewRequest request, ApiContext apiContext);

    Promise<List<Item>> getIAPItems(HostItemInfo hostItemInfo, Set<String> skus, ApiContext apiContext);
}
