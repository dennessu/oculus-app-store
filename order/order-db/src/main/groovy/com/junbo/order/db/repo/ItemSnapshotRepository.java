/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.ItemSnapshot;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by LinYi on 2014/8/28.
 */
public interface ItemSnapshotRepository extends BaseRepository<ItemSnapshot, Long> {
    @ReadMethod
    Promise<List<ItemSnapshot>> getByOfferSnapshotId(Long offerSnapshotId);
}
