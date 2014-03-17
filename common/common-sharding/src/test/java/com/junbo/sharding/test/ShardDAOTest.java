package com.junbo.sharding.test;

import com.junbo.sharding.test.data.dao.ShardDAO;
import com.junbo.sharding.test.data.dao.ShardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.transaction.TransactionRolledbackException;

/**
 * Created by haomin on 14-3-4.
 */
@ContextConfiguration(locations = {
        "/spring/sharding-context-test.xml"
})
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class ShardDAOTest extends AbstractTestNGSpringContextTests {

    @Resource(name="shardDao")
    private ShardDAO shardDao;

    @Test
    @Transactional
    public void shardDaoTest() {
        ShardEntity entity = new ShardEntity();
        ShardEntity saved = shardDao.saveShard(entity);

        ShardEntity entity1 = new ShardEntity();
        ShardEntity saved1 = shardDao.saveShard(entity1);

        Assert.assertNotNull(saved.getId());
    }
}
