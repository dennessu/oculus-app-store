package com.junbo.emulator.casey.rest

import com.junbo.catalog.spec.resource.ItemAttributeResource
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.catalog.spec.resource.OfferAttributeResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.identity.spec.v1.resource.LocaleResource
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The ResourceContainer class.
 */
@CompileStatic
@Component('caseyResourceContainer')
class ResourceContainer {

    @Resource(name = 'casey.offerClient')
    OfferResource offerResource
    @Resource(name = 'casey.offerRevisionClient')
    OfferRevisionResource offerRevisionResource
    @Resource(name = 'casey.offerAttributeClient')
    OfferAttributeResource offerAttributeResource
    @Resource(name = 'casey.itemClient')
    ItemResource itemResource
    @Resource(name = 'casey.offerItemRevisionClient')
    ItemRevisionResource itemRevisionResource
    @Resource(name = 'casey.itemAttributeClient')
    ItemAttributeResource itemAttributeResource
    @Resource(name = 'casey.localeResource')
    LocaleResource localeResource
}
