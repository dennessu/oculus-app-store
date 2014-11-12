/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo;

import com.junbo.common.id.SubledgerEventId;
import com.junbo.common.id.SubledgerId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.SubledgerEvent;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by fzhang on 2015/1/18.
 */
public interface SubledgerEventRepository extends BaseRepository<SubledgerEvent, SubledgerEventId> {

    @ReadMethod
    Promise<List<SubledgerEvent>> getBySubledgerId(SubledgerId subledgerId);

}
