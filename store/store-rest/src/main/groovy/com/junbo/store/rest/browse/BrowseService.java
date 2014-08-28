/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.browse;

import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.browse.document.Item;

/**
 * The BrowseService class.
 */
public interface BrowseService {

    Promise<Item> getItem(String itemId, BrowseContext browseContext);

    Promise<TocResponse> getTocResponse(BrowseContext browseContext);

    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, BrowseContext browseContext);

    Promise<ListResponse> getList(ListRequest request, BrowseContext browseContext);

    Promise<LibraryResponse> getLibrary(BrowseContext browseContext);
}
