/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.data
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * The pending action.
 */
@CompileStatic
public interface PendingActionRepository extends BaseRepository<PendingAction, Long> {

    Promise<List<PendingAction>> list(Integer dc, Integer shardId, Integer limit, Integer offset, Integer timeOffset);

}
