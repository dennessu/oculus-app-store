package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserPersonalInfoFilter
import com.junbo.identity.core.service.validator.UserPersonalInfoValidator
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/26/14.
 */
@Transactional
@CompileStatic
class UserPersonalInfoResourceImpl implements UserPersonalInfoResource {


    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserPersonalInfoRepository userPersonalInfoRepository

    @Autowired
    private UserPersonalInfoValidator userPersonalInfoValidator

    @Autowired
    private UserPersonalInfoFilter userPersonalInfoFilter

    @Override
    Promise<UserPersonalInfo> create(UserPersonalInfo userPii) {
        if (userPii == null) {
            throw new IllegalArgumentException('userPii is null')
        }

        userPii = userPersonalInfoFilter.filterForCreate(userPii)

        return userPersonalInfoValidator.validateForCreate(userPii).then {
            userPersonalInfoRepository.create(userPii).then { UserPersonalInfo newUserPii ->
                created201Marker.mark((Id) newUserPii.id)

                newUserPii = userPersonalInfoFilter.filterForGet(newUserPii, null)
                return Promise.pure(newUserPii)
            }
        }
    }

    @Override
    Promise<UserPersonalInfo> get(UserPersonalInfoId userPiiId, UserPersonalInfoGetOptions getOptions) {
        if (userPiiId == null) {
            throw new IllegalArgumentException('userPiiId is null')
        }

        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userPersonalInfoValidator.validateForGet(userPiiId).then { UserPersonalInfo userPii ->
            userPii = userPersonalInfoFilter.filterForGet(userPii, getOptions.properties?.split(',') as List<String>)

            return Promise.pure(userPii)
        }
    }

    @Override
    Promise<UserPersonalInfo> patch(UserPersonalInfoId userPiiId, UserPersonalInfo userPersonalInfo) {
        if (userPiiId == null) {
            throw new IllegalArgumentException('userPiiId is null')
        }

        if (userPersonalInfo == null) {
            throw new IllegalArgumentException('userPersonalInfo is null')
        }

        return userPersonalInfoRepository.get(userPiiId).then { UserPersonalInfo oldUserPersonalInfo ->
            if (oldUserPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPiiId).exception()
            }

            userPersonalInfo = userPersonalInfoFilter.filterForPatch(userPersonalInfo, oldUserPersonalInfo)

            return userPersonalInfoValidator.validateForUpdate(userPersonalInfo, oldUserPersonalInfo).then {
                return userPersonalInfoRepository.update(userPersonalInfo).then { UserPersonalInfo newUserPii ->
                    newUserPii = userPersonalInfoFilter.filterForGet(newUserPii, null)
                    return Promise.pure(newUserPii)
                }
            }
        }
    }

    @Override
    Promise<UserPersonalInfo> put(UserPersonalInfoId userPiiId, UserPersonalInfo userPii) {
        if (userPiiId == null) {
            throw new IllegalArgumentException('userPiiId is null')
        }

        if (userPii == null) {
            throw new IllegalArgumentException('userPii is null')
        }

        return userPersonalInfoRepository.get(userPiiId).then { UserPersonalInfo oldUserPersonalInfo ->
            if (oldUserPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPiiId).exception()
            }

            userPii = userPersonalInfoFilter.filterForPut(userPii, oldUserPersonalInfo)

            return userPersonalInfoValidator.validateForUpdate(userPii, oldUserPersonalInfo).then {
                userPersonalInfoRepository.update(userPii).then { UserPersonalInfo newUserPersonalInfo ->
                    newUserPersonalInfo = userPersonalInfoFilter.filterForGet(newUserPersonalInfo, null)
                    return Promise.pure(newUserPersonalInfo)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserPersonalInfoId userPiiId) {
        if (userPiiId == null) {
            throw new IllegalArgumentException('userPiiId is null')
        }

        return userPersonalInfoValidator.validateForGet(userPiiId).then {
            userPersonalInfoRepository.delete(userPiiId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Results<UserPersonalInfo>> list(UserPersonalInfoListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return userPersonalInfoValidator.validateForSearch(listOptions).then {
            def resultList = new Results<UserPersonalInfo>(items: [])

            if (listOptions.userId != null && listOptions.type != null) {
                userPersonalInfoRepository.searchByUserIdAndType(listOptions.userId, listOptions.type).
                    then { List<UserPersonalInfo> userPersonalInfoList ->
                        userPersonalInfoList.each { UserPersonalInfo temp ->
                            temp = userPersonalInfoFilter.filterForGet(temp,
                                    listOptions.properties?.split(',') as List<String>)

                            if (temp != null) {
                                resultList.items.add(temp)
                            }
                        }
                    }
            } else {
                userPersonalInfoRepository.searchByUserId(listOptions.userId).
                    then { List<UserPersonalInfo> userPersonalInfoList ->
                        userPersonalInfoList.each { UserPersonalInfo temp ->
                            temp = userPersonalInfoFilter.filterForGet(temp,
                                    listOptions.properties?.split(',') as List<String>)

                            if (temp != null) {
                                resultList.items.add(temp)
                            }
                        }
                    }
            }

            return Promise.pure(resultList)
        }
    }
}
