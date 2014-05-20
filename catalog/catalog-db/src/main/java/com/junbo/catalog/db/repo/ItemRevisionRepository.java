/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.common.id.ItemId;

import java.util.Collection;
import java.util.List;

/**
 * Offer revision repository.
 */
public interface ItemRevisionRepository extends BaseRevisionRepository<ItemRevision> {
    Long create(ItemRevision itemRevision);
    ItemRevision get(Long revisionId);
    List<ItemRevision> getRevisions(ItemRevisionsGetOptions options);
    List<ItemRevision> getRevisions(Collection<ItemId> itemIds, Long timestamp);
    List<ItemRevision> getRevisions(Long hostItemId);
    Long update(ItemRevision revision);
    void delete(Long revisionId);
}
