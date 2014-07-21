package com.junbo.crypto.rest.resource
import com.junbo.crypto.core.validator.ItemCryptoValidator
import com.junbo.crypto.data.repo.ItemCryptoRepo
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.crypto.spec.model.ItemCryptoRepoData
import com.junbo.crypto.spec.model.ItemCryptoVerify
import com.junbo.crypto.spec.resource.CryptoResource
import com.junbo.crypto.spec.resource.ItemCryptoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.annotation.Transactional

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.ConcurrentHashMap
/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
@Transactional
class ItemCryptoResourceImpl extends CommonResourceImpl implements ItemCryptoResource {

    private static final String KEY_GENERATOR_ALGORITHM = 'RSA'
    private static final Integer KEY_GENERATOR_ALGORITHM_LENGTH = 2048
    private static final String SIGNATURE_ALGORITHM = 'SHA1withRSA'

    private static Map<String, PublicKey> publicKeyMap = new ConcurrentHashMap<>()

    private static Map<String, PrivateKey> privateKeyMap = new ConcurrentHashMap<>()

    private ItemCryptoValidator validator

    private ItemCryptoRepo itemCryptoRepo

    private CryptoResource cryptoResource

    @Required
    void setValidator(ItemCryptoValidator validator) {
        this.validator = validator
    }

    @Required
    void setItemCryptoRepo(ItemCryptoRepo itemCryptoRepo) {
        this.itemCryptoRepo = itemCryptoRepo
    }

    @Required
    void setCryptoResource(CryptoResource cryptoResource) {
        this.cryptoResource = cryptoResource
    }

    @Override
    Promise<ItemCryptoMessage> sign(String itemId, ItemCryptoMessage rawMessage) {
        validator.validateForSign(itemId, rawMessage)

        PrivateKey privateKey = privateKeyMap.get(itemId)

        if (privateKey == null) {
            return checkAndUpdateItemKey(itemId).then {
                privateKey = privateKeyMap.get(itemId)
                return signMessageByKey(privateKey, rawMessage.message)
            }
        }

        return signMessageByKey(privateKey, rawMessage.message)
    }

    @Override
    Promise<String> refresh(String itemId) {
        return validator.validateForRefresh(itemId).then { ItemCryptoRepoData data ->
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_GENERATOR_ALGORITHM)
            kpg.initialize(KEY_GENERATOR_ALGORITHM_LENGTH)
            KeyPair kp = kpg.genKeyPair()
            PublicKey key1 = kp.getPublic()
            PrivateKey key2 = kp.getPrivate()
            return encryptByMasterKey(savePrivateKey(key2)).then { String str ->
                data.encryptedPrivateKey = str
                return encryptByMasterKey(savePublicKey(key1))
            }.then { String str ->
                data.encryptedPublicKey = str

                return itemCryptoRepo.update(data, data)
            }.then { ItemCryptoRepoData cryptoRepoData ->

                return decryptByMasterKey(cryptoRepoData.encryptedPrivateKey).then { String value ->
                    privateKeyMap.put(itemId, loadPrivateKey(value))

                    return decryptByMasterKey(cryptoRepoData.encryptedPublicKey)
                }.then { String value ->
                    publicKeyMap.put(itemId, loadPublicKey(value))

                    return Promise.pure(value)
                }
            }
        }
    }

    @Override
    Promise<String> getPublicKey(String itemId) {
        validator.validateForGetPublicKey(itemId)
        return checkAndUpdateItemKey(itemId).then {
            return Promise.pure(savePublicKey(publicKeyMap.get(itemId)))
        }
    }

    @Override
    Promise<Boolean> verify(String itemId, ItemCryptoVerify message) {
        validator.validateForVerify(itemId, message)
        return checkAndUpdateItemKey(itemId).then {
            return verifyMessage(publicKeyMap.get(itemId), message.messageSigned, message.rawMessage)
        }
    }

    private Promise<Void> checkAndUpdateItemKey(String itemId) {
        PrivateKey privateKey = privateKeyMap.get(itemId)
        if (privateKey == null) {
            // fetch db first
            return itemCryptoRepo.getByItemId(itemId).then { ItemCryptoRepoData data ->
                if (data == null) {
                    // need to save this key
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_GENERATOR_ALGORITHM)
                    kpg.initialize(KEY_GENERATOR_ALGORITHM_LENGTH)
                    KeyPair kp = kpg.genKeyPair()
                    PublicKey key1 = kp.getPublic()
                    PrivateKey key2 = kp.getPrivate()
                    ItemCryptoRepoData created = new ItemCryptoRepoData(
                            itemId: itemId
                    )
                    return encryptByMasterKey(savePrivateKey(key2)).then { String str ->
                        created.encryptedPrivateKey = str
                        return encryptByMasterKey(savePublicKey(key1))
                    }.then { String str ->
                        created.encryptedPublicKey = str

                        return itemCryptoRepo.create(created)
                    }.then { ItemCryptoRepoData cryptoRepoData ->

                        return decryptByMasterKey(cryptoRepoData.encryptedPrivateKey).then { String value ->
                            privateKeyMap.put(itemId, loadPrivateKey(value))

                            return decryptByMasterKey(cryptoRepoData.encryptedPublicKey)
                        }.then { String value ->
                            publicKeyMap.put(itemId, loadPublicKey(value))

                            return Promise.pure(null)
                        }
                    }
                }

                return decryptByMasterKey(data.encryptedPrivateKey).then { String value ->
                    privateKeyMap.put(itemId, loadPrivateKey(value))

                    return decryptByMasterKey(data.encryptedPublicKey)
                }.then { String value ->
                    publicKeyMap.put(itemId, loadPublicKey(value))

                    return Promise.pure(null)
                }
            }
        }

        return Promise.pure(null)
    }

    private Promise<String> encryptByMasterKey(String data) {
        CryptoMessage message = new CryptoMessage(
              value: data
        )

        return cryptoResource.encrypt(message).then { CryptoMessage cryptoMessage ->
            return Promise.pure(cryptoMessage.value)
        }
    }

    private Promise<String> decryptByMasterKey(String data) {
        CryptoMessage message = new CryptoMessage(
                value: data
        )

        return cryptoResource.decrypt(message).then { CryptoMessage cryptoMessage ->
            return Promise.pure(cryptoMessage.value)
        }
    }

    private Promise<ItemCryptoMessage> signMessageByKey(PrivateKey key, String message) {
        Signature instance = Signature.getInstance(SIGNATURE_ALGORITHM)
        instance.initSign(key)
        instance.update(message.getBytes("UTF-8"))
        String signed = new String(Base64.encodeBase64(instance.sign()))
        ItemCryptoMessage itemCryptoMessage = new ItemCryptoMessage(
                message: signed
        )

        return Promise.pure(itemCryptoMessage)
    }

    private Promise<Boolean> verifyMessage(PublicKey key, String signedMessage, String rawMessage) {
        try {
            Signature instance = Signature.getInstance(SIGNATURE_ALGORITHM)
            instance.initVerify(key)
            instance.update(rawMessage.getBytes("UTF-8"))
            return Promise.pure(instance.verify(Base64.decodeBase64(signedMessage)))
        } catch (Exception e) {
            throw new IllegalStateException('Verify failed. Exception: ' + e.message)
        }
    }

    public static PrivateKey loadPrivateKey(String key) {
        try {
            byte[] clear = Base64.decodeBase64(key)
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear)
            KeyFactory fact = KeyFactory.getInstance(KEY_GENERATOR_ALGORITHM)
            PrivateKey priv = fact.generatePrivate(keySpec)
            Arrays.fill(clear, (byte) 0)
            return priv
        } catch (Exception e) {
            throw new IllegalStateException('Load public Key from string failed. Exception: ' + e.message)
        }
    }

    public static PublicKey loadPublicKey(String key) {
        try {
            byte[] data = Base64.decodeBase64(key)
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data)
            KeyFactory fact = KeyFactory.getInstance(KEY_GENERATOR_ALGORITHM)
            return fact.generatePublic(spec)
        } catch (Exception e) {
            throw new IllegalStateException('Load public Key from string failed. Exception: ' + e.message)
        }
    }

    public static String savePrivateKey(PrivateKey privateKey) {
        try {
            KeyFactory fact = KeyFactory.getInstance(KEY_GENERATOR_ALGORITHM)
            PKCS8EncodedKeySpec spec = fact.getKeySpec(privateKey, PKCS8EncodedKeySpec.class);
            byte[] packed = spec.getEncoded()
            String key = new String(Base64.encodeBase64(packed))
            Arrays.fill(packed, (byte) 0)
            return key
        } catch (Exception e) {
            throw new IllegalStateException('Convert private key to string failed. Exception: ' + e.message)
        }
    }

    public static String savePublicKey(PublicKey publicKey) {
        try {
            KeyFactory fact = KeyFactory.getInstance(KEY_GENERATOR_ALGORITHM)
            X509EncodedKeySpec spec = fact.getKeySpec(publicKey, X509EncodedKeySpec.class)
            return new String(Base64.encodeBase64(spec.getEncoded()))
        } catch (Exception e) {
            throw new IllegalStateException('Convert public key to string failed. Exception: ' + e.message)
        }
    }
}
