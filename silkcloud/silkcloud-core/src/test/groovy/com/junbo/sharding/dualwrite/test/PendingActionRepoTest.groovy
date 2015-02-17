/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.dualwrite.test
import com.junbo.common.id.UserId
import com.junbo.common.model.ResourceMeta
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.dualwrite.data.PendingAction
import com.junbo.sharding.dualwrite.data.PendingActionRepository
import com.junbo.sharding.dualwrite.data.PendingActionRepositorySqlImpl
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.transaction.annotation.Transactional
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull
/**
 * Java doc for IdGeneratorTest.
 */
@ContextConfiguration(locations = "classpath:spring/sharding-context-test.xml")
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
@CompileStatic
public class PendingActionRepoTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    @Autowired
    @Qualifier("pendingActionSqlRepo")
    private PendingActionRepositorySqlImpl pendingActionSqlRepo;

    @Test
    public void testPendingActionSqlRepository() {
        def impl = pendingActionSqlRepo
        logger.info("SQL Repository with hardDelete: ${impl.hardDelete}")

        testPendingActionRepository(impl)

        impl.hardDelete = !impl.hardDelete
        logger.info("SQL Repository with hardDelete: ${impl.hardDelete}")

        testPendingActionRepository(impl)

        impl.hardDelete = !impl.hardDelete
    }

    private void testPendingActionRepository(PendingActionRepository repo) {

        PendingAction savedEntity = createSavedEntityPendingAction();

        PendingAction newPending = repo.create(savedEntity).get();
        assertEquals(newPending.getChangedEntityId(), savedEntity.getChangedEntityId())
        assertEquals(newPending.getSavedEntity(), savedEntity.getSavedEntity())

        newPending.deletedKey = 54321L;
        newPending = repo.update(newPending, savedEntity).get();
        assertEquals(newPending.deletedKey, 54321L)

        newPending = repo.get(newPending.id).get()
        assertEquals(newPending.deletedKey, 54321L)
        assertEquals(newPending.getChangedEntityId(), savedEntity.getChangedEntityId())
        assertEquals((FakeEntity)newPending.getSavedEntity(), (FakeEntity)savedEntity.getSavedEntity())

        repo.delete(newPending.id).get()
        newPending = repo.get(newPending.id).get()
        assertNull(newPending)
    }

    private PendingAction createSavedEntityPendingAction() {
        PendingAction pendingAction = new PendingAction();
        pendingAction.setId(null);
        pendingAction.setRetryCount(0);
        pendingAction.setChangedEntityId(idGenerator.nextId());
        pendingAction.setSavedEntity(createFakeEntity());
        pendingAction.setDeletedKey(778899L);

        return pendingAction;
    }

    private FakeEntity createFakeEntity() {
        FakeEntity fakeEntity = new FakeEntity();
        fakeEntity.setId(new UserId(idGenerator.nextId()));
        fakeEntity.setUsername("Hello World");

        return fakeEntity;
    }

    private static class FakeEntity extends ResourceMeta<UserId> {
        private UserId id;
        private String username;

        public UserId getId() {
            return id
        }

        public void setId(UserId id) {
            this.id = id
        }

        public String getUsername() {
            return username
        }

        public void setUsername(String username) {
            this.username = username
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            FakeEntity that = (FakeEntity) o

            if (id != that.id) return false
            if (username != that.username) return false

            return true
        }

        int hashCode() {
            int result
            result = (id != null ? id.hashCode() : 0)
            result = 31 * result + (username != null ? username.hashCode() : 0)
            return result
        }
    }
}
