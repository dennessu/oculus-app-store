package com.junbo.crypto.rest.resource

import com.junbo.crypto.core.validator.ItemCryptoValidator
import com.junbo.crypto.data.repo.ItemCryptoRepo
import com.junbo.crypto.spec.error.AppErrors
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.crypto.spec.model.ItemCryptoRepoData
import com.junbo.crypto.spec.model.ItemCryptoVerify
import com.junbo.crypto.spec.resource.CryptoResource
import com.junbo.crypto.spec.resource.ItemCryptoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import net.sf.ehcache.Status
import org.apache.commons.codec.binary.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.cache.ehcache.EhCacheCacheManager
import org.springframework.transaction.annotation.Transactional
import sun.security.rsa.RSASignature
import sun.security.x509.AlgorithmId

import javax.crypto.Cipher
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
@Transactional
class ItemCryptoResourceImpl extends CommonResourceImpl implements ItemCryptoResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCryptoResourceImpl)

    private static final String PRIVATE_KEY_CACHE_NAME = 'PRIVATE_KEY'
    private static final String PUBLIC_KEY_CACHE_NAME = 'PUBLIC_KEY'

    private static final String KEY_GENERATOR_ALGORITHM = 'RSA'
    private static final Integer KEY_GENERATOR_ALGORITHM_LENGTH = 2048
    private static final String SIGNATURE_ALGORITHM = 'SHA1withRSA'
    private static final String ENCRYPT_ALGORITHM = 'RSA'

    private ItemCryptoValidator validator

    private ItemCryptoRepo itemCryptoRepo

    private CryptoResource cryptoResource

    private CacheManager cacheManager

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

    @Required
    void setCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.cacheManager = ehCacheCacheManager.cacheManager
    }

    @Override
    Promise<ItemCryptoMessage> sign(String itemId, ItemCryptoMessage rawMessage, Boolean digested) {
        validator.validateForSign(itemId, rawMessage)

        Element element = getCache(PRIVATE_KEY_CACHE_NAME).get(itemId)

        if (element == null) {
            return saveOrGetPrivateKey(itemId, true).then { PrivateKey privateKey ->
                getCache(PRIVATE_KEY_CACHE_NAME).put(new Element(itemId, privateKey))
                return signMessageByKey(privateKey, rawMessage.message, digested)
            }
        }

        return signMessageByKey((PrivateKey)element.objectValue, rawMessage.message, digested)
    }

    @Override
    Promise<String> refresh(String itemId) {
        return validator.validateForRefresh(itemId).then { ItemCryptoRepoData data ->
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_GENERATOR_ALGORITHM)
            kpg.initialize(KEY_GENERATOR_ALGORITHM_LENGTH)
            KeyPair kp = kpg.genKeyPair()
            PublicKey key1 = kp.getPublic()
            PrivateKey key2 = kp.getPrivate()
            return encryptByMasterKey(encodePrivateKey(key2)).then { String str ->
                data.encryptedPrivateKey = str
                return encryptByMasterKey(encodePublicKey(key1))
            }.then { String str ->
                data.encryptedPublicKey = str

                return itemCryptoRepo.update(data, data)
            }.then { ItemCryptoRepoData cryptoRepoData ->

                return decryptByMasterKey(cryptoRepoData.encryptedPrivateKey).then { String value ->
                    Cache cache = getCache(PRIVATE_KEY_CACHE_NAME)
                    cache.put(new Element(itemId, decodePrivateKey(value)))

                    return decryptByMasterKey(cryptoRepoData.encryptedPublicKey)
                }.then { String value ->
                    Cache cache = getCache(PUBLIC_KEY_CACHE_NAME)
                    cache.put(new Element(itemId, decodePublicKey(value)))

                    return Promise.pure(value)
                }
            }
        }
    }

    @Override
    Promise<String> getPublicKey(String itemId) {
        validator.validateForGetPublicKey(itemId)
        return getOrCreateItemKeys(itemId, true).then {
            Cache cache = getCache(PUBLIC_KEY_CACHE_NAME)
            Element element = cache.get(itemId)
            if (element != null) {
                return Promise.pure(encodePublicKey((PublicKey)element.objectValue))
            }

            return saveOrGetPublicKey(itemId, false).then { PublicKey publicKey ->
                cache.put(new Element(itemId, publicKey))
                return Promise.pure(encodePublicKey(publicKey))
            }
        }
    }

    @Override
    Promise<Boolean> verify(String itemId, ItemCryptoVerify message) {
        validator.validateForVerify(itemId, message)
        return getOrCreateItemKeys(itemId, false).then {
            Cache cache = getCache(PUBLIC_KEY_CACHE_NAME)
            Element element = cache.get(itemId)
            if (element != null) {
                return verifyMessage((PublicKey)element.objectValue, message.messageSigned, message.rawMessage)
            }

            return saveOrGetPublicKey(itemId, false).then { PublicKey publicKey ->
                cache.put(new Element(itemId, publicKey))
                return verifyMessage(publicKey, message.messageSigned, message.rawMessage)
            }
        }
    }

    private Promise<PublicKey> saveOrGetPublicKey(String itemId, Boolean allowCreate) {
        return itemCryptoRepo.getByItemId(itemId).then { ItemCryptoRepoData data ->
            if (data == null && !allowCreate) {
                throw AppErrors.INSTANCE.itemKeyNotFound(itemId).exception()
            }
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
                return encryptByMasterKey(encodePrivateKey(key2)).then { String str ->
                    created.encryptedPrivateKey = str
                    return encryptByMasterKey(encodePublicKey(key1))
                }.then { String str ->
                    created.encryptedPublicKey = str

                    return itemCryptoRepo.create(created)
                }.then { ItemCryptoRepoData cryptoRepoData ->

                    return decryptByMasterKey(cryptoRepoData.encryptedPrivateKey).then { String value ->
                        return decryptByMasterKey(cryptoRepoData.encryptedPublicKey)
                    }.then { String value ->
                        return Promise.pure(key1)
                    }
                }
            }

            return decryptByMasterKey(data.encryptedPublicKey).then { String value ->
                return Promise.pure(decodePublicKey(value))
            }
        }
    }

    private Promise<PrivateKey> saveOrGetPrivateKey(String itemId, Boolean allowCreate) {
        return itemCryptoRepo.getByItemId(itemId).then { ItemCryptoRepoData data ->
            if (data == null && !allowCreate) {
                throw AppErrors.INSTANCE.itemKeyNotFound(itemId).exception()
            }
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
                return encryptByMasterKey(encodePrivateKey(key2)).then { String str ->
                    created.encryptedPrivateKey = str
                    return encryptByMasterKey(encodePublicKey(key1))
                }.then { String str ->
                    created.encryptedPublicKey = str

                    return itemCryptoRepo.create(created)
                }.then { ItemCryptoRepoData cryptoRepoData ->

                    return decryptByMasterKey(cryptoRepoData.encryptedPrivateKey).then { String value ->
                        return decryptByMasterKey(cryptoRepoData.encryptedPublicKey)
                    }.then { String value ->
                        return Promise.pure(key2)
                    }
                }
            }

            return decryptByMasterKey(data.encryptedPrivateKey).then { String value ->
                return Promise.pure(decodePrivateKey(value))
            }
        }
    }

    private Promise<Void> getOrCreateItemKeys(String itemId, Boolean allowCreate) {
        return saveOrGetPublicKey(itemId, allowCreate).then { PublicKey publicKey ->
            Cache cache = getCache(PUBLIC_KEY_CACHE_NAME)
            cache.put(new Element(itemId, publicKey))

            return Promise.pure(null)
        }.then {
            return saveOrGetPrivateKey(itemId, allowCreate).then { PrivateKey privateKey ->
                Cache cache = getCache(PRIVATE_KEY_CACHE_NAME)
                cache.put(new Element(itemId, privateKey))

                return Promise.pure(null)
            }
        }
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

    private Promise<ItemCryptoMessage> signMessageByKey(PrivateKey key, String message, boolean digested) {
        ItemCryptoMessage itemCryptoMessage
        if (!digested) {
            Signature instance = Signature.getInstance(SIGNATURE_ALGORITHM)
            instance.initSign(key)
            instance.update(Base64.decodeBase64(message))
            String signed = new String(Base64.encodeBase64(instance.sign()))
            itemCryptoMessage = new ItemCryptoMessage(
                    message: signed
            )
        } else { // already digested, only encrypt the digest
            byte[] encode = RSASignature.encodeSignature(AlgorithmId.SHA_oid, Base64.decodeBase64(message))
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            String encrypted = new String(Base64.encodeBase64(cipher.doFinal(encode)))
            itemCryptoMessage = new ItemCryptoMessage(
                    message: encrypted
            )
        }
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

    public static PrivateKey decodePrivateKey(String key) {
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

    public static PublicKey decodePublicKey(String key) {
        try {
            byte[] data = Base64.decodeBase64(key)
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data)
            KeyFactory fact = KeyFactory.getInstance(KEY_GENERATOR_ALGORITHM)
            return fact.generatePublic(spec)
        } catch (Exception e) {
            throw new IllegalStateException('Load public Key from string failed. Exception: ' + e.message)
        }
    }

    public static String encodePrivateKey(PrivateKey privateKey) {
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

    public static String encodePublicKey(PublicKey publicKey) {
        try {
            KeyFactory fact = KeyFactory.getInstance(KEY_GENERATOR_ALGORITHM)
            X509EncodedKeySpec spec = fact.getKeySpec(publicKey, X509EncodedKeySpec.class)
            return new String(Base64.encodeBase64(spec.getEncoded()))
        } catch (Exception e) {
            throw new IllegalStateException('Convert public key to string failed. Exception: ' + e.message)
        }
    }

    public Cache getCache(String cacheName) {
        if (cacheManager.status != Status.STATUS_ALIVE) {
            LOGGER.error('name=Cache_Manager_Invalid.')
            throw new IllegalStateException('Cache Manger Status is invalid')
        } else {
            Cache cache = cacheManager.getCache(cacheName)
            if (cache == null) {
                LOGGER.error('name=Cache_Manager_Invalid. Not exist Cache name : ' + cacheName)
                throw new IllegalStateException('Cache Manger Invalid. Not exist Cache name : ' + cacheName)
            }

            return cache
        }
    }
}
