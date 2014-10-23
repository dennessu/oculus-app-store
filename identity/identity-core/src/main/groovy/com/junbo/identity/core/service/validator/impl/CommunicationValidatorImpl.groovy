package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.CommunicationId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.validator.CommunicationValidator
import com.junbo.identity.service.CommunicationService
import com.junbo.identity.service.CountryService
import com.junbo.identity.service.LocaleService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.model.CommunicationLocale
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 *  1): Locale & country existence check;
 *  2): LocaleName length check;
 *  3): LocaleDescription length check;
 *  Created by haomin on 14-4-25.
 */
@CompileStatic
class CommunicationValidatorImpl implements CommunicationValidator {

    private static final String DEFAULT_LOCALE = 'en_US'

    private CommunicationService communicationService
    private LocaleService localeService
    private CountryService countryService

    private Integer minCommunicationLocaleName
    private Integer maxCommunicationLocaleName

    private Integer minCommunicationLocaleDescription
    private Integer maxCommunicationLocaleDescription

    @Required
    void setCommunicationService(CommunicationService communicationService) {
        this.communicationService = communicationService
    }

    @Required
    void setLocaleService(LocaleService localeService) {
        this.localeService = localeService
    }

    @Required
    void setCountryService(CountryService countryService) {
        this.countryService = countryService
    }

    @Required
    void setMinCommunicationLocaleName(Integer minCommunicationLocaleName) {
        this.minCommunicationLocaleName = minCommunicationLocaleName
    }

    @Required
    void setMaxCommunicationLocaleName(Integer maxCommunicationLocaleName) {
        this.maxCommunicationLocaleName = maxCommunicationLocaleName
    }

    @Required
    void setMinCommunicationLocaleDescription(Integer minCommunicationLocaleDescription) {
        this.minCommunicationLocaleDescription = minCommunicationLocaleDescription
    }

    @Required
    void setMaxCommunicationLocaleDescription(Integer maxCommunicationLocaleDescription) {
        this.maxCommunicationLocaleDescription = maxCommunicationLocaleDescription
    }

    @Override
    Promise<Communication> validateForGet(CommunicationId communicationId) {
        if (communicationId == null || communicationId.value == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        return communicationService.get(communicationId).then { Communication communication ->
            if (communication == null) {
                throw AppErrors.INSTANCE.communicationNotFound(communicationId).exception()
            }

            return Promise.pure(communication)
        }
    }

    @Override
    Promise<Void> validateForSearch(CommunicationListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Communication communication) {
        return checkBasicCommunicationInfo(communication).then {
            if (communication.id != null) {
                throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
            }

            JsonNode inputJsonNode = communication.locales.get(DEFAULT_LOCALE)
            if (inputJsonNode == null) {
                return Promise.pure(null)
            }

            CommunicationLocale inputLocale = (CommunicationLocale)JsonHelper.jsonNodeToObj(inputJsonNode, CommunicationLocale)

            return Promise.each(communication.regions) { CountryId region ->
                return communicationService.searchByRegion(region, Integer.MAX_VALUE, 0).then { List<Communication> communicationList ->
                    if (CollectionUtils.isEmpty(communicationList)) {
                        return Promise.pure(null)
                    }

                    for (Communication existing : communicationList) {
                        String name = getDefaultLocaleName(existing)
                        if (name.equalsIgnoreCase(inputLocale.name)) {
                            throw AppCommonErrors.INSTANCE.fieldInvalid('regions', 'communication have overlap region support for same title').exception()
                        }
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(CommunicationId communicationId, Communication communication, Communication oldCommunication) {
        if (communicationId == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        if (communicationId != communication.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (communicationId != oldCommunication.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        return checkBasicCommunicationInfo(communication).then {
            JsonNode updateJsonNode = communication.locales.get(DEFAULT_LOCALE)
            if (updateJsonNode == null) {
                return Promise.pure(null)
            }

            CommunicationLocale inputCommunicationLocale = (CommunicationLocale)JsonHelper.jsonNodeToObj(updateJsonNode, CommunicationLocale)
            return Promise.each(communication.regions) { CountryId region ->
                return communicationService.searchByRegion(region, Integer.MAX_VALUE, 0).then { List<Communication> communicationList ->
                    if (CollectionUtils.isEmpty(communicationList)) {
                        return Promise.pure(null)
                    }

                    for (Communication existing : communicationList) {
                        if (existing.getId() == communication.getId()) {
                            continue
                        }
                        String name = getDefaultLocaleName(existing)
                        if (name.equalsIgnoreCase(inputCommunicationLocale.name)) {
                            throw AppCommonErrors.INSTANCE.fieldInvalid('regions', 'communication have overlap region support for same title').exception()
                        }
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(null)
            }
        }
    }

    private Promise<Void> checkBasicCommunicationInfo(Communication communication) {
        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        if (CollectionUtils.isEmpty(communication.regions)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('regions').exception()
        }
        if (CollectionUtils.isEmpty(communication.translations)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('translations').exception()
        }
        if (communication.locales == null || communication.locales.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldRequired('locales').exception()
        }

        return checkRegions(communication).then {
            return checkTranslations(communication)
        }.then {
            communication.locales.each { Map.Entry<String, JsonNode> entry ->
                if (!ValidatorUtil.isValidLocale(entry.key)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('value.key').exception()
                }

                CommunicationLocale locale = (CommunicationLocale)JsonHelper.jsonNodeToObj(entry.value,
                        CommunicationLocale)
                if (locale.name == null) {
                    throw AppCommonErrors.INSTANCE.fieldRequired('value.name').exception()
                }
                if (locale.name.length() > maxCommunicationLocaleName) {
                    throw AppCommonErrors.INSTANCE.fieldTooLong('value.name', maxCommunicationLocaleName).exception()
                }
                if (locale.name.length() < minCommunicationLocaleName) {
                    throw AppCommonErrors.INSTANCE.fieldTooShort('value.name', minCommunicationLocaleName).exception()
                }

                if (locale.description == null) {
                    throw AppCommonErrors.INSTANCE.fieldRequired('value.description').exception()
                }
                if (locale.description.length() > maxCommunicationLocaleDescription) {
                    throw AppCommonErrors.INSTANCE.fieldTooLong('value.description', maxCommunicationLocaleDescription).
                            exception()
                }
                if (locale.description.length() < minCommunicationLocaleName) {
                    throw AppCommonErrors.INSTANCE.fieldTooShort('value.description', minCommunicationLocaleDescription).
                            exception()
                }

                return Promise.pure(null)
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> checkRegions(Communication communication) {
        if (CollectionUtils.isEmpty(communication.regions)) {
            return Promise.pure(null)
        }

        Collection<CountryId> countryIdList = communication.regions.unique { CountryId countryId ->
            return countryId
        }

        if (countryIdList.size() != communication.regions.size()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('regions').exception()
        }

        Promise.each(communication.regions) { CountryId countryId ->
            return countryService.get(countryId).then { Country country ->
                if (country == null) {
                    throw AppErrors.INSTANCE.countryNotFound(countryId).exception()
                }

                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(null)
        }
    }

    private Promise<Void> checkTranslations(Communication communication) {
        if (CollectionUtils.isEmpty(communication.translations)) {
            return Promise.pure(null)
        }

        Collection<LocaleId> localeIdList = communication.translations.unique { LocaleId localeId ->
            return localeId
        }

        if (localeIdList.size() != communication.translations.size()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('translations').exception()
        }

        Promise.each(communication.translations) { LocaleId localeId ->
            return localeService.get(localeId).then { com.junbo.identity.spec.v1.model.Locale locale ->
                if (locale == null) {
                    throw AppErrors.INSTANCE.localeNotFound(localeId).exception()
                }

                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(null)
        }
    }

    private String getDefaultLocaleName(Communication communication) {
        if (communication == null || communication.locales == null) {
            return ''
        }
        JsonNode jsonNode = communication.getLocales().get(DEFAULT_LOCALE)
        if (jsonNode == null) {
            return ''
        }

        CommunicationLocale communicationLocale = (CommunicationLocale)JsonHelper.jsonNodeToObj(jsonNode, CommunicationLocale)
        return communicationLocale.name
    }
}
