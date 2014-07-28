package com.junbo.test.buyerscenario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.payment.spec.model.PaymentCallbackParams;
import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.AdyenInfo;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.apihelper.order.OrderEventService;
import com.junbo.test.common.apihelper.order.impl.OrderEventServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.payment.apihelper.PaymentCallbackService;
import com.junbo.test.payment.apihelper.impl.PaymentCallbackServiceImpl;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weiyu_000 on 7/24/14.
 */
public class UAT extends BaseTestClass {
    @Property(
            priority = Priority.Dailies,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            description = "Test UAT Scenario",
            steps = {
                    "1. Adyen checkout flow",
            }
    )
    @Test
    public void testUATByAdyen() throws Exception {
        String vatId = "AU132456";
        String uid = testDataProvider.createUser();

        Country country = Country.US;
        Currency currency = Currency.USD;

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_uat, 1);

        AdyenInfo adyenInfo = AdyenInfo.getAdyenInfo(country);
        String adyenId = testDataProvider.postPaymentInstrument(uid, adyenInfo);

        String orderId = testDataProvider.postOrder(uid, country, currency, adyenId, false, offerList);

        Order order = Master.getInstance().getOrder(orderId);
        order.setTentative(false);
        order.getPayments().get(0).setSuccessRedirectUrl("http://www.baidu.com/");
        order.getPayments().get(0).setCancelRedirectUrl("http://www.baidu.com/cancel/");
        orderId = testDataProvider.updateOrder(order);
        order = Master.getInstance().getOrder(orderId);
        String providerConfirmUrl = order.getPayments().get(0).getProviderConfirmUrl();

        /*
        String[] params = providerConfirmUrl.split("&");
        String urlEncoded = new String();

        for (int i = 0; i < params.length; i++) {
            if (params[i].contains("merchantSig")) {
                String sig = params[i].substring(12);
                String sigEncoded = URLEncoder.encode(sig, "utf-8");
                params[i] = "merchantSig=" + sigEncoded;
            }
            if (params[i].contains("shopperEmail")) {
                String email = params[i].substring(13);
                String emailEncoded = URLEncoder.encode(email, "utf-8");
                params[i] = "shopperEmail=" + emailEncoded;
            }
            urlEncoded += params[i] + "&";
        }

        urlEncoded = urlEncoded.substring(0, urlEncoded.length() - 1);
        */

        emulateAdyenCheckout(order);

        //MasterCard
        //5555 4444 3333 1111
        //CVC  737  Exp

    }

    @Property(
            priority = Priority.Dailies,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test digital good checkout",
            steps = {
                    "1. UAT Scenarios",
            }
    )
    @Test
    public void testUATByCreditCard() throws Exception {

        Address address = new Address();
        UserAddress userAddress = addressCA1;
        address.setStreet1(userAddress.Street1);
        address.setCity(userAddress.City);
        address.setCountryId(new CountryId(Country.DEFAULT.toString()));
        address.setSubCountry(userAddress.SubCountry);
        address.setPostalCode(userAddress.PostalCode);


        String uid = testDataProvider.createUser(null, address);

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_uat, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(uid,
                Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

    }

    private void emulateAdyenCheckout(Order order) throws Exception {
        //Long paymentTransactionId = getTransactionId(order.getUser().getValue());
        String successRedirectUrl = "";
        String paymentTransactionId = "";
        String authResult = "AUTHORISED";
        String pspReference = "";
        String skinCode = "";
        String merchantSig = "";
        String[] params = successRedirectUrl.split("&");
        for (String param : params) {
            String value = param.substring(param.indexOf('=') + 1);
            if (param.contains("merchantReference")) {
                paymentTransactionId = value;
            } else if (param.contains("skinCode")) {
                skinCode = value;
            } else if (param.contains("psp")) {
                pspReference = value;
            } else if (param.contains("merchantSig")) {
                //merchantSig = URLEncoder.encode(value, "utf-8");
                merchantSig = URLDecoder.decode(value, "utf-8");
            }
        }


        Map<String, String> properties = new HashMap<>();
        properties.put("paymentId", paymentTransactionId);
        properties.put("pspReference", pspReference);
        properties.put("authResult", authResult);
        properties.put("skinCode", skinCode);
        properties.put("merchantSig", merchantSig);
        properties.put("merchantReference", paymentTransactionId);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(properties);

        //Post "charge completed" order event
        com.junbo.test.order.apihelper.OrderEventService orderEventService =
                com.junbo.test.order.apihelper.impl.OrderEventServiceImpl.getInstance();
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(order.getId());
        orderEvent.setAction("CHARGE");
        orderEvent.setStatus("COMPLETED");
        orderEvent.setProperties(json);

        orderEventService.postOrderEvent(orderEvent);
    }

    class UserAddress {
        String Street1;
        String City;
        String SubCountry;
        String PostalCode;
        String PhoneNum;
        String CountryId;

        public UserAddress(String street1, String city, String subCountry, String postalCode,
                           String phoneNum, String countryId) {
            Street1 = street1;
            City = city;
            SubCountry = subCountry;
            PostalCode = postalCode;
            PhoneNum = phoneNum;
            CountryId = countryId;
        }
    }

    UserAddress addressCA1 = new UserAddress("150 S 1st St #135", "San Jose", "CA", "95113", "408-293-9945", "US");
    UserAddress addressTX = new UserAddress("910 Louisiana Street #135", "Houston", "TX",
            "77002", "713-224-5800", "US");
    UserAddress addressNC = new UserAddress("500 Fayetteville Street", "Raleigh", "NC", "27601", "919-334-9894", "US");
    UserAddress addressMD = new UserAddress("16 Church Circle", "Annapolis", "MD", "21401", "410-263-2641", "US");
    UserAddress addressMN = new UserAddress("Country Road B West", "Roseville", "MN", "55113", "651-482-0198", "US");
    UserAddress addressCA2 = new UserAddress("643-693 Santa Cruz Avenus", "Menlo Park", "CA",
            "94027", "650-323-5118", "US");
    UserAddress addressWA = new UserAddress("550 Capital Way South, Space C", "Olympia", "WA",
            "98501", "360-753-7771", "US");
    UserAddress addressNV = new UserAddress("400 West 5th Street #101", "Reno", "NV", "89503", "775-348-7800", "US");

}
