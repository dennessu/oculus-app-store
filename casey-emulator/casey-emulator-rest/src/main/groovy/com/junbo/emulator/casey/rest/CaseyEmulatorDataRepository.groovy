package com.junbo.emulator.casey.rest

import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import com.junbo.store.spec.model.external.casey.cms.CmsPage
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * The CaseyRepository class.
 */
@CompileStatic
@Component('caseyEmulatorDataRepository')
class CaseyEmulatorDataRepository implements InitializingBean {

    @Value('${store.browse.featured.page}')
    private String defaultPageName;

    private CaseyEmulatorData caseyEmulatorData = new CaseyEmulatorData(
        caseyAggregateRatings: [],
        caseyReviews: []
    )

    CaseyEmulatorData post(CaseyEmulatorData caseyEmulatorData) {
        if (caseyEmulatorData.cmsPages != null) {
            this.caseyEmulatorData.cmsPages = caseyEmulatorData.cmsPages
        }
        if (caseyEmulatorData.cmsPageOffers != null) {
            this.caseyEmulatorData.cmsPageOffers = caseyEmulatorData.cmsPageOffers
        }
        this.caseyEmulatorData.caseyAggregateRatings = caseyEmulatorData.caseyAggregateRatings
        this.caseyEmulatorData.caseyReviews = caseyEmulatorData.caseyReviews
        return caseyEmulatorData
    }

    CaseyEmulatorData get() {
        return caseyEmulatorData
    }

    @Override
    void afterPropertiesSet() throws Exception {
        caseyEmulatorData = new CaseyEmulatorData(
                caseyAggregateRatings: [],
                caseyReviews: [],
                cmsPages: [ new CmsPage(
                        path: defaultPageName,
                        slots: [:] as Map
                ) ] as List
        )
    }
}
