/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.browse;

import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.browse.document.SectionInfoNode;

import java.util.List;

/**
 * The SectionService class.
 */
public interface SectionService {

    List<SectionInfoNode> getTopLevelSectionInfoNode(ApiContext apiContext);

    SectionInfoNode  getSectionInfoNode(String category, String criteria, ApiContext apiContext);
}
