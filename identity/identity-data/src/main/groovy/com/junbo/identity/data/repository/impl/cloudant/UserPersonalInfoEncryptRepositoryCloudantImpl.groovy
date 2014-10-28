package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.memcached.JunboMemcachedClient
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.crypto.spec.resource.CryptoResource
import com.junbo.identity.common.util.HashHelper
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.hash.PiiHashFactory
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.repository.EncryptUserPersonalInfoRepository
import com.junbo.identity.data.repository.HashUserPersonalInfoRepository
import com.junbo.identity.data.repository.UserPersonalInfoIdToUserIdLinkRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.v1.model.*
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import net.spy.memcached.CASValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import java.util.Locale

/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
class UserPersonalInfoEncryptRepositoryCloudantImpl extends CloudantClient<UserPersonalInfo>
        implements UserPersonalInfoRepository {
    private static final String MD5_ALGORITHM = 'MD5'
    Logger logger = LoggerFactory.getLogger(UserPersonalInfoEncryptRepositoryCloudantImpl)
    private static JunboMemcachedClient junboMemcachedClient = JunboMemcachedClient.instance()

    private PiiHashFactory piiHashFactory

    private UserPersonalInfoIdToUserIdLinkRepository userIdLinkRepository

    private EncryptUserPersonalInfoRepository encryptUserPersonalInfoRepository

    private HashUserPersonalInfoRepository hashUserPersonalInfoRepository

    private CryptoResource cryptoResource

    private IdGenerator idGenerator

    private Integer expiration

    private Integer maxEntitySize

    @Override
    Promise<List<UserPersonalInfo>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userIdLinkRepository.searchByUserId(userId, limit, offset).then { List<UserPersonalInfoIdToUserIdLink> links ->
            List<UserPersonalInfo> userPersonalInfoList = new ArrayList<>()
            if (CollectionUtils.isEmpty(links)) {
                return Promise.pure(userPersonalInfoList)
            }

            return Promise.each(links) { UserPersonalInfoIdToUserIdLink link ->
                if (link == null || link.userPersonalInfoId == null) {
                    return Promise.pure(null)
                }
                return get(link.userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
                    if (userPersonalInfo != null) {
                        userPersonalInfoList.add(userPersonalInfo)
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(userPersonalInfoList)
            }
        }
    }
    @Override
    Promise<List<UserPersonalInfo>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset) {
        return searchByUserId(userId, limit, offset).then { List<UserPersonalInfo> userPersonalInfoList ->
            if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                return Promise.pure(new ArrayList())
            }
            userPersonalInfoList.removeAll { UserPersonalInfo userPersonalInfo ->
                return userPersonalInfo.type != type
            }

            return Promise.pure(userPersonalInfoList)
        }
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByEmail(String email, Boolean isValidated, Integer limit, Integer offset) {
        PiiHash hash = getPiiHash(UserPersonalInfoType.EMAIL.toString())

        List<UserPersonalInfo> infos = new ArrayList<>()
        return hashUserPersonalInfoRepository.searchByHashValue(hash.generateHash(email.toLowerCase(Locale.ENGLISH))).then {
            List<HashUserPersonalInfo> userPersonalInfos ->
                if (CollectionUtils.isEmpty(userPersonalInfos)) {
                    return Promise.pure(infos)
                }

                return Promise.each(userPersonalInfos) { HashUserPersonalInfo personalInfo ->
                    return get(personalInfo.getId()).then { UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo == null || userPersonalInfo.type != UserPersonalInfoType.EMAIL.toString()) {
                            return Promise.pure(infos)
                        }

                        if (isValidated != null) {
                            if ((isValidated && userPersonalInfo.lastValidateTime == null) || (!isValidated && userPersonalInfo.lastValidateTime != null)) {
                                return Promise.pure(infos)
                            }
                        }

                        Email emailObj = (Email)JsonHelper.jsonNodeToObj(userPersonalInfo.value, Email)
                        if (emailObj.info.toLowerCase(Locale.ENGLISH) == email.toLowerCase(Locale.ENGLISH)) {
                            infos.add(userPersonalInfo)
                        }
                        return Promise.pure(infos)
                    }
                }.then {
                    return Promise.pure(infos)
                }
        }
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByCanonicalUsername(String canonicalUsername, Integer limit, Integer offset) {
        PiiHash hash = getPiiHash(UserPersonalInfoType.USERNAME.toString())

        List<UserPersonalInfo> infos = new ArrayList<>()
        return hashUserPersonalInfoRepository.searchByHashValue(hash.generateHash(canonicalUsername)).then {
            List<HashUserPersonalInfo> userPersonalInfos ->
                if (CollectionUtils.isEmpty(userPersonalInfos)) {
                    return Promise.pure(infos)
                }

                return Promise.each(userPersonalInfos) { HashUserPersonalInfo personalInfo ->
                    return get(personalInfo.getId()).then { UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo == null || userPersonalInfo.type != UserPersonalInfoType.USERNAME.toString()) {
                            return Promise.pure(infos)
                        }

                        UserLoginName loginNameObj = (UserLoginName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserLoginName)
                        if (loginNameObj.canonicalUsername == canonicalUsername) {
                            infos.add(userPersonalInfo)
                        }
                        return Promise.pure(infos)
                    }
                }.then {
                    return Promise.pure(infos)
                }
        }
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByPhoneNumber(String phoneNumber, Boolean isValidated, Integer limit, Integer offset) {
        PiiHash hash = getPiiHash(UserPersonalInfoType.PHONE.toString())

        List<UserPersonalInfo> infos = new ArrayList<>()
        return hashUserPersonalInfoRepository.searchByHashValue(hash.generateHash(phoneNumber)).then {
            List<HashUserPersonalInfo> userPersonalInfos ->
                if (CollectionUtils.isEmpty(userPersonalInfos)) {
                    return Promise.pure(infos)
                }

                return Promise.each(userPersonalInfos) { HashUserPersonalInfo personalInfo ->
                    return get(personalInfo.getId()).then { UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo == null ||
                            userPersonalInfo.type != UserPersonalInfoType.PHONE.toString()) {
                            return Promise.pure(infos)
                        }

                        if (isValidated != null) {
                            if ((isValidated && userPersonalInfo.lastValidateTime == null)
                                    || (!isValidated && userPersonalInfo.lastValidateTime != null)) {
                                return Promise.pure(infos)
                            }
                        }

                        PhoneNumber phoneObj = (PhoneNumber)JsonHelper.jsonNodeToObj(userPersonalInfo.value,
                                PhoneNumber)
                        if (phoneObj.info == phoneNumber) {
                            infos.add(userPersonalInfo)
                        }
                        return Promise.pure(infos)
                    }
                }.then {
                    return Promise.pure(infos)
                }
        }
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByName(String name, Integer limit, Integer offset) {
        PiiHash hash = getPiiHash(UserPersonalInfoType.NAME.toString())

        List<UserPersonalInfo> infos = new ArrayList<>()
        return hashUserPersonalInfoRepository.searchByHashValue(hash.generateHash(name)).then {
            List<HashUserPersonalInfo> userPersonalInfos ->
                if (CollectionUtils.isEmpty(userPersonalInfos)) {
                    return Promise.pure(infos)
                }

                return Promise.each(userPersonalInfos) { HashUserPersonalInfo personalInfo ->
                    return get(personalInfo.getId()).then { UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo == null ||
                                userPersonalInfo.type != UserPersonalInfoType.NAME.toString()) {
                            return Promise.pure(infos)
                        }

                        UserName nameObj = (UserName) JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserName)
                        if (name.contains(nameObj.givenName) || name.contains(nameObj.familyName)) {
                            infos.add(userPersonalInfo)
                        }
                        return Promise.pure(infos)
                    }
                }.then {
                    return Promise.pure(infos)
                }
        }
    }

    @Override
    Promise<UserPersonalInfo> create(UserPersonalInfo model) {
        if (model.id == null) {
            model.id = new UserPersonalInfoId(idGenerator.nextId(model.userId == null ? model.organizationId.value : model.userId.value))
        }

        CryptoMessage cryptoMessage = new CryptoMessage()
        cryptoMessage.value = marshall(model)

        return cryptoResource.encrypt(cryptoMessage).then { CryptoMessage messageValue ->
            EncryptUserPersonalInfo encryptUserPersonalInfo = new EncryptUserPersonalInfo()
            encryptUserPersonalInfo.id = (UserPersonalInfoId)model.id
            encryptUserPersonalInfo.encryptUserPersonalInfo = messageValue.value
            PiiHash piiHash = getPiiHash(model.type)

            HashUserPersonalInfo hashUserPersonalInfo = new HashUserPersonalInfo()
            hashUserPersonalInfo.id = (UserPersonalInfoId)model.id
            hashUserPersonalInfo.hashSearchInfo = piiHash.generateHash(model.value)

            UserPersonalInfoIdToUserIdLink link = new UserPersonalInfoIdToUserIdLink(
                    userId: model.userId,
                    userPersonalInfoId: (UserPersonalInfoId)model.id
            )

            return userIdLinkRepository.create(link).then {
                return hashUserPersonalInfoRepository.create(hashUserPersonalInfo)
            }.then {
                return encryptUserPersonalInfoRepository.create(encryptUserPersonalInfo).then { EncryptUserPersonalInfo info ->
                    return get(info.getId())
                }
            }
        }
    }

    @Override
    Promise<UserPersonalInfo> update(UserPersonalInfo model, UserPersonalInfo oldModel) {
        CryptoMessage cryptoMessage = new CryptoMessage()
        cryptoMessage.value = marshall(model)

        return cryptoResource.encrypt(cryptoMessage).then { CryptoMessage messageValue ->
            return encryptUserPersonalInfoRepository.get(model.getId()).then { EncryptUserPersonalInfo info ->
                info.encryptUserPersonalInfo = messageValue.value
                return encryptUserPersonalInfoRepository.update(info, info).then { EncryptUserPersonalInfo updateInfo ->
                    // Delete cache, then get it again
                    deleteCache(updateInfo.encryptUserPersonalInfo)
                    return hashUserPersonalInfoRepository.get(model.getId()).then { HashUserPersonalInfo hashInfo ->
                        PiiHash piiHash = getPiiHash(model.type)
                        hashInfo.hashSearchInfo = piiHash.generateHash(model.value)
                        return hashUserPersonalInfoRepository.update(hashInfo, hashInfo)
                    }.then {
                        return get(updateInfo.getId()).then { UserPersonalInfo updated ->
                            return Promise.pure(updated)
                        }
                    }
                }
            }
        }
    }

    @Override
    Promise<UserPersonalInfo> get(UserPersonalInfoId personalInfoId) {
        if (personalInfoId == null || personalInfoId.value == null) {
            return Promise.pure(null)
        }

        return encryptUserPersonalInfoRepository.get(personalInfoId).then { EncryptUserPersonalInfo encryptUserPersonalInfo ->
            if (encryptUserPersonalInfo == null) {
                return Promise.pure(null)
            }

            CASValue<UserPersonalInfoEncryptPair> cachedValue = getCache(encryptUserPersonalInfo.encryptUserPersonalInfo)
            if (cachedValue != null && encryptUserPersonalInfo.encryptUserPersonalInfo == cachedValue.value.encryptMessage) {
                return Promise.pure(cachedValue.value.userPersonalInfo)
            }

            CryptoMessage cryptoMessage = new CryptoMessage(
                    value: encryptUserPersonalInfo.encryptUserPersonalInfo
            )
            return cryptoResource.decrypt(cryptoMessage).then { CryptoMessage decrypt ->
                UserPersonalInfo userPersonalInfo = unmarshall(decrypt.value, UserPersonalInfo)
                userPersonalInfo.createdBy = encryptUserPersonalInfo.createdBy
                userPersonalInfo.createdTime = encryptUserPersonalInfo.createdTime
                userPersonalInfo.updatedBy = encryptUserPersonalInfo.updatedBy
                userPersonalInfo.updatedTime = encryptUserPersonalInfo.updatedTime
                userPersonalInfo.resourceAge = encryptUserPersonalInfo.resourceAge
                addOrUpdateCache(encryptUserPersonalInfo.encryptUserPersonalInfo, userPersonalInfo)
                return Promise.pure(userPersonalInfo)
            }
        }
    }

    @Override
    Promise<Void> delete(UserPersonalInfoId id) {
        throw new IllegalStateException('Delete is not supported')
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByUserIdAndValidateStatus(UserId userId, String type, Boolean isValidated,
                                                                    Integer limit, Integer offset) {

        List<UserPersonalInfo> list = new ArrayList<>()
        return searchCandidatesByUserIdAndType(userId, type).then { List<UserPersonalInfo> userPersonalInfoList ->
            if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                return Promise.pure(list)
            }

            userPersonalInfoList.retainAll { UserPersonalInfo userPersonalInfo ->
                return ((userPersonalInfo.lastValidateTime != null && isValidated)
                    || (userPersonalInfo.lastValidateTime == null && !isValidated))
            }

            Integer calLimit = limit == null ? Integer.MAX_VALUE : limit
            Integer calOffset = offset == null ? 0 : offset
            for (int index = 0; index < userPersonalInfoList.size() && index < calLimit + calOffset; index ++) {
                if (index >= calOffset) {
                    list.add(userPersonalInfoList.get(index))
                }
            }

            return Promise.pure(list)
        }
    }

    private Promise<List<UserPersonalInfo>> searchCandidatesByUserIdAndType(UserId userId, String type) {
        if (StringUtils.isEmpty(type)) {
            return searchByUserId(userId, Integer.MAX_VALUE, 0)
        } else {
            return searchByUserIdAndType(userId, type, Integer.MAX_VALUE, 0)
        }
    }

    private PiiHash getPiiHash(String type) {
        PiiHash hash = piiHashFactory.getAllPiiHashes().find { PiiHash piiHash ->
            return piiHash.handles(type)
        }
        if (hash == null) {
            throw new IllegalStateException('No hash implementation for type ' + type)
        }

        return hash
    }

    private void addOrUpdateCache(String rawValue, UserPersonalInfo userPersonalInfo) {
        if (junboMemcachedClient == null) {
            return
        }
        deleteCache(rawValue)
        try {
            UserPersonalInfoEncryptPair pair = new UserPersonalInfoEncryptPair()
            pair.encryptMessage = rawValue
            pair.userPersonalInfo = userPersonalInfo
            junboMemcachedClient.add(getKey(rawValue), expiration, marshaller.marshall(pair))
        } catch (Exception ex) {
            logger.warn("Error add or update from memcached", ex)
        }
    }

    private CASValue<UserPersonalInfoEncryptPair> getCache(String rawValue) {
        if (junboMemcachedClient == null) {
            return null
        }
        try {
            CASValue<String> casValue = (CASValue<String>)junboMemcachedClient.gets(getKey(rawValue))
            if (casValue == null) {
                return null
            }
            try {
                UserPersonalInfoEncryptPair result =
                        (UserPersonalInfoEncryptPair) marshaller.unmarshall(casValue.value, UserPersonalInfoEncryptPair)
                if (result != null && logger.isDebugEnabled()) {
                    logger.debug("Found {} from memcached.", getKey(rawValue))
                }
                return new CASValue<UserPersonalInfoEncryptPair>(casValue.getCas(), result)
            } catch (Exception ex) {
                logger.warn("Error unmarshalling from memcached.", ex)
                deleteCacheOnError(rawValue)
            }
        } catch (Exception ex) {
            logger.warn("Error getting from memcached.", ex)
        }
        return null
    }

    void deleteCache(String rawValue) {
        if (junboMemcachedClient == null) {
            return
        }
        try {
            junboMemcachedClient.delete(getKey(rawValue)).get()
        } catch (Exception ex) {
            logger.warn("Error deleting from memcached.", ex)
        }
    }

    void deleteCacheOnError(String rawValue) {
        if (junboMemcachedClient == null) {
            return
        }
        try {
            // async delete
            junboMemcachedClient.delete(getKey(rawValue))
        } catch (Exception ex) {
            logger.warn("Error deleting key on error from memcached.", ex)
        }
    }

    private String getKey(String encryptedMessage) {
        return HashHelper.shaHash(encryptedMessage, null, MD5_ALGORITHM)
    }

    @Required
    void setPiiHashFactory(PiiHashFactory piiHashFactory) {
        this.piiHashFactory = piiHashFactory
    }

    @Required
    void setUserIdLinkRepository(UserPersonalInfoIdToUserIdLinkRepository userIdLinkRepository) {
        this.userIdLinkRepository = userIdLinkRepository
    }

    @Required
    void setEncryptUserPersonalInfoRepository(EncryptUserPersonalInfoRepository encryptUserPersonalInfoRepository) {
        this.encryptUserPersonalInfoRepository = encryptUserPersonalInfoRepository
    }

    @Required
    void setHashUserPersonalInfoRepository(HashUserPersonalInfoRepository hashUserPersonalInfoRepository) {
        this.hashUserPersonalInfoRepository = hashUserPersonalInfoRepository
    }

    @Required
    void setCryptoResource(CryptoResource cryptoResource) {
        this.cryptoResource = cryptoResource
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setExpiration(Integer expiration) {
        this.expiration = expiration
    }

    @Required
    void setMaxEntitySize(Integer maxEntitySize) {
        this.maxEntitySize = maxEntitySize
    }

    static class UserPersonalInfoEncryptPair implements Serializable {
        public String encryptMessage
        public UserPersonalInfo userPersonalInfo
    }
}
