/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao;

import com.junbo.cart.db.util.Generator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;

/**
 * Created by fzhang@wan-san.com on 14-1-21.
 */
@ContextConfiguration(locations = {
        "/context-dao-test.xml"})
public class DaoTestBase extends AbstractTransactionalTestNGSpringContextTests {

    protected Generator testGenerator = new Generator();

}
