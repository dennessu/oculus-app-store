package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.crypto.spec.resource.CryptoResource
import com.junbo.identity.common.util.HashHelper
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.hash.PiiHashFactory
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.repository.EncryptUserPersonalInfoRepository
import com.junbo.identity.data.repository.UserPersonalInfoIdToUserIdLinkRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.EncryptUserPersonalInfo
import com.junbo.identity.spec.v1.model.PhoneNumber
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoIdToUserIdLink
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
class UserPersonalInfoEncryptRepositoryCloudantImpl extends CloudantClient<UserPersonalInfo>
        implements UserPersonalInfoRepository {

    private PiiHashFactory piiHashFactory

    private UserPersonalInfoIdToUserIdLinkRepository userIdLinkRepository

    private EncryptUserPersonalInfoRepository encryptUserPersonalInfoRepository

    private CryptoResource cryptoResource

    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userIdLinkRepository.searchByUserId(userId, limit, offset).then {
            List<UserPersonalInfoIdToUserIdLink> links ->
            List<UserPersonalInfo> userPersonalInfoList = new ArrayList<>()
            if (CollectionUtils.isEmpty(links)) {
                return Promise.pure(userPersonalInfoList)
            }

            return Promise.each(links) { UserPersonalInfoIdToUserIdLink link ->
                return decryptUserPersonalInfo(userId, link.userPersonalInfoId, userPersonalInfoList)
            }.then {
                return Promise.pure(userPersonalInfoList)
            }
        }
    }

    Promise<Void> decryptUserPersonalInfo(UserId userId, UserPersonalInfoId userPersonalInfoId,
                                          List<UserPersonalInfo> infoList) {
        return encryptUserPersonalInfoRepository.searchByUserPersonalInfoId(userPersonalInfoId)
                .then { EncryptUserPersonalInfo info ->
            return decryptUserPersonalInfo(userId, info.encryptUserPersonalInfo).then {
                UserPersonalInfo userPersonalInfo ->
                    if (userPersonalInfo != null) {
                        infoList.add(userPersonalInfo)
                    }

                    return Promise.pure(null)
            }
        }
    }

    Promise<UserPersonalInfo> decryptUserPersonalInfo(UserId userId, String message) {
        CryptoMessage cryptoMessage = new CryptoMessage()
        cryptoMessage.value = message
        return cryptoResource.decrypt(userId, cryptoMessage).then { CryptoMessage decryptValue ->
            return Promise.pure(marshaller.unmarshall(decryptValue.value, UserPersonalInfo))
        }
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset) {
        return searchByUserId(userId, limit, offset).then { List<UserPersonalInfo> userPersonalInfoList ->
            if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                return Promise.pure(null)
            }
            userPersonalInfoList.removeAll { UserPersonalInfo userPersonalInfo ->
                return userPersonalInfo.type != type
            }

            return Promise.pure(userPersonalInfoList)
        }
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByEmail(String email, Integer limit, Integer offset) {
        PiiHash hash = getPiiHash(UserPersonalInfoType.EMAIL.toString())

        return encryptUserPersonalInfoRepository.searchByHashValue(hash.generateHash(email.toLowerCase(Locale.ENGLISH))).then {
            List<EncryptUserPersonalInfo> userPersonalInfos ->
                if (CollectionUtils.isEmpty(userPersonalInfos)) {
                    return Promise.pure(null)
                }

                List<UserPersonalInfo> infos = new ArrayList<>()
                return Promise.each(userPersonalInfos) { EncryptUserPersonalInfo personalInfo ->
                    return get(personalInfo.userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo == null ||
                            userPersonalInfo.type != UserPersonalInfoType.EMAIL.toString()) {
                            return Promise.pure(null)
                        }

                        Email emailObj = (Email)JsonHelper.jsonNodeToObj(userPersonalInfo.value, Email)
                        if (emailObj.info.toLowerCase(Locale.ENGLISH) == email.toLowerCase(Locale.ENGLISH)) {
                            infos.add(userPersonalInfo)
                        }
                        return Promise.pure(null)
                    }
                }.then {
                    return Promise.pure(infos)
                }
        }
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByPhoneNumber(String phoneNumber, Integer limit, Integer offset) {
        PiiHash hash = getPiiHash(UserPersonalInfoType.PHONE.toString())

        return encryptUserPersonalInfoRepository.searchByHashValue(hash.generateHash(phoneNumber)).then {
            List<EncryptUserPersonalInfo> userPersonalInfos ->
                if (CollectionUtils.isEmpty(userPersonalInfos)) {
                    return Promise.pure(null)
                }

                List<UserPersonalInfo> infos = new ArrayList<>()
                return Promise.each(userPersonalInfos) { EncryptUserPersonalInfo personalInfo ->
                    return get(personalInfo.userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo == null ||
                            userPersonalInfo.type != UserPersonalInfoType.PHONE.toString()) {
                            return Promise.pure(null)
                        }

                        PhoneNumber phoneObj = (PhoneNumber)JsonHelper.jsonNodeToObj(userPersonalInfo.value,
                                PhoneNumber)
                        if (phoneObj.info == phoneNumber) {
                            infos.add(userPersonalInfo)
                        }
                        return Promise.pure(null)
                    }
                }.then {
                    return Promise.pure(infos)
                }
        }
    }

    @Override
    Promise<UserPersonalInfo> create(UserPersonalInfo model) {
        if (model.id == null) {
            model.id = new UserPersonalInfoId(idGenerator.nextId(model.userId.value))
        }

        CryptoMessage cryptoMessage = new CryptoMessage()
        cryptoMessage.value = marshaller.marshall(model)

        return cryptoResource.encrypt(model.userId, cryptoMessage).then { CryptoMessage messageValue ->
            EncryptUserPersonalInfo encryptUserPersonalInfo = new EncryptUserPersonalInfo()
            encryptUserPersonalInfo.encryptUserPersonalInfo = messageValue.value
            encryptUserPersonalInfo.userPersonalInfoId = (UserPersonalInfoId)model.id
            PiiHash piiHash = getPiiHash(model.type)
            encryptUserPersonalInfo.hashSearchInfo = piiHash.generateHash(model.value)

            UserPersonalInfoIdToUserIdLink link = new UserPersonalInfoIdToUserIdLink(
                    userId: model.userId,
                    userPersonalInfoId: (UserPersonalInfoId)model.id
            )

            return userIdLinkRepository.create(link).then {
                return encryptUserPersonalInfoRepository.create(encryptUserPersonalInfo).then {
                    EncryptUserPersonalInfo info ->
                        return get(info.userPersonalInfoId)
                }
            }
        }
    }

    @Override
    Promise<UserPersonalInfo> update(UserPersonalInfo model) {
        CryptoMessage cryptoMessage = new CryptoMessage()
        cryptoMessage.value = marshaller.marshall(model)

        return cryptoResource.encrypt(model.userId, cryptoMessage).then { CryptoMessage messageValue ->

            return encryptUserPersonalInfoRepository.searchByUserPersonalInfoId((UserPersonalInfoId)model.id).then {
                EncryptUserPersonalInfo info ->

                    PiiHash piiHash = getPiiHash(model.type)
                    info.hashSearchInfo = piiHash.generateHash(model.value)
                    info.encryptUserPersonalInfo = messageValue.value

                    return encryptUserPersonalInfoRepository.update(info).then { EncryptUserPersonalInfo updateInfo ->
                            return get(updateInfo.userPersonalInfoId)
                    }
            }
        }
    }

    @Override
    Promise<UserPersonalInfo> get(UserPersonalInfoId id) {
        return encryptUserPersonalInfoRepository.searchByUserPersonalInfoId(id).then {
            EncryptUserPersonalInfo encryptUserPersonalInfo ->

                if (encryptUserPersonalInfo == null) {
                    return Promise.pure(null)
                }

                return userIdLinkRepository.searchByUserPersonalInfoId(id).then { UserPersonalInfoIdToUserIdLink link ->
                    CryptoMessage cryptoMessage = new CryptoMessage(
                            value: encryptUserPersonalInfo.encryptUserPersonalInfo
                    )
                    return cryptoResource.decrypt(link.userId, cryptoMessage).then { CryptoMessage decrypt ->
                        UserPersonalInfo userPersonalInfo = marshaller.unmarshall(decrypt.value, UserPersonalInfo)
                        userPersonalInfo.createdBy = encryptUserPersonalInfo.createdBy
                        userPersonalInfo.createdTime = encryptUserPersonalInfo.createdTime
                        userPersonalInfo.updatedBy = encryptUserPersonalInfo.updatedBy
                        userPersonalInfo.updatedTime = encryptUserPersonalInfo.updatedTime
                        userPersonalInfo.resourceAge = encryptUserPersonalInfo.resourceAge
                        return Promise.pure(userPersonalInfo)
                    }
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
        return searchCandidatesByUserIdAndType(userId, type).then { List<UserPersonalInfo> userPersonalInfoList ->
            if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                return Promise.pure(null)
            }

            userPersonalInfoList.retainAll { UserPersonalInfo userPersonalInfo ->
                return ((userPersonalInfo.lastValidateTime != null && isValidated)
                    || (userPersonalInfo.lastValidateTime == null && !isValidated))
            }

            List<UserPersonalInfo> list = new ArrayList<>()
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
    void setCryptoResource(CryptoResource cryptoResource) {
        this.cryptoResource = cryptoResource
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
