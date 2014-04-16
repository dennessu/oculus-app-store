/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.SubledgerItemDao;
import com.junbo.order.db.entity.SubledgerItemEntity;
import com.junbo.order.db.entity.enums.SubledgerItemStatus;
import com.junbo.sharding.view.ViewQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linyi on 14-2-8.
 */
@Repository("subledgerItemDao")
public class SubledgerItemDaoImpl extends BaseDaoImpl<SubledgerItemEntity> implements SubledgerItemDao {
    @Override
    public List<SubledgerItemEntity> getByStatus(SubledgerItemStatus status, int start, int count) {
        // TODO: implement page param
        SubledgerItemEntity example = new SubledgerItemEntity();
        example.setStatus(status);

        ViewQuery<Long> viewQuery = viewQueryFactory.from(example);
        if (viewQuery != null) {
            List<Long> subledgerIds = viewQuery.list();
            List<SubledgerItemEntity> subledgerItemEntities = new ArrayList<>();
            for (Long id : subledgerIds) {
                subledgerItemEntities.add(read(id));
            }

            return subledgerItemEntities;
        }

        return null;
    }
}
