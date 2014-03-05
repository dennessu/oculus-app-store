/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.SubledgerEventDao;
import com.junbo.order.db.entity.SubledgerEventEntity;
import org.springframework.stereotype.Repository;

/**
 * Created by LinYi on 2/8/14.
 */
@Repository("subledgerEventDao")
public class SubledgerEventDaoImpl extends BaseDaoImpl<SubledgerEventEntity> implements SubledgerEventDao {
}
