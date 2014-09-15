package com.junbo.identity.rest.resource.v1

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.ErrorIdentifier
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.filter.ErrorInfoFilter
import com.junbo.identity.core.service.validator.ErrorInfoValidator
import com.junbo.identity.data.identifiable.LocaleAccuracy
import com.junbo.identity.data.repository.ErrorInfoRepository
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.ErrorDetail
import com.junbo.identity.spec.v1.model.ErrorInfo
import com.junbo.identity.spec.v1.option.list.ErrorInfoListOptions
import com.junbo.identity.spec.v1.option.model.ErrorInfoGetOptions
import com.junbo.identity.spec.v1.resource.ErrorInfoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

import java.lang.reflect.Field

/**
 * Created by liangfu on 7/22/14.
 */
@Transactional
@CompileStatic
class ErrorInfoResourceImpl implements ErrorInfoResource {
    private static Map<String, Field> fieldMap = new HashMap<String, Field>()

    @Autowired
    private ErrorInfoRepository errorInfoRepository

    @Autowired
    private ErrorInfoFilter errorInfoFilter

    @Autowired
    private ErrorInfoValidator errorInfoValidator

    @Autowired
    private LocaleRepository localeRepository

    @Override
    Promise<ErrorInfo> create(ErrorInfo errorInfo) {
        if (errorInfo == null) {
            throw new IllegalArgumentException('errorInfo is null')
        }

        errorInfo = errorInfoFilter.filterForCreate(errorInfo)

        return errorInfoValidator.validateForCreate(errorInfo).then {
            return errorInfoRepository.create(errorInfo).then { ErrorInfo newErrorInfo ->
                Created201Marker.mark(newErrorInfo.id)
                newErrorInfo = errorInfoFilter.filterForGet(newErrorInfo, null)

                return Promise.pure(newErrorInfo)
            }
        }
    }

    @Override
    Promise<ErrorInfo> put(ErrorIdentifier errorIdentifier, ErrorInfo errorInfo) {
        if (errorIdentifier == null) {
            throw new IllegalArgumentException('errorIdentifier is null')
        }

        if (errorInfo == null) {
            throw new IllegalArgumentException('errorInfo is null')
        }

        return errorInfoRepository.get(errorIdentifier).then { ErrorInfo oldErrorInfo ->
            if (oldErrorInfo == null) {
                throw AppErrors.INSTANCE.errorInfoNotFound(errorIdentifier).exception()
            }

            errorInfo = errorInfoFilter.filterForPut(errorInfo, oldErrorInfo)

            return errorInfoValidator.validateForUpdate(errorIdentifier, errorInfo, oldErrorInfo).then {
                return errorInfoRepository.update(errorInfo, oldErrorInfo).then { ErrorInfo newErrorInfo ->
                    newErrorInfo = errorInfoFilter.filterForGet(newErrorInfo, null)
                    return Promise.pure(newErrorInfo)
                }
            }
        }
    }

    @Override
    Promise<ErrorInfo> patch(ErrorIdentifier errorIdentifier, ErrorInfo errorInfo) {
        if (errorIdentifier == null) {
            throw new IllegalArgumentException('errorIdentifier is null')
        }

        if (errorInfo == null) {
            throw new IllegalArgumentException('errorInfo is null')
        }

        return errorInfoRepository.get(errorIdentifier).then { ErrorInfo oldErrorInfo ->
            if (oldErrorInfo == null) {
                throw AppErrors.INSTANCE.errorInfoNotFound(errorIdentifier).exception()
            }

            errorInfo = errorInfoFilter.filterForPatch(errorInfo, oldErrorInfo)

            return errorInfoValidator.validateForUpdate(errorIdentifier, errorInfo, oldErrorInfo).then {
                return errorInfoRepository.update(errorInfo, oldErrorInfo).then { ErrorInfo newErrorInfo ->
                    newErrorInfo = errorInfoFilter.filterForGet(newErrorInfo, null)
                    return Promise.pure(newErrorInfo)
                }
            }
        }
    }

    @Override
    Promise<ErrorInfo> get(ErrorIdentifier errorIdentifier, ErrorInfoGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return errorInfoValidator.validateForGet(errorIdentifier).then {
            return errorInfoRepository.get(errorIdentifier).then { ErrorInfo newErrorInfo ->
                if (newErrorInfo == null) {
                    throw AppErrors.INSTANCE.errorInfoNotFound(errorIdentifier).exception()
                }
                return filterErrorInfo(newErrorInfo, getOptions).then { ErrorInfo filterErrorInfo ->
                    filterErrorInfo = errorInfoFilter.filterForGet(filterErrorInfo, null)
                    return Promise.pure(filterErrorInfo)
                }
            }
        }
    }

    @Override
    Promise<Results<ErrorInfo>> list(ErrorInfoListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalStateException('Unsupported operation')
        }

        return errorInfoValidator.validateForSearch(listOptions).then {
            return errorInfoRepository.searchAll(listOptions.limit, listOptions.offset).then { List<ErrorInfo> errorInfoList ->
                def result = new Results<ErrorInfo>(items: [])

                errorInfoList.each { ErrorInfo newErrorInfo ->
                    newErrorInfo = errorInfoFilter.filterForGet(newErrorInfo, null)

                    if (newErrorInfo != null) {
                        result.items.add(newErrorInfo)
                    }
                }

                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<Void> delete(ErrorIdentifier errorIdentifier) {
        throw new IllegalStateException('Unsupported operation')
    }

    private Promise<ErrorInfo> filterErrorInfo(ErrorInfo errorInfo, ErrorInfoGetOptions getOptions) {
        if (StringUtils.isEmpty(getOptions.locale)) {
            return Promise.pure(errorInfo)
        }

        ErrorDetail errorDetail = null
        JsonNode jsonNode = errorInfo.locales.get(getOptions.locale)
        if (jsonNode != null) {
            errorDetail = (ErrorDetail) JsonHelper.jsonNodeToObj(jsonNode, ErrorDetail)
        }
        return fillErrorInfo(errorInfo, getOptions.locale).then { Map<String, ErrorDetail> map ->
            errorInfo.locales = unwrap(map)
            errorInfo.localeAccuracy = calcErrorDetailKeyAccuracy(errorDetail, map.get(getOptions.locale))
            return Promise.pure(errorInfo)
        }
    }

    Promise<Map<String, ErrorDetail>> fillErrorInfo(ErrorInfo errorInfo, String locale) {
        ErrorDetail errorDetailKey = new ErrorDetail()

        Field[] fields = ErrorDetail.class.getDeclaredFields()
        return Promise.each(Arrays.asList(fields)) { Field field ->
            field = getAndCacheField(ErrorDetail.class, field.getName())
            return fillErrorInfo(errorInfo, field.getName(), locale).then { Map<String, Object> value ->
                if (value == null || value.isEmpty()) {
                    return Promise.pure(null)
                }

                value.each { Map.Entry<String, Object> entry ->
                    field.set(errorDetailKey, entry.value)
                }

                return Promise.pure(null)
            }
        }.then {
            Map<String, ErrorDetail> map = new HashMap<>()
            map.put(locale, errorDetailKey)
            return Promise.pure(map)
        }
    }

    // return locale:fieldValue
    // if we can't find it in the end, will return error;
    // else will return the first locale with fieldValue
    Promise<Map<String, Object>> fillErrorInfo(ErrorInfo errorInfo, String fieldName, String initLocale) {
        if (errorInfo.locales == null) {
            return Promise.pure(null)
        }
        JsonNode jsonNode = errorInfo.locales.get(initLocale)
        if (jsonNode != null) {
            ErrorDetail localeKey = (ErrorDetail) JsonHelper.jsonNodeToObj(jsonNode, ErrorDetail)

            Object obj = getAndCacheField(ErrorDetail.class, fieldName).get(localeKey)
            if (obj != null) {
                Map<String, Object> map = new HashMap<>()
                map.put(initLocale, obj)
                return Promise.pure(map)
            }
        }

        return localeRepository.get(new LocaleId(initLocale)).then { com.junbo.identity.spec.v1.model.Locale locale1 ->
            if (locale1 == null || locale1.fallbackLocale == null) {
                return Promise.pure(null)
            }

            return fillErrorInfo(errorInfo, fieldName, locale1.fallbackLocale.toString())
        }
    }

    private Field getAndCacheField(Class cls, String fieldName) {
        String key = cls.toString() + ":" + fieldName
        if (fieldMap.get(key) == null) {
            Field field = cls.getDeclaredField(fieldName)
            field.setAccessible(true)
            fieldMap.put(key, field)
        }

        return fieldMap.get(key)
    }

    static String calcErrorDetailKeyAccuracy(ErrorDetail source, ErrorDetail target) {
        if (source == target) {
            return LocaleAccuracy.HIGH.toString()
        }

        if (source == null &&
                (target.supportLink == null && target.errorTitle == null && target.errorSummary == null && target.errorInformation == null)) {
            return LocaleAccuracy.HIGH.toString()
        } else if (source == null) {
            return LocaleAccuracy.LOW.toString()
        }

        if (target == null &&
                (source.supportLink == null && source.errorTitle == null && source.errorSummary == null && source.errorInformation == null)) {
            return LocaleAccuracy.HIGH.toString()
        } else if (target == null) {
            return LocaleAccuracy.LOW.toString()
        }

        if (source.supportLink == target.supportLink && source.errorTitle == target.errorTitle && source.errorSummary == target.errorSummary
                && source.errorInformation == target.errorInformation) {
            return LocaleAccuracy.HIGH.toString()
        } else if (source.supportLink == target.supportLink || source.errorTitle == target.errorTitle || source.errorSummary == target.errorSummary
                || source.errorInformation == target.errorInformation) {
            return LocaleAccuracy.MEDIUM.toString()
        } else {
            return LocaleAccuracy.LOW.toString()
        }
    }

    static Map<String, JsonNode> unwrap(Map<String, ErrorDetail> map) {
        Map<String, JsonNode> jsonNodeMap = new HashMap<>()

        map.entrySet().each { Map.Entry<String, ErrorDetail> entry ->
            jsonNodeMap.put(entry.key, ObjectMapperProvider.instance().valueToTree(entry.value))
        }

        return jsonNodeMap
    }
}
