/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.browse;

import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.browse.ListRequest;
import com.junbo.store.spec.model.browse.ListResponse;
import com.junbo.store.spec.model.browse.SectionLayoutRequest;
import com.junbo.store.spec.model.browse.SectionLayoutResponse;
import com.junbo.store.spec.model.browse.document.SectionInfoNode;

import java.util.List;

/**
 * The SectionHandler class.
 */
public interface SectionHandler {

    Boolean canHandle(String category, String criteria, ApiContext apiContext);

    Promise<List<SectionInfoNode>> getTopLevelSectionInfoNode(ApiContext apiContext);

    Promise<SectionInfoNode> getSectionInfoNode(String category, String criteria, ApiContext apiContext);

    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext);

    Promise<ListResponse> getSectionList(ListRequest request, ApiContext apiContext);

}
