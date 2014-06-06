/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;

import java.util.Collection;
import java.util.List;

/**
 * Offer revision repository.
 */
public interface ItemRevisionRepository extends BaseRevisionRepository<ItemRevision> {
    ItemRevision create(ItemRevision itemRevision);
    ItemRevision get(Long revisionId);
    List<ItemRevision> getRevisions(ItemRevisionsGetOptions options);
    List<ItemRevision> getRevisions(Collection<Long> itemIds, Long timestamp);
    List<ItemRevision> getRevisions(Long hostItemId);
    ItemRevision update(ItemRevision revision);
    void delete(Long revisionId);
}
