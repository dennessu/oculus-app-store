package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserPersonalInfoFilter
import com.junbo.identity.core.service.filter.pii.PIIAdvanceFilter
import com.junbo.identity.core.service.filter.pii.PIIAdvanceFilterFactory
import com.junbo.identity.core.service.validator.UserPersonalInfoValidator
import com.junbo.identity.service.UserPersonalInfoService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.core.Response

/**
 * Created by liangfu on 4/26/14.
 */
@Transactional
@CompileStatic
class UserPersonalInfoResourceImpl implements UserPersonalInfoResource {

    @Autowired
    private UserPersonalInfoService userPersonalInfoService

    @Autowired
    private UserPersonalInfoValidator userPersonalInfoValidator

    @Autowired
    private UserPersonalInfoFilter userPersonalInfoFilter

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory userPropertyAuthorizeCallbackFactory

    @Autowired
    private PIIAdvanceFilterFactory piiAdvanceFilterFactory

    @Override
    Promise<UserPersonalInfo> create(UserPersonalInfo userPii) {
        if (userPii == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        PIIAdvanceFilter piiAdvanceFilter = getCurrentPIIAdvanceFilter(userPii)
        def callback = userPropertyAuthorizeCallbackFactory.create(userPii.userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            piiAdvanceFilter.checkCreatePermission()

            userPii = userPersonalInfoFilter.filterForCreate(userPii)

            return userPersonalInfoValidator.validateForCreate(userPii).then {
                return userPersonalInfoService.create(userPii).then { UserPersonalInfo newUserPii ->
                    newUserPii.isValidated = newUserPii.lastValidateTime != null
                    Created201Marker.mark(newUserPii.getId())

                    newUserPii = userPersonalInfoFilter.filterForGet(newUserPii, null)
                    newUserPii = piiAdvanceFilter.getFilter(newUserPii)
                    return Promise.pure(newUserPii)
                }
            }
        }
    }

    @Override
    Promise<UserPersonalInfo> get(UserPersonalInfoId userPiiId, UserPersonalInfoGetOptions getOptions) {
        if (userPiiId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userPersonalInfoValidator.validateForGet(userPiiId).then { UserPersonalInfo userPii ->
            def callback = userPropertyAuthorizeCallbackFactory.create(userPii.userId)
            PIIAdvanceFilter piiAdvanceFilter = getCurrentPIIAdvanceFilter(userPii)
            return RightsScope.with(authorizeService.authorize(callback)) {
                piiAdvanceFilter.checkGetPermission(userPii.getId())

                userPii.isValidated = userPii.lastValidateTime != null
                userPii = userPersonalInfoFilter.filterForGet(userPii, getOptions.properties?.split(',') as List)
                userPii = piiAdvanceFilter.getFilter(userPii)
                return Promise.pure(userPii)
            }
        }
    }

    @Override
    Promise<UserPersonalInfo> patch(UserPersonalInfoId userPiiId, UserPersonalInfo userPersonalInfo) {
        if (userPiiId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (userPersonalInfo == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        userPersonalInfo = filterUserPersonalInfo(userPersonalInfo)

        return userPersonalInfoService.get(userPiiId).then { UserPersonalInfo oldUserPersonalInfo ->
            if (oldUserPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPiiId).exception()
            }

            def callback = userPropertyAuthorizeCallbackFactory.create(oldUserPersonalInfo.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                userPersonalInfo = userPersonalInfoFilter.filterForPatch(userPersonalInfo, oldUserPersonalInfo)
                PIIAdvanceFilter piiAdvanceFilter = getCurrentPIIAdvanceFilter(userPersonalInfo)
                piiAdvanceFilter.checkUpdatePermission()

                return userPersonalInfoValidator.validateForUpdate(userPersonalInfo, oldUserPersonalInfo).then {
                    return userPersonalInfoService.update(userPersonalInfo, oldUserPersonalInfo).then { UserPersonalInfo newUserPii ->
                        newUserPii.isValidated = newUserPii.lastValidateTime != null
                        newUserPii = userPersonalInfoFilter.filterForGet(newUserPii, null)
                        newUserPii = piiAdvanceFilter.getFilter(newUserPii)
                        return Promise.pure(newUserPii)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserPersonalInfo> put(UserPersonalInfoId userPiiId, UserPersonalInfo userPii) {
        if (userPiiId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (userPii == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        userPii = filterUserPersonalInfo(userPii)

        return userPersonalInfoService.get(userPiiId).then { UserPersonalInfo oldUserPersonalInfo ->
            if (oldUserPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPiiId).exception()
            }

            def callback = userPropertyAuthorizeCallbackFactory.create(oldUserPersonalInfo.userId)
            PIIAdvanceFilter piiAdvanceFilter = getCurrentPIIAdvanceFilter(userPii)
            return RightsScope.with(authorizeService.authorize(callback)) {
                piiAdvanceFilter.checkUpdatePermission()

                userPii = userPersonalInfoFilter.filterForPut(userPii, oldUserPersonalInfo)

                return userPersonalInfoValidator.validateForUpdate(userPii, oldUserPersonalInfo).then {
                    return userPersonalInfoService.update(userPii, oldUserPersonalInfo).then { UserPersonalInfo newUserPersonalInfo ->
                        newUserPersonalInfo.isValidated = newUserPersonalInfo.lastValidateTime != null
                        newUserPersonalInfo = userPersonalInfoFilter.filterForGet(newUserPersonalInfo, null)
                        newUserPersonalInfo = piiAdvanceFilter.getFilter(newUserPersonalInfo)
                        return Promise.pure(newUserPersonalInfo)
                    }
                }
            }
        }
    }

    @Override
    Promise<Response> delete(UserPersonalInfoId userPiiId) {
        throw AppCommonErrors.INSTANCE.invalidOperation('pii operation is not supported').exception()
    }

    @Override
    Promise<Results<UserPersonalInfo>> list(UserPersonalInfoListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        def filterUserPersonalInfos = { List<UserPersonalInfo> userPersonalInfoList ->
            def resultList = new Results<UserPersonalInfo>(items: [])

            return Promise.each(userPersonalInfoList) { UserPersonalInfo userPersonalInfo ->
                def callback = userPropertyAuthorizeCallbackFactory.create(userPersonalInfo.userId)
                PIIAdvanceFilter piiAdvanceFilter = getCurrentPIIAdvanceFilter(userPersonalInfo)
                return RightsScope.with(authorizeService.authorize(callback)) {
                    userPersonalInfo.isValidated = userPersonalInfo.lastValidateTime != null
                    userPersonalInfo = userPersonalInfoFilter.filterForGet(userPersonalInfo,
                            listOptions.properties?.split(',') as List<String>)

                    if (userPersonalInfo != null && AuthorizeContext.hasRights('read')) {
                        userPersonalInfo = piiAdvanceFilter.getFilter(userPersonalInfo)
                        resultList.items.add(userPersonalInfo)
                        return Promise.pure(userPersonalInfo)
                    } else {
                        return Promise.pure(null)
                    }
                }
            }.then {
                return Promise.pure(resultList)
            }
        }

        return userPersonalInfoValidator.validateForSearch(listOptions).then {
            if (listOptions.userId != null && listOptions.isValidated != null) {
                return userPersonalInfoService.searchByUserIdAndValidateStatus(listOptions.userId, listOptions.type,
                        listOptions.isValidated, listOptions.limit, listOptions.offset).then(filterUserPersonalInfos)
            } else if  (listOptions.userId != null && listOptions.type != null) {
                return userPersonalInfoService.searchByUserIdAndType(listOptions.userId, listOptions.type,
                        listOptions.limit, listOptions.offset).then(filterUserPersonalInfos)
            } else if (listOptions.email != null) {
                return userPersonalInfoService.searchByEmail(listOptions.email, listOptions.isValidated, listOptions.limit,
                        listOptions.offset).then(filterUserPersonalInfos)
            } else if (listOptions.phoneNumber != null) {
                return userPersonalInfoService.searchByPhoneNumber(listOptions.phoneNumber, listOptions.isValidated, listOptions.limit,
                        listOptions.offset).then(filterUserPersonalInfos)
            } else if (listOptions.name != null) {
                return userPersonalInfoService.searchByName(listOptions.name, listOptions.limit,
                        listOptions.offset).then(filterUserPersonalInfos)
            } else {
                return userPersonalInfoService.searchByUserId(listOptions.userId, listOptions.limit,
                        listOptions.offset).then(filterUserPersonalInfos)
            }
        }
    }

    private UserPersonalInfo filterUserPersonalInfo(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo == null) {
            return userPersonalInfo
        }

        userPersonalInfo.isValidated = null

        return userPersonalInfo
    }

    private PIIAdvanceFilter getCurrentPIIAdvanceFilter(UserPersonalInfo userPersonalInfo) {
        List<PIIAdvanceFilter> piiAdvanceFilterList = piiAdvanceFilterFactory.getAll()

        PIIAdvanceFilter filter = piiAdvanceFilterList.find { PIIAdvanceFilter piiAdvanceFilter ->
            return piiAdvanceFilter.handles(userPersonalInfo)
        }

        if (filter == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('type').exception()
        }

        return filter
    }
}
