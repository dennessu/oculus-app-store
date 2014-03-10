/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by haomin on 14-3-10.
 */
public interface ShardSessionFactory extends SessionFactory{
    Session getShardSession(int shardId, String dbName) throws HibernateException;
}
