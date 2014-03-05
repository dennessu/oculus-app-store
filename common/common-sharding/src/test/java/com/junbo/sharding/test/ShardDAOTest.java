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
public class ShardDAOTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private ShardDAO shardDao;

    @Test(enabled = false)
    public void shardDaoTest() {
        for (int i = 0; i < 100; i++) {
            ShardEntity entity = new ShardEntity();
            entity.setId(new Long(i));
            shardDao.saveShard(entity);
        }
    }
}
