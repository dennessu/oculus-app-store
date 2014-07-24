package com.junbo.billing.clientproxy

import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.common.enumid.CountryId
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.identity.spec.v1.model.Address
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.testng.Assert
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * UT for Sabrix facade.
 */
@CompileStatic
@TypeChecked
class SabrixFacadeTest extends BaseTest{
    @Resource(name = 'sabrixFacade')
    TaxFacade sabrixFacade

    @Test(enabled = false)
    void testTaxCalculation() {
        def physicalBalance = buildBalance(true)
        def address = buildAddress()
        Balance balanceWithTax = sabrixFacade.calculateTaxQuote(physicalBalance, address, address).get()
        Assert.assertNotNull(balanceWithTax, 'Fail to calculate tax.');
        Assert.assertEquals(balanceWithTax.taxStatus, TaxStatus.TAXED.name(), 'Fail to calculate tax.')
    }

    @Test(enabled = false)
    void testAddressValidation() {
        def address = buildAddress()
        Address validatedAddress = sabrixFacade.validateAddress(address).get()
        Assert.assertNotNull(validatedAddress, 'Fail to validate address.')
    }

    @Test(enabled = false)
    void testVatIdValidation() {
        def id = 'IE6388047V'
        def country = 'IE'
        VatIdValidationResponse response = sabrixFacade.validateVatId(id, country).get()
        Assert.assertEquals(response.status, 'VALID', 'Fail to validate valid VAT ID.')

        def invalidId = '12345'
        VatIdValidationResponse errorResponse = sabrixFacade.validateVatId(invalidId, country).get()
        Assert.assertEquals(errorResponse.status, 'INVALID', 'Fail to validate invalid VAT ID.')
    }

    Address buildAddress() {
        def address = new Address()
        address.countryId = new CountryId('US')
        address.subCountry = 'CA'
        address.city = 'oakland'
        address.postalCode = '94601'

        return address
    }

    Balance buildBalance(boolean physical) {
        def balance = new Balance()
        balance.id = new BalanceId(123L)
        balance.orderIds = [new OrderId(321L)]
        balance.currency = 'USD'
        balance.addBalanceItem(buildBalanceItem(physical))

        return balance
    }

    BalanceItem buildBalanceItem(boolean physical) {
        def item = new BalanceItem()
        item.financeId = '123'
        item.amount = BigDecimal.valueOf(1000)
        if (physical) {
            item.propertySet.put(PropertyKey.ITEM_TYPE.name(), 'PHYSICAL')
        }
        else {
            item.propertySet.put(PropertyKey.ITEM_TYPE.name(), 'DIGITAL')
        }

        return item
    }
}
