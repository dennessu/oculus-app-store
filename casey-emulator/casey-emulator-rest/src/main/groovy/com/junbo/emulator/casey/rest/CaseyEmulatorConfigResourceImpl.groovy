package com.junbo.emulator.casey.rest
import com.junbo.emulator.casey.spec.resource.CaseyEmulatorConfigResource
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign
import com.junbo.store.spec.model.external.casey.cms.CmsContent
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * The CaseyEmulatorConfigResourceImpl class.
 */
@CompileStatic
@Component('defaultCaseyEmulatorConfigResource')
class CaseyEmulatorConfigResourceImpl implements CaseyEmulatorConfigResource {

    @Resource(name = 'caseyEmulatorUtils')
    EmulatorUtils emulatorUtils

    @Resource(name = 'emulatorCaseyRepository')
    CaseyRepository caseyRepository

    @Override
    Promise<CmsContent> reset() {
        caseyRepository.clear()
        return Promise.pure()
    }

    @Override
    Promise<CmsCampaign> addCmsCampaign(CmsCampaign cmsCampaign) {
        return Promise.pure(caseyRepository.addCmsCampaign(cmsCampaign))
    }

    @Override
    Promise<CmsContent> addCmsContent(CmsContent cmsContent) {
        return Promise.pure(caseyRepository.addCmsContent(cmsContent))
    }

    @Override
    Promise<CaseyAggregateRating> addCaseyAggregateRating(CaseyAggregateRating caseyAggregateRating) {
        def added = caseyRepository.addCaseyAggregateRating(caseyAggregateRating.getResourceId(), caseyAggregateRating)
        return Promise.pure(added)
    }
}
