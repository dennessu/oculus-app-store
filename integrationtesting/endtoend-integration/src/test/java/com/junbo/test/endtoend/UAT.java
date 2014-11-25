package com.junbo.test.endtoend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.enumid.CountryId;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.test.endtoend.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.AdyenInfo;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            status = Status.Disable,
            description = "Test digital good checkout",
            steps = {
                    "1. UAT Scenarios",
            }
    )
    @Test
    public void testUATDigitalByCreditCard() throws Exception {

        List<UserAddress> userAddressList = new ArrayList<>();
//        userAddressList.add(addressNC);
        userAddressList.add(addressON);

        for (UserAddress userAddress : userAddressList) {

            Address address = new Address();
            address.setStreet1(userAddress.Street1);
            address.setCity(userAddress.City);
            address.setCountryId(new CountryId(userAddress.Country.toString()));
            address.setSubCountry(userAddress.SubCountry);
            address.setPostalCode(userAddress.PostalCode);

            String uid = testDataProvider.createUser(userAddress.VATId, address);

            Map<String, Integer> offerList = new HashedMap();

            offerList.put(offer_digital_uat, 1);

            CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
            String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

            String orderId = testDataProvider.postOrder(uid,
                    userAddress.Country, userAddress.Currency, creditCardId, false, offerList);

            orderId = testDataProvider.updateOrderTentative(orderId, false);
        }

    }

    @Property(
            priority = Priority.Dailies,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Disable,
            description = "Test digital good checkout",
            steps = {
                    "1. UAT Scenarios",
            }
    )
    @Test
    public void testUATDownloadByCreditCard() throws Exception {

        List<UserAddress> userAddressList = new ArrayList<>();
//        userAddressList.add(addressCA1);
//        userAddressList.add(addressMN);
        userAddressList.add(addressCY);

        for (UserAddress userAddress : userAddressList) {

            Address address = new Address();
            address.setStreet1(userAddress.Street1);
            address.setCity(userAddress.City);
            address.setCountryId(new CountryId(userAddress.Country.toString()));
            address.setSubCountry(userAddress.SubCountry);
            address.setPostalCode(userAddress.PostalCode);

            String uid = testDataProvider.createUser("CY12345678A", address);

            Map<String, Integer> offerList = new HashedMap();

            offerList.put(offer_download_uat, 1);

            CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
            String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

            String orderId = testDataProvider.postOrder(uid,
                    userAddress.Country, userAddress.Currency, creditCardId, false, offerList);

            orderId = testDataProvider.updateOrderTentative(orderId, false);
        }

    }

    @Property(
            priority = Priority.Dailies,
            features = "BuyerScenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Disable,
            description = "Test digital good checkout",
            steps = {
                    "1. UAT Scenarios",
            }
    )
    @Test
    public void testUATPhysicalByCreditCard() throws Exception {
        List<UserAddress> userAddressList = new ArrayList<>();
        UserAddress userBillingAddress = addressDE;  //first one is always default
        UserAddress userShippingAddress = addressIE4;

        userAddressList.add(userBillingAddress);
        userAddressList.add(userShippingAddress);

        Long billingAddressId = 0l;
        Long shippingAddressId = 0l;

        List<Address> addressList = new ArrayList<>();

        for (UserAddress userAddress : userAddressList) {
            Address address = new Address();
            address.setStreet1(userAddress.Street1);
            address.setCity(userAddress.City);
            address.setCountryId(new CountryId(userAddress.Country.toString()));
            address.setSubCountry(userAddress.SubCountry);
            address.setPostalCode(userAddress.PostalCode);
            addressList.add(address);
        }

        String uid = testDataProvider.createUser("CY12345678A", addressList);

        List<UserPersonalInfoLink> userPersonalInfos = Master.getInstance().getUser(uid).getAddresses();

        for (UserPersonalInfoLink userPersonalInfoLink : userPersonalInfos) {
            if (userPersonalInfoLink.getIsDefault()) {
                billingAddressId = userPersonalInfoLink.getValue().getValue();
            } else {
                shippingAddressId = userPersonalInfoLink.getValue().getValue();
            }
        }

        if(userPersonalInfos.size() == 1){
            shippingAddressId = billingAddressId;
        }


        Map<String, Integer> offerList = new HashedMap();
        offerList.put(offer_physical_uat, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo, billingAddressId);

        String orderId = testDataProvider.postOrder(uid,
                userBillingAddress.Country, userBillingAddress.Currency, creditCardId, true, shippingAddressId, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

//            OrderEventService orderEventService = OrderEventServiceImpl.getInstance();
//            OrderEvent orderEvent = new OrderEvent();
//            orderEvent.setOrder(Master.getInstance().getOrder(orderId).getId());
//            orderEvent.setAction("FULFILMENT");
//            orderEvent.setStatus("COMPLETED");
//
//            orderEventService.postOrderEvent(orderEvent);

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
        String VATId;
        Country Country;
        Currency Currency;

        public UserAddress(String street1, String city, String subCountry, String postalCode,
                           String vatId, Country country, Currency currency) {
            Street1 = street1;
            City = city;
            SubCountry = subCountry;
            PostalCode = postalCode;
            VATId = vatId;
            Country = country;
            Currency = currency;
        }
    }

    UserAddress addressCA1 = new UserAddress("150 S 1st St #135", "San Jose", "CA", "95113", null, Country.US, Currency.USD);
    UserAddress addressTX = new UserAddress("910 Louisiana Street #135", "Houston", "TX",
            "77002", "713-224-5800", Country.US, Currency.USD);
    UserAddress addressNC = new UserAddress("500 Fayetteville Street", "Raleigh", "NC", "27601", null, Country.US, Currency.USD);
    UserAddress addressMD = new UserAddress("16 Church Circle", "Annapolis", "MD", "21401", null, Country.US, Currency.USD);
    UserAddress addressMN = new UserAddress("Country Road B West", "Roseville", "MN", "55113", null, Country.US, Currency.USD);
    UserAddress addressCA2 = new UserAddress("643-693 Santa Cruz Avenus", "Menlo Park", "CA",
            "94027", null, Country.US, Currency.USD);
    UserAddress addressWA = new UserAddress("550 Capital Way South, Space C", "Olympia", "WA",
            "98501", null, Country.US, Currency.USD);
    UserAddress addressNV = new UserAddress("400 West 5th Street #101", "Reno", "NV", "89503", null, Country.US, Currency.USD);

    UserAddress addressIE1 = new UserAddress("Kilmartin N6 Centre, Dublin Rd", null, null, null, "CY88788", Country.IE, Currency.EUR);
    UserAddress addressIE2 = new UserAddress("Kilmartin N6 Centre, Dublin Rd", null, null, null, "IE909096", Country.IE, Currency.EUR);
    UserAddress addressIE3 = new UserAddress("Kilmartin N6 Centre, Dublin Rd", null, null, null, "DE132456", Country.IE, Currency.EUR);
    UserAddress addressIE4 = new UserAddress("Kilmartin N6 Centre, Dublin Rd", "Dublin", "Dublin", "0", null, Country.IE, Currency.EUR);

    UserAddress addressBC = new UserAddress("Suite D-5519 Gitselasu Rd", "Terrace", "BC", "V8G 0A9", "0", Country.CA, Currency.USD);
    UserAddress addressPE = new UserAddress("University Ave, Charlottetown", "Charlottetown", "PE", "C1A 8R8", null, Country.CA, Currency.USD);
    UserAddress addressQC = new UserAddress("Accueil Sud Km 33 Rte 167", "La Dore", "QC", "G8J 1Y4", null, Country.CA, Currency.USD);
    UserAddress addressON = new UserAddress("Ogoki", "Ogoki", "ON", "P0T2L0", null, Country.CA, Currency.USD);
    UserAddress addressONVat = new UserAddress("Ogoki", "Ogoki", "ON", "P0T2L0", "CA232132132", Country.CA, Currency.USD);
    UserAddress addressYT = new UserAddress("Mayo Yukon College", "Mayo", "YT", "Y0B 1M0", null, Country.CA, Currency.USD);

    UserAddress addressMX = new UserAddress("16 de Septiembre 79, Centro", "Mexico City", "06000", "Mexico City", null, Country.MX, Currency.USD);

    UserAddress addressAT = new UserAddress("Schottenring 11", "Wien", null, "1010", null, Country.AT, Currency.EUR);
    UserAddress addressAU = new UserAddress("428 George St", "Sydney", "Sydney", "2000", null, Country.AU, Currency.AUD);
    UserAddress addressBE = new UserAddress("Galerie du roi 5", "Brussels", null, "1000", null, Country.BE, Currency.EUR);
    UserAddress addressCY = new UserAddress("Franklin Roosevelt 285", "Limassol", "Limassol", "0", null, Country.CY, Currency.EUR);
    UserAddress addressCZ = new UserAddress("Olivova 2096/4", "Prague", null, "110 00", null, Country.CZ, Currency.EUR);
    UserAddress addressDE = new UserAddress("Frankfurter Allee 111", "Berlin", "Berlin", "10247", null, Country.DE, Currency.EUR);
    UserAddress addressDK = new UserAddress("H. C. Andersens Boulevard 27", "Copenhagen", null, "1553", null, Country.DK, Currency.EUR);
    UserAddress addressES = new UserAddress("Calle de Atocha", "Madrid", null, "28012", null, Country.ES, Currency.EUR);
    UserAddress addressFI = new UserAddress("Elielinaukio 5", "Helsinki", null, "00100", null, Country.FI, Currency.EUR);
    UserAddress addressFR = new UserAddress("36 Rue de la Verrerie", "Paris", null, "75004", null, Country.FR, Currency.EUR);
    UserAddress addressGB = new UserAddress("16-18 Whitehall, London", "London", "London", "123317", null, Country.GB, Currency.EUR);
    UserAddress addressIT = new UserAddress("Via Cristoforo Colombo", "Roma", null, "00144", null, Country.IT, Currency.EUR);
    UserAddress addressNL = new UserAddress("Nieuwezijds Voorburgwal 182", "Amsterdam", null, "1012SJ", null, Country.NL, Currency.EUR);
    UserAddress addressPL = new UserAddress("Grzybowska 63", "Warsaw", null, "00-844", null, Country.PL, Currency.EUR);
    UserAddress addressRU = new UserAddress("ulitsa Novyy Arbat,7", "Moscow", "Moscow", "123317", null, Country.RU, Currency.EUR);
    UserAddress addressSE = new UserAddress("Hamngatan 37", "Stockholm", null, "11153", null, Country.SE, Currency.EUR);


}
