/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.sql.DataSource;

/**
 * Base class of Test.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
public abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
    /**
     * Set the {@code DataSource}, typically provided via Dependency Injection.
     * <p>This method also instantiates the {@link #jdbcTemplate} instance variable.
     *
     * @param dataSource
     */
    @Autowired
    protected IdGeneratorFacade idGenerator;

    @Override
    @Qualifier("orderDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
}
}
