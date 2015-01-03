package com.junbo.store.rest.utils

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.identity.PersonalInfo
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Value
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

    @Value('${store.conf.tosChallengeDefaultLocale}')
    private String tosDefaultLocale

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    Promise<UserPersonalInfo> createPhoneInfoIfNotExist(UserId userId, PersonalInfo phoneInfo) {
        PhoneNumber updatePhone = ObjectMapperProvider.instance().treeToValue(phoneInfo.value, PhoneNumber)
        return resourceContainer.userPersonalInfoResource.list(new UserPersonalInfoListOptions(type: 'PHONE', phoneNumber: updatePhone.info)).then { Results<UserPersonalInfo> results ->
            UserPersonalInfo result = results.items.find {UserPersonalInfo e -> e.userId == userId}
            if (result == null) {
                return resourceContainer.userUserPersonalInfoResource.create(
                        new UserPersonalInfo(userId: userId, type: 'PHONE', value: phoneInfo.value)
                )
            } else {
                return Promise.pure(result)
            }
        }.syncThen { UserPersonalInfo userPersonalInfo ->
            return dataConverter.toStorePersonalInfo(userPersonalInfo, null)
        }
    }

    public Promise<User> getActiveUserFromToken() {
        UserId userId = AuthorizeContext.currentUserId
        return resourceContainer.userUserResource.get(userId, new UserGetOptions()).then { User user ->
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

            return resourceContainer.userUserPersonalInfoResource.get(link.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo pii ->
                if (!pii.isValidated) {
                    throw AppErrors.INSTANCE.userPrimaryEmailNotVerified().exception()
                }

                return Promise.pure(user)
            }
        }
    }

    public Promise<String> getVerifiedUserPrimaryMail(User user) {
        if (CollectionUtils.isEmpty(user.emails)) {
            throw AppErrors.INSTANCE.userEmailNotFound().exception()
        }

        UserPersonalInfoLink link = user.emails.find { UserPersonalInfoLink infoLink ->
            return infoLink.isDefault
        }

        if (link == null) {
            throw AppErrors.INSTANCE.userEmailPrimaryEmailNotFound().exception()
        }

        return resourceContainer.userUserPersonalInfoResource.get(link.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo pii ->
            if (!pii.isValidated) {
                throw AppErrors.INSTANCE.userPrimaryEmailNotVerified().exception()
            }

            Email email = ObjectMapperProvider.instance().treeToValue(pii.value, Email)
            return Promise.pure(email.info)
        }
    }

    public Promise<com.junbo.store.spec.model.browse.document.Tos> lookupTos(String title, String type,
                                                                             LocaleId localeId, CountryId countryId) {
        return resourceContainer.tosResource.list(new TosListOptions(title: title, countryId: countryId,
                state: 'APPROVED', type: type)).recover { Throwable ex ->
            appErrorUtils.throwUnknownError('lookupTos', ex)
        }.then { Results<Tos> tosResults ->
            if (tosResults == null || org.springframework.util.CollectionUtils.isEmpty(tosResults.items)) {
                return Promise.pure(null)
            }

            Tos latestTos = tosResults.items.max { Tos tos ->
                try {
                    return Double.parseDouble(tos.version)
                } catch (NumberFormatException ex) {
                    return Double.valueOf(0)
                }
            }

            List<Tos> tosList = tosResults.items.findAll { Tos item ->
                try {
                    Double current = Double.parseDouble(item.version)
                    Double latest = Double.parseDouble(latestTos.version)

                    return current == latest
                } catch (NumberFormatException ex) {
                    return false
                }
            }?.asList()

            if (org.springframework.util.CollectionUtils.isEmpty(tosList)) {
                return Promise.pure(null)
            }
            LocaleId locale = getLocale(null, localeId, null)
            Tos tos = getSupportedTos(tosList, locale)
            if (tos == null) {
                return Promise.pure(null)
            }
            return Promise.pure(dataConverter.toStoreTos(tos, null))
        }
    }

    private LocaleId getLocale(User user, LocaleId localeId, CountryId countryId) {
        if (user != null && user.preferredLocale != null) {
            return user.preferredLocale
        }

        if (localeId != null) {
            return localeId
        }

        if (countryId != null) {
            Country country = resourceContainer.countryResource.get(countryId, new CountryGetOptions()).get()
            if (country != null && country.defaultLocale != null) {
                return country.defaultLocale
            }
        }

        return new LocaleId(tosDefaultLocale)
    }

    private Tos getSupportedTos(List<Tos> tosList, LocaleId localeId) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(tosList) || localeId == null) {
            return null
        }

        Map<String, Boolean> circle = new HashMap<>()

        LocaleId current = localeId
        LocaleId fallback = null
        while (true) {
            com.junbo.identity.spec.v1.model.Locale locale = resourceContainer.localeResource.get(current, new LocaleGetOptions()).get()
            Boolean visited = circle.get(locale.getId().toString())
            if (visited) {
                break
            }
            circle.put(locale.getId().toString(), true)
            for (Tos tos : tosList) {
                if (!org.springframework.util.CollectionUtils.isEmpty(tos.locales)) {
                    if (tos.locales.any { LocaleId tosLocaleId ->
                        return tosLocaleId == current
                    }) {
                        return tos
                    }
                }
            }

            fallback = locale.fallbackLocale
            if (current == fallback || fallback == null) {
                break
            }
            current = fallback
        }

        return null
    }
}
