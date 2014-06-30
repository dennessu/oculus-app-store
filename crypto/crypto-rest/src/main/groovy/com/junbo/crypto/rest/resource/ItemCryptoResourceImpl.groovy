package com.junbo.crypto.rest.resource

import com.junbo.crypto.core.validator.ItemCryptoValidator
import com.junbo.crypto.data.repo.ItemCryptoRepo
import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.crypto.spec.model.ItemCryptoRepoData
import com.junbo.crypto.spec.resource.ItemCryptoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
@Transactional
class ItemCryptoResourceImpl extends CommonResourceImpl implements ItemCryptoResource {

    private static final String KEY_GENERATOR_ALGORITHM = 'RSA'
    private static final Integer KEY_GENERATOR_ALGORITHM_LENGTH = 2048

    private static Map<String, Key> publicKeyMap = new ConcurrentHashMap<>()

    private static Map<String, Key> privateKeyMap = new ConcurrentHashMap<>()

    @Autowired
    private ItemCryptoValidator validator

    @Autowired
    private ItemCryptoRepo itemCryptoRepo

    @Override
    Promise<ItemCryptoMessage> sign(String itemId, ItemCryptoMessage rawMessage) {
        validator.validateForSign(itemId, rawMessage)

        Key privateKey = privateKeyMap.get(itemId)

        if (privateKey == null) {
            return checkAndUpdateItemKey(itemId).then {

            }
        }

        return null
    }

    @Override
    Promise<String> getPublicKey(String itemId) {
        return null
    }

    private Promise<Void> checkAndUpdateItemKey(String itemId) {
        Key privateKey = privateKeyMap.get(itemId)
        if (privateKey == null) {
            // fetch db first
            return itemCryptoRepo.getByItemId(itemId).then { ItemCryptoRepoData data ->
                if (data == null) {
                    // need to save this key
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_GENERATOR_ALGORITHM)
                    kpg.initialize(KEY_GENERATOR_ALGORITHM_LENGTH)
                    KeyPair kp = kpg.genKeyPair()
                    Key key1 = kp.getPublic()
                    Key key2 = kp.getPrivate()
                    ItemCryptoRepoData created = new ItemCryptoRepoData(
                            itemId: itemId,
                            privateKey: new String(key2.getEncoded()),
                            publicKey: new String(key1.getEncoded())
                    )

                    return itemCryptoRepo.create(created).then { ItemCryptoRepoData cryptoRepoData ->
                        privateKeyMap.put(itemId, rsaCipherService.stringToKey(cryptoRepoData.privateKey))
                        publicKeyMap.put(itemId, rsaCipherService.stringToKey(cryptoRepoData.publicKey))
                    }
                }

                // decrypt the
                privateKeyMap.put(itemId, rsaCipherService.stringToKey(data.privateKey))
                publicKeyMap.put(itemId, rsaCipherService.stringToKey(data.publicKey))
            }
        }
    }
}
