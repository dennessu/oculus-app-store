package com.junbo.identity.rest.resource.v1

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.CommunicationId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.filter.CommunicationFilter
import com.junbo.identity.core.service.validator.CommunicationValidator
import com.junbo.identity.data.identifiable.LocaleAccuracy
import com.junbo.identity.service.CommunicationService
import com.junbo.identity.service.LocaleService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.model.CommunicationLocale
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.identity.spec.v1.option.model.CommunicationGetOptions
import com.junbo.identity.spec.v1.resource.CommunicationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response
import java.lang.reflect.Field

/**
 * Created by liangfu on 4/24/14.
 */
@Transactional
@CompileStatic
class CommunicationResourceImpl implements CommunicationResource {
    private static Map<String, Field> fieldMap = new HashMap<String, Field>()

    @Autowired
    private CommunicationService communicationService

    @Autowired
    private CommunicationFilter communicationFilter

    @Autowired
    private CommunicationValidator communicationValidator

    @Autowired
    private LocaleService localeService

    @Override
    Promise<Communication> create(Communication communication) {
        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        communication = communicationFilter.filterForCreate(communication)

        return communicationValidator.validateForCreate(communication).then {
            return communicationService.create(communication).then { Communication newCommunication ->
                Created201Marker.mark(newCommunication.id)

                newCommunication = communicationFilter.filterForGet(newCommunication, null)
                return Promise.pure(newCommunication)
            }
        }
    }

    @Override
    Promise<Communication> put(CommunicationId communicationId, Communication communication) {
        if (communicationId == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        return communicationService.get(communicationId).then { Communication oldCommunication ->
            if (oldCommunication == null) {
                throw AppErrors.INSTANCE.communicationNotFound(communicationId).exception()
            }

            communication = communicationFilter.filterForPut(communication, oldCommunication)

            return communicationValidator.validateForUpdate(communicationId, communication, oldCommunication).then {
                return communicationService.update(communication, oldCommunication).then { Communication newCommunication ->
                    newCommunication = communicationFilter.filterForGet(newCommunication, null)
                    return Promise.pure(newCommunication)
                }
            }
        }
    }

    @Override
    Promise<Communication> patch(CommunicationId communicationId, Communication communication) {
        if (communicationId == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        return communicationService.get(communicationId).then { Communication oldCommunication ->
            if (oldCommunication == null) {
                throw AppErrors.INSTANCE.communicationNotFound(communicationId).exception()
            }

            communication = communicationFilter.filterForPatch(communication, oldCommunication)

            return communicationValidator.validateForUpdate(communicationId, communication, oldCommunication).then {
                return communicationService.update(communication, oldCommunication).then { Communication newCommunication ->
                    newCommunication = communicationFilter.filterForGet(newCommunication, null)
                    return Promise.pure(newCommunication)
                }
            }
        }
    }

    @Override
    Promise<Communication> get(CommunicationId communicationId, CommunicationGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return communicationValidator.validateForGet(communicationId).then { Communication communication ->
            return filterCommunication(communication, getOptions).then { Communication newCommunication ->
                newCommunication = communicationFilter.filterForGet(newCommunication, null)
                return Promise.pure(newCommunication)
            }
        }
    }

    @Override
    Promise<Results<Communication>> list(CommunicationListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return communicationValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { List<Communication> communicationList ->
                def result = new Results<Communication>(items: [])

                communicationList.each { Communication newCommunication ->
                    newCommunication = communicationFilter.filterForGet(newCommunication, null)

                    if (newCommunication != null) {
                        result.items.add(newCommunication)
                    }
                }

                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<Void> delete(CommunicationId communicationId) {
        if (communicationId == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        return communicationValidator.validateForGet(communicationId).then { Communication communication ->
            return communicationService.delete(communicationId).then {
                return Promise.pure(Response.status(204).build())
            }
        }
    }

    private Promise<List<Communication>> search(CommunicationListOptions listOptions) {
        if (listOptions.region != null && listOptions.translation != null) {
            return communicationService.searchByRegionAndTranslation(listOptions.region, listOptions.translation,
                    listOptions.limit, listOptions.offset)
        } else if (listOptions.region != null) {
            return communicationService.searchByRegion(listOptions.region, listOptions.limit, listOptions.offset)
        } else if (listOptions.translation != null) {
            return communicationService.searchByTranslation(listOptions.translation, listOptions.limit, listOptions.offset)
        } else {
            return communicationService.searchAll(listOptions.limit, listOptions.offset)
        }
    }

    private Promise<Communication> filterCommunication(Communication communication, CommunicationGetOptions options) {
        if (StringUtils.isEmpty(options.locale)) {
            return Promise.pure(communication)
        }

        return filterCommunicationLocales(wrap(communication.locales), options.locale).then { Map<String, CommunicationLocale> map ->
            JsonNode node = communication.locales.get(options.locale)
            CommunicationLocale communicationLocale = null
            if (node != null) {
                communicationLocale = (CommunicationLocale) JsonHelper.jsonNodeToObj(node, CommunicationLocale)
            }
            communication.locales = unwrap(map)
            communication.localeAccuracy = calcCommunicationLocaleAccuracy(communicationLocale, map.get(options.locale))
            return Promise.pure(communication)
        }
    }

    private Promise<Map<String, CommunicationLocale>> filterCommunicationLocales(Map<String, CommunicationLocale> localeKeys, String locale) {
        CommunicationLocale key = new CommunicationLocale()

        Field[] fields = CommunicationLocale.class.getDeclaredFields()
        return Promise.each(Arrays.asList(fields)) { Field field ->
            field = getAndCacheField(CommunicationLocale.class, field.getName())
            return fillCommunicationLocaleKey(localeKeys, field.getName(), locale).then { Map<String, Object> value ->
                if (value == null || value.isEmpty()) {
                    return Promise.pure(null)
                }

                value.each { Map.Entry<String, Object> entry ->
                    field.set(key, entry.value)
                }

                return Promise.pure(null)
            }
        }.then {
            Map<String, CommunicationLocale> map = new HashMap<>()
            map.put(locale, key)
            return Promise.pure(map)
        }
    }

    private Promise<Map<String, Object>> fillCommunicationLocaleKey(Map<String, CommunicationLocale> locales, String fieldName, String initLocale) {
        if (locales == null) {
            return Promise.pure(null)
        }
        CommunicationLocale localeKey = locales.get(initLocale)

        if (localeKey != null) {
            Object obj = getAndCacheField(CommunicationLocale.class, fieldName).get(localeKey)
            if (obj != null) {
                Map<String, Object> map = new HashMap<>()
                map.put(initLocale, obj)
                return Promise.pure(map)
            }
        }

        return localeService.get(new LocaleId(initLocale)).then { com.junbo.identity.spec.v1.model.Locale locale1 ->
            if (locale1 == null || locale1.fallbackLocale == null) {
                return Promise.pure(null)
            }

            return fillCommunicationLocaleKey(locales, fieldName, locale1.fallbackLocale.toString())
        }
    }

    private static Field getAndCacheField(Class cls, String fieldName) {
        String key = cls.toString() + ":" + fieldName
        if (fieldMap.get(key) == null) {
            Field field = cls.getDeclaredField(fieldName)
            field.setAccessible(true)
            fieldMap.put(key, field)
        }

        return fieldMap.get(key)
    }

    private static Map<String, CommunicationLocale> wrap(Map<String, JsonNode> jsonNodeMap) {
        if (jsonNodeMap == null || jsonNodeMap.isEmpty()) {
            return new HashMap<String, CommunicationLocale>()
        }

        Map<String, CommunicationLocale> localeMap = new HashMap<>()
        jsonNodeMap.entrySet().each { Map.Entry<String, JsonNode> entry ->
            localeMap.put(entry.key, (CommunicationLocale) JsonHelper.jsonNodeToObj(entry.value, CommunicationLocale))
        }

        return localeMap
    }

    private static Map<String, JsonNode> unwrap(Map<String, CommunicationLocale> localeMap) {
        if (localeMap == null || localeMap.isEmpty()) {
            return new HashMap<String, JsonNode>()
        }

        Map<String, JsonNode> jsonNodeMap = new HashMap<>()
        localeMap.entrySet().each { Map.Entry<String, CommunicationLocale> entry ->
            jsonNodeMap.put(entry.key, ObjectMapperProvider.instance().valueToTree(entry.value))
        }

        return jsonNodeMap
    }

    static String calcCommunicationLocaleAccuracy(CommunicationLocale source, CommunicationLocale target) {
        if (source == target) {
            return LocaleAccuracy.HIGH.toString()
        }

        if (source == null && (target.name == null && target.description == null)) {
            return LocaleAccuracy.HIGH.toString()
        } else if (source == null) {
            return LocaleAccuracy.LOW.toString()
        }

        if (target == null && (source.name == null && source.description == null)) {
            return LocaleAccuracy.HIGH.toString()
        } else if (target == null) {
            return LocaleAccuracy.HIGH.toString()
        }

        if (source.name == target.name && source.description == target.description) {
            return LocaleAccuracy.HIGH.toString()
        } else if (source.name == target.name || source.description == target.description) {
            return LocaleAccuracy.MEDIUM.toString()
        } else {
            return LocaleAccuracy.LOW.toString()
        }
    }
}
