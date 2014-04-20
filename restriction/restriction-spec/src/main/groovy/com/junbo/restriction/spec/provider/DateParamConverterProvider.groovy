package com.junbo.restriction.spec.provider

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.junbo.restriction.spec.error.AppErrors
import org.springframework.util.StringUtils

import javax.ws.rs.ext.ParamConverter
import javax.ws.rs.ext.ParamConverterProvider
import javax.ws.rs.ext.Provider
import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.text.ParseException

/**
 * DateParamConverterProvider.
 */
@javax.inject.Singleton
@Provider
class DateParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> type, Type genericType, Annotation[] annotations) {
        if (type.equals(Date)) {
            return (ParamConverter<T>) new DateParamConverter()
        } else {
            return null
        }

    }

    private static class DateParamConverter implements ParamConverter<Date> {
        @Override
        public Date fromString(String value) throws Exception {
            if (!StringUtils.isEmpty(value)) {
                try {
                    return new ISO8601DateFormat().parse(value)
                } catch (IllegalArgumentException e) {
                    throw AppErrors.INSTANCE.invalidDateFormat().exception()
                } catch (ParseException ex) {
                    throw AppErrors.INSTANCE.invalidDateFormat().exception()
                }
            }
            return null
        }

        @Override
        public String toString(Date value) {
            return value.toString()
        }

    }
}
