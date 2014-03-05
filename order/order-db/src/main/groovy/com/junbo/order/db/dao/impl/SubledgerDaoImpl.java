/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.SubledgerDao;
import com.junbo.order.db.entity.SubledgerEntity;
import org.springframework.stereotype.Repository;

/**
 * Created by linyi on 14-2-8.
 */
@Repository("subledgerDao")
public class SubledgerDaoImpl extends BaseDaoImpl<SubledgerEntity> implements SubledgerDao {
}
