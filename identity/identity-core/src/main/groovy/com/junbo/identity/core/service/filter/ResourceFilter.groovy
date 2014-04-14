package com.junbo.identity.core.service.filter

/**
 * Created by kg on 3/19/2014.
 */
interface ResourceFilter<T> {

    T filterForCreate(T resource)

    T filterForPut(T resource, T oldResource)

    T filterForPatch(T resource, T oldResource)

    T filterForGet(T resource, List<String> propertiesToInclude)
}
