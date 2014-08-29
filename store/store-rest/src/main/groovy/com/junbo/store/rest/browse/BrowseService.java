/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.browse;

import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.browse.document.Item;

/**
 * The BrowseService class.
 */
public interface BrowseService {

    Promise<Item> getItem(String itemId, ApiContext apiContext);

    Promise<TocResponse> getToc(ApiContext apiContext);

    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext);

    Promise<ListResponse> getList(ListRequest request, ApiContext apiContext);

    Promise<LibraryResponse> getLibrary(ApiContext apiContext);
}
