package com.junbo.store.rest.utils

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.PhoneNumber
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.identity.PersonalInfo
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The IdentityUtils class.
 */
@CompileStatic
@Component('storeIdentityUtils')
class IdentityUtils {

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = "storeDataConverter")
    private DataConverter dataConverter

    Promise<PersonalInfo> createEmailInfoIfNotExist(UserId userId, PersonalInfo emailInfo) {
        Email updateEmail = ObjectMapperProvider.instance().treeToValue(emailInfo.value, Email)
        return resourceContainer.userPersonalInfoResource.list(new UserPersonalInfoListOptions(type: 'EMAIL', email: updateEmail.info)).then { Results<UserPersonalInfo> results ->
            UserPersonalInfo result = results.items.find {UserPersonalInfo e -> e.userId == userId}
            if (result == null) {
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(userId: userId, type: 'EMAIL', value: emailInfo.value)
                )
            } else {
                return Promise.pure(result)
            }
        }.syncThen { UserPersonalInfo userPersonalInfo ->
            return dataConverter.toStorePersonalInfo(userPersonalInfo, null)
        }
    }

    Promise<UserPersonalInfo> createPhoneInfoIfNotExist(UserId userId, PersonalInfo phoneInfo) {
        PhoneNumber updatePhone = ObjectMapperProvider.instance().treeToValue(phoneInfo.value, PhoneNumber)
        return resourceContainer.userPersonalInfoResource.list(new UserPersonalInfoListOptions(type: 'PHONE', phoneNumber: updatePhone.info)).then { Results<UserPersonalInfo> results ->
            UserPersonalInfo result = results.items.find {UserPersonalInfo e -> e.userId == userId}
            if (result == null) {
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(userId: userId, type: 'PHONE', value: phoneInfo.value)
                )
            } else {
                return Promise.pure(result)
            }
        }.syncThen { UserPersonalInfo userPersonalInfo ->
            return dataConverter.toStorePersonalInfo(userPersonalInfo, null)
        }
    }

    public Promise addPersonalInfo(UserId userId, String type, PersonalInfo personalInfo, List<UserPersonalInfoLink> linkToAdd) {
        return resourceContainer.userPersonalInfoResource.create(new UserPersonalInfo(
                userId: userId, type: type, value: personalInfo.value
        )).then { UserPersonalInfo added ->
            linkToAdd << new UserPersonalInfoLink(value: added.getId(), isDefault: personalInfo.isDefault)
            boolean hasDefault = linkToAdd.any { UserPersonalInfoLink link -> link.isDefault }
            if (personalInfo.isDefault || !hasDefault) {
                setDefaultUserPersonalInfo(linkToAdd, added.getId())
            }
            return Promise.pure(null)
        }
    }

    public void setDefaultUserPersonalInfo(List<UserPersonalInfoLink> list, UserPersonalInfoId id) {
        list.each { UserPersonalInfoLink link ->
            link.isDefault = link.value == id
        }
    }

    public List<UserPersonalInfoLink> removeFromUserPersonalInfoLinkList(List<UserPersonalInfoLink> list, UserPersonalInfoId id, boolean setDefault) {
        if (CollectionUtils.isEmpty(list)) {
            return list
        }
        ensureUnique(list)

        boolean isDefault = false
        list.removeAll { UserPersonalInfoLink link ->
            if (link.value == id) {
                isDefault = link.isDefault
                return true
            }
            return false
        }

        if (isDefault && setDefault && !list.isEmpty()) {
            list[0].isDefault = true
        }
        return list
    }

    Promise<List<PersonalInfo>> expandPersonalInfo(List<UserPersonalInfoLink> userPersonalInfoLinkList) {
        List<PersonalInfo> result = []
        if (org.springframework.util.CollectionUtils.isEmpty(userPersonalInfoLinkList)) {
            return Promise.pure(result)
        }
        return Promise.each(userPersonalInfoLinkList) { UserPersonalInfoLink link ->
            resourceContainer.userPersonalInfoResource.get(link.value, new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo personalInfo ->
                result << dataConverter.toStorePersonalInfo(personalInfo, link)
            }
        }.syncThen {
            return result
        }
    }

    private void ensureUnique(List<UserPersonalInfoLink> list) {
        Set<UserPersonalInfoId> set = [] as Set
        set.addAll(list.collect {UserPersonalInfoLink link -> link.value})
        assert set.size() == list.size() : 'duplicates found in user personal info'
    }
}
