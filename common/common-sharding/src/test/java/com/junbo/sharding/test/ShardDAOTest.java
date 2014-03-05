package com.junbo.sharding.test;

import com.junbo.sharding.test.data.dao.ShardDAO;
import com.junbo.sharding.test.data.dao.ShardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

/**
 * Created by haomin on 14-3-4.
 */
@ContextConfiguration(locations = {
        "/spring/sharding-context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
public class ShardDAOTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private ShardDAO shardDao;

    @Test
    public void shardDaoTest() {
        ShardEntity entity = new ShardEntity();
        entity.setId(136L);
        shardDao.saveShard(entity);
    }
}
