package com.junbo.payment.clientproxy.service;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.identity.spec.v1.resource.CountryResource;
import com.junbo.identity.spec.v1.resource.CurrencyResource;
import com.junbo.payment.clientproxy.BaseTest;
import com.junbo.payment.clientproxy.impl.CountryServiceFacadeImpl;
import com.junbo.payment.clientproxy.impl.CurrencyServiceFacadeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Administrator on 14-7-9.
 */
public class DependantClientProxyTest extends BaseTest {
    @Autowired
    private CountryServiceFacadeImpl countryResource;
    @Autowired
    private CurrencyServiceFacadeImpl currencyResource;

    @Test(enabled = false)
    public void testCountryResource(){
        String result = countryResource.getDefaultCurrency("US").get().getValue();
        Assert.assertEquals(result, "USD");
        result = countryResource.getDefaultCurrency("DE").get().getValue();
        Assert.assertEquals(result, "EUR");
        result = countryResource.getDefaultCurrency("CN").get().getValue();
        Assert.assertEquals(result, "CNY");
    }

    @Test(enabled = false)
    public void testCurrencyResource(){
        Long minAuthAmount = currencyResource.getMinAuthAmount(new CurrencyId("USD")).get();
        Assert.assertEquals(minAuthAmount.longValue(), 100L);
        Long numbers = currencyResource.getNumberAfterDecimal("USD").get();
        Assert.assertEquals(numbers.longValue(), 100L);

        minAuthAmount = currencyResource.getMinAuthAmount(new CurrencyId("JPY")).get();
        Assert.assertEquals(minAuthAmount.longValue(), 1L);
        numbers = currencyResource.getNumberAfterDecimal("JPY").get();
        Assert.assertEquals(numbers.longValue(), 1L);

        minAuthAmount = currencyResource.getMinAuthAmount(new CurrencyId("CNY")).get();
        Assert.assertEquals(minAuthAmount.longValue(), 100L);
        numbers = currencyResource.getNumberAfterDecimal("CNY").get();
        Assert.assertEquals(numbers.longValue(), 100L);
    }

}
