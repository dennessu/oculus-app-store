package com.junbo.store.clientproxy

import com.junbo.store.clientproxy.catalog.CatalogFacade
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The FacadeContainer class.
 */
@CompileStatic
@Component('storeFacadeContainer')
class FacadeContainer {

    @Resource(name = 'storeCatalogFacade')
    CatalogFacade catalogFacade
}
