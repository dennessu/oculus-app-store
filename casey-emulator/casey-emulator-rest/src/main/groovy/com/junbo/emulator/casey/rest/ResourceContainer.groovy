package com.junbo.emulator.casey.rest
import com.junbo.catalog.spec.resource.*
import com.junbo.entitlement.spec.resource.EntitlementResource
import com.junbo.identity.spec.v1.resource.LocaleResource
import com.junbo.identity.spec.v1.resource.OrganizationResource
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
    @Resource(name = 'sewer.entitlementResource')
    EntitlementResource entitlementResource
    @Resource(name = 'sewer.organizationResource')
    OrganizationResource organizationResource
}
