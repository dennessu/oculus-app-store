package com.junbo.store.rest.utils

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.error.AppErrors
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
        boolean isDefault = personalInfo.isDefault != null ? personalInfo.isDefault : false
        return resourceContainer.userPersonalInfoResource.create(new UserPersonalInfo(
                userId: userId, type: type, value: personalInfo.value
        )).then { UserPersonalInfo added ->
            linkToAdd << new UserPersonalInfoLink(value: added.getId(), isDefault: isDefault)
            boolean hasDefault = linkToAdd.any { UserPersonalInfoLink link -> link.isDefault }
            if (personalInfo.isDefault || !hasDefault) {
                setDefaultUserPersonalInfo(linkToAdd, added.getId())
            }
            return Promise.pure(null)
        }
    }

    public Promise<User> getActiveUserFromToken() {
        UserId userId = AuthorizeContext.currentUserId
        return resourceContainer.userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user.isAnonymous || user.status != Constants.UserStatus.ACTIVE) {
                throw AppErrors.INSTANCE.invalidUserStatus().exception()
            }

            return Promise.pure(user)
        }
    }

    public Promise<User> getVerifiedUserFromToken() {
        return getActiveUserFromToken().then { User user ->
            if (CollectionUtils.isEmpty(user.emails)) {
                throw AppErrors.INSTANCE.userEmailNotFound().exception()
            }

            UserPersonalInfoLink link = user.emails.find { UserPersonalInfoLink infoLink ->
                return infoLink.isDefault
            }

            if (link == null) {
                throw AppErrors.INSTANCE.userEmailPrimaryEmailNotFound().exception()
            }

            return resourceContainer.userPersonalInfoResource.get(link.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo pii ->
                if (!pii.isValidated) {
                    throw AppErrors.INSTANCE.userPrimaryEmailNotVerified().exception()
                }

                return Promise.pure(user)
            }
        }
    }

    public void setDefaultUserPersonalInfo(User user, UserPersonalInfoId id) {
        assert user.addresses != null && user.emails != null && user.phones != null
        List<UserPersonalInfoLink> list = findLinksByPersonalInfoId(user, id)
        if (list == null) {
            return
        }
        setDefaultUserPersonalInfo(list, id)
    }

    public void setDefaultUserPersonalInfo(List<UserPersonalInfoLink> list , UserPersonalInfoId id) {
        list.each { UserPersonalInfoLink link ->
            link.isDefault = link.value == id
        }
    }

    public void removeFromUserPersonalInfoLinkList(User user, UserPersonalInfoId id, boolean setDefault) {
        assert user.addresses != null && user.emails != null && user.phones != null
        List<UserPersonalInfoLink> list = findLinksByPersonalInfoId(user, id)
        if (list == null) {
            return
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
    }

    private List<UserPersonalInfoLink> findLinksByPersonalInfoId(User user, UserPersonalInfoId personalInfoId) {
        List<List<UserPersonalInfoLink>> links = [user.addresses, user.emails, user.phones]
        return links.find { List<UserPersonalInfoLink> it ->
            return it?.find { UserPersonalInfoLink link ->
                return link.value == personalInfoId
            } != null
        }
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
