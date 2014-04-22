package com.junbo.fulfilment.rest.interceptor;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RequestInterceptorTest {
    @Test
    public void testCleanup() {
        RequestInterceptor interceptor = new RequestInterceptor();
        String input = getSample();

        String output = interceptor.cleanup(input);

        Assert.assertFalse(output.contains("href"));
    }

    private String getSample() {
        return "{\n" +
                "            \"userId\": {\"href\": \"http://api.wan-san.com/v1/users/12345\", \"id\": \"12345\"},\n" +
                "            \"orderId\": {\"href\": \"http://api.wan-san.com/v1/orders/12345\", \"id\": \"12345\"},\n" +
                "            \"trackingGuid\": \"151907d5-807e-4a69-83e6-e709b6f75359\",\n" +
                "            \"items\":[\n" +
                "                {\n" +
                "                    \"billingLineItemId\": \"1000000001\",\n" +
                "                    \"offerId\": {\"href\": \"http://api.wan-san.com/v1/offers/11111\", \"id\": \"11111\"},\n" +
                "                    \"sku\": \"1000000001\",\n" +
                "                    \"offerRevision\": 1,\n" +
                "                    \"quantity\": 1\n" +
                "                },\n" +
                "                {\n" +
                "                    \"billingLineItemId\": \"1000000002\",\n" +
                "                    \"offerId\": {\"href\": \"http://api.wan-san.com/v1/offers/22222\", \"id\": \"22222\"},\n" +
                "                    \"sku\": \"1000000002\",\n" +
                "                    \"offerRevision\": 2,\n" +
                "                    \"quantity\": 10\n" +
                "                }\n" +
                "            ],\n" +
                "            \"shippingInfo\": {\n" +
                "                \"firstName\":  \"Hello\",\n" +
                "                \"middleName\":  \"My\",\n" +
                "                \"lastName\": \"World\",\n" +
                "                \"shihppingMethod\": \"EMS\",\n" +
                "                \"address\": {\n" +
                "                    \"country\": \"US\",\n" +
                "                    \"state\": \"CA\",\n" +
                "                    \"city\": \"Redwood\",\n" +
                "                    \"street\": \"test street\",\n" +
                "                    \"street1\": \"test street1\",\n" +
                "                    \"street2\": \"test street2\",\n" +
                "                    \"street3\": \"test street3\",\n" +
                "                    \"postCode\": \"12345\",\n" +
                "                    \"phoneNumber\": \"1234567\"\n" +
                "                }\n" +
                "            }\n" +
                "        }";

    }
}
