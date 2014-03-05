package com.goshop.catalog.rest.resolver

import com.goshop.catalog.common.error.AppErrors
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.data.domain.Sort
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class SortResolverImpl implements SortResolver {

    private static final String ORDER = "order"

    private Class<?> entityClass

    private Map<String, String> sortableProperties

    private List<String> possibleValues

    @Required
    void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass
    }

    @Required
    void setSortableProperties(String sortableProperties) {
        this.sortableProperties = [:]
        this.possibleValues = []

        if (sortableProperties != null) {
            def properties = sortableProperties.split(",")
            properties.each { String pair ->
                if (pair.contains(":")) {
                    def parts = pair.split(":", 2)
                    this.sortableProperties.put(parts[0], parts[1])
                } else {
                    this.sortableProperties.put(pair, pair)
                }
            }

            possibleValues.addAll(this.sortableProperties.keySet())
            possibleValues.addAll(this.sortableProperties.keySet().collect { String prop -> "-" + prop })
        }
    }

    @Override
    boolean canResolve(Class<?> entityClass) {
        return entityClass == this.entityClass
    }

    @Override
    Sort resolve(Class<?> entityClass, Map<String, Object> options) {

        if (options[ORDER] == null) {
            return null
        }

        def order = ((String) options[ORDER]).trim()
        def direction = Sort.Direction.ASC

        if (order.startsWith("-")) {
            order = order.substring(1)

            order = sortableProperties[order]
            direction = Sort.Direction.DESC
        } else {
            order = sortableProperties[order]
        }

        if (order == null) {
            throw AppErrors.INSTANCE.queryParamInvalid(ORDER,
                    "Possible values: " + possibleValues.join(", ")).exception()
        } else {
            return new Sort(new Sort.Order(direction, order))
        }
    }
}
