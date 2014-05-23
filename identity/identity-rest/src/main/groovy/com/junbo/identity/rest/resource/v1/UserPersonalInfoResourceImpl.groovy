package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.id.Id
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPersonalInfoAuthorizeCallbackFactory
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
    private UserPersonalInfoRepository userPersonalInfoRepository

    @Autowired
    private UserPersonalInfoValidator userPersonalInfoValidator

    @Autowired
    private UserPersonalInfoFilter userPersonalInfoFilter

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPersonalInfoAuthorizeCallbackFactory userPersonalInfoAuthorizeCallbackFactory

    @Override
    Promise<UserPersonalInfo> create(UserPersonalInfo userPii) {
        if (userPii == null) {
            throw new IllegalArgumentException('userPii is null')
        }

        def callback = userPersonalInfoAuthorizeCallbackFactory.create(userPii)
        return RightsScope.with(authorizeService.authorize(callback)) {
            userPii = userPersonalInfoFilter.filterForCreate(userPii)

            return userPersonalInfoValidator.validateForCreate(userPii).then {
                return userPersonalInfoRepository.create(userPii).then { UserPersonalInfo newUserPii ->
                    Created201Marker.mark((Id) newUserPii.id)

                    newUserPii = userPersonalInfoFilter.filterForGet(newUserPii, null)
                    return Promise.pure(newUserPii)
                }
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
            def callback = userPersonalInfoAuthorizeCallbackFactory.create(userPii)
            return RightsScope.with(authorizeService.authorize(callback)) {

                userPii = userPersonalInfoFilter.filterForGet(userPii, getOptions.properties?.split(',') as List)
                return Promise.pure(userPii)
            }
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

            def callback = userPersonalInfoAuthorizeCallbackFactory.create(oldUserPersonalInfo)
            return RightsScope.with(authorizeService.authorize(callback)) {
                userPersonalInfo = userPersonalInfoFilter.filterForPatch(userPersonalInfo, oldUserPersonalInfo)

                return userPersonalInfoValidator.validateForUpdate(userPersonalInfo, oldUserPersonalInfo).then {
                    return userPersonalInfoRepository.update(userPersonalInfo).then { UserPersonalInfo newUserPii ->
                        newUserPii = userPersonalInfoFilter.filterForGet(newUserPii, null)
                        return Promise.pure(newUserPii)
                    }
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

            def callback = userPersonalInfoAuthorizeCallbackFactory.create(oldUserPersonalInfo)
            return RightsScope.with(authorizeService.authorize(callback)) {
                userPii = userPersonalInfoFilter.filterForPut(userPii, oldUserPersonalInfo)

                return userPersonalInfoValidator.validateForUpdate(userPii, oldUserPersonalInfo).then {
                    return userPersonalInfoRepository.update(userPii).then { UserPersonalInfo newUserPersonalInfo ->
                        newUserPersonalInfo = userPersonalInfoFilter.filterForGet(newUserPersonalInfo, null)
                        return Promise.pure(newUserPersonalInfo)
                    }
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserPersonalInfoId userPiiId) {
        if (userPiiId == null) {
            throw new IllegalArgumentException('userPiiId is null')
        }

        return userPersonalInfoValidator.validateForGet(userPiiId).then { UserPersonalInfo userPii ->
            def callback = userPersonalInfoAuthorizeCallbackFactory.create(userPii)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppErrors.INSTANCE.invalidAccess().exception()
                }

                return userPersonalInfoRepository.delete(userPiiId)
            }
        }
    }

    @Override
    Promise<Results<UserPersonalInfo>> list(UserPersonalInfoListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        def filterUserPersonalInfos = { List<UserPersonalInfo> userPersonalInfoList ->
            def resultList = new Results<UserPersonalInfo>(items: [])

            return Promise.each(userPersonalInfoList) { UserPersonalInfo userPersonalInfo ->
                def callback = userPersonalInfoAuthorizeCallbackFactory.create(userPersonalInfo)
                return RightsScope.with(authorizeService.authorize(callback)) {
                    userPersonalInfo = userPersonalInfoFilter.filterForGet(userPersonalInfo,
                            listOptions.properties?.split(',') as List<String>)

                    if (userPersonalInfo != null) {
                        resultList.items.add(userPersonalInfo)
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(resultList)
            }
        }

        return userPersonalInfoValidator.validateForSearch(listOptions).then {
            if (listOptions.userId != null && listOptions.type != null) {
                return userPersonalInfoRepository.searchByUserIdAndType(listOptions.userId, listOptions.type).
                        then(filterUserPersonalInfos)
            } else if (listOptions.userId != null) {
                return userPersonalInfoRepository.searchByUserId(listOptions.userId).
                        then(filterUserPersonalInfos)
            } else if (listOptions.email != null) {
                return userPersonalInfoRepository.searchByEmail(listOptions.email).
                        then(filterUserPersonalInfos)
            } else {
                return userPersonalInfoRepository.searchByPhoneNumber(listOptions.phoneNumber).
                        then(filterUserPersonalInfos)
            }
        }
    }
}
