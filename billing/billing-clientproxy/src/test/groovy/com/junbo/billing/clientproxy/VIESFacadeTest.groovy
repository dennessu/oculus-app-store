package com.junbo.billing.clientproxy

import com.junbo.billing.spec.model.VatIdValidationResponse
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.testng.Assert
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * UT for VIES facade.
 */
@CompileStatic
@TypeChecked
class VIESFacadeTest extends BaseTest {
    @Resource(name = 'viesFacade')
    TaxFacade viesFacade

    @Test(enabled = false)
    void testVatIdValidation() {
        def id = '26033489'
        def country = 'DK'
        VatIdValidationResponse response = viesFacade.validateVatId(id, country).get()
        Assert.assertEquals(response.status, 'VALID', 'Fail to validate valid VAT ID.')

        def invalidId = '12345'
        VatIdValidationResponse errorResponse = viesFacade.validateVatId(invalidId, country).get()
        Assert.assertEquals(errorResponse.status, 'INVALID', 'Fail to validate invalid VAT ID.')
    }
}
