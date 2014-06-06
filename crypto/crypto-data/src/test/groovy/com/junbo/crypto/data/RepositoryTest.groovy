package com.junbo.crypto.data

import com.junbo.common.id.UserId
import com.junbo.crypto.data.repo.MasterKeyRepo
import com.junbo.crypto.data.repo.UserCryptoKeyRepo
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.crypto.spec.model.UserCryptoKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.testng.annotations.Test

/**
 * Created by liangfu on 5/12/14.
 */
@ContextConfiguration(locations = ['classpath:test/spring/context-test.xml'])
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
class RepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MasterKeyRepo masterKeyRepo

    @Autowired
    private UserCryptoKeyRepo userCryptoKeyRepo

    @Test
    public void testMasterKeyRepo() {
        MasterKey masterKey = new MasterKey()
        masterKey.setEncryptValue(UUID.randomUUID().toString())
        MasterKey newMaster = masterKeyRepo.create(masterKey).get()

        masterKey = masterKeyRepo.get(newMaster.id).get()

        assert newMaster.encryptValue == masterKey.encryptValue

        List<MasterKey> list = masterKeyRepo.getAllMaterKeys().get()
        assert list.size() != 0
    }

    @Test
    public void testUserCryptoRepo() {
        UserId userId = new UserId(1493188608L)
        UserCryptoKey userCryptoKey = new UserCryptoKey()
        userCryptoKey.setEncryptValue(UUID.randomUUID().toString())
        userCryptoKey.setUserId(userId)

        UserCryptoKey newUserCryptoKey = userCryptoKeyRepo.create(userCryptoKey).get()

        userCryptoKey = userCryptoKeyRepo.get(newUserCryptoKey.id).get()

        assert userCryptoKey.encryptValue == newUserCryptoKey.encryptValue

        List<UserCryptoKey> list = userCryptoKeyRepo.getAllUserCryptoKeys(userId).get()
        assert list.size() != 0
    }
}
