package com.goshop.catalog.rest.util

import com.goshop.catalog.spec.model.options.GetOptions
import com.goshop.catalog.spec.model.options.PostOptions
import com.goshop.catalog.spec.model.options.PutOptions
import com.goshop.oom.core.MappingContext
import com.goshop.oom.core.filter.ElementMappingFilterList
import com.goshop.oom.core.filter.PropertyMappingFilterImpl
import groovy.transform.CompileStatic
/**
 * Created by kevingu on 11/22/13.
 */
@CompileStatic
class MappingContextUtil {

    private static MappingContext applyBaseOptions(MappingContext context, GetOptions options) {

        if (options.fields != null) {
            def fields = options.fields.split(",")

            context.propertiesToInclude = fields as List
            context.propertyMappingFilter = new PropertyMappingFilterImpl()
        }

        def elementMappingFilters = new ElementMappingFilterList(filters: [])

        if (options.country != null) {
            def countries = options.country.split(",")
            context.countriesToInclude = countries as List
        }

        if (options.locale != null) {
            def locales = options.locale.split(",")
            context.localesToInclude = locales as List
        }

        if (options.currency != null) {
            def currencies = options.currency.split(",")
            context.currenciesToInclude = currencies as List
        }

        if (elementMappingFilters.filters.size() > 0) {
            context.elementMappingFilter = elementMappingFilters
        }

        if (options.expand != null) {
            def entities = options.expand.split(",")
            context.entitiesToExpand = entities as List
        }

        return context
    }

    static MappingContext applyOptions(MappingContext context, GetOptions options) {

        applyBaseOptions(context, options)

        return context
    }

    static MappingContext applyOptions(MappingContext context, PostOptions options) {
        if (options.body == Boolean.FALSE) {
            context.skipMapping = true
        }

        if (options.expand != null) {
            def entities = options.expand.split(",")
            context.entitiesToExpand = entities as List
        }

        return context
    }

    static MappingContext applyOptions(MappingContext context, PutOptions options) {
        if (options.body == Boolean.FALSE) {
            context.skipMapping = true
        }

        if (options.expand != null) {
            def entities = options.expand.split(",")
            context.entitiesToExpand = entities as List
        }

        return context
    }
}
