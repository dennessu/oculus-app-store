package com.junbo.test.buyerscenario;

import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.payment.spec.model.PaymentCallbackParams;
import com.junbo.test.buyerscenario.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.AdyenInfo;
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

import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by weiyu_000 on 7/24/14.
 */
public class UAT extends BaseTestClass {
    @Property(
            priority = Priority.BVT,
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
    public void testDigitalGoodCheckoutByCreditCard() throws Exception {
        String vatId = "AU132456";
        String uid = testDataProvider.createUser();

        Country country = Country.DE;
        Currency currency = Currency.EUR;

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);

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

        emulateAdyenCheckout(order);

    }

    private void emulateAdyenCheckout(Order order) throws Exception {
        //Long paymentTransactionId = getTransactionId(order.getUser().getValue());
        String paymentTransactionId = "";
        String authResult = "AUTHORISED";
        String pspReference = "";


        PaymentCallbackParams paymentProperties = new PaymentCallbackParams();
        paymentProperties.setAuthResult(authResult);
        paymentProperties.setPspReference(pspReference);
        PaymentCallbackService paymentCallbackService = PaymentCallbackServiceImpl.getInstance();
        paymentCallbackService.postPaymentProperties(paymentTransactionId, paymentProperties);


        //Post "charge completed" order event
        OrderEventService orderEventService = OrderEventServiceImpl.getInstance();
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(order.getId());
        orderEvent.setAction("CHARGE");
        orderEvent.setStatus("COMPLETED");

        orderEventService.postOrderEvent(orderEvent);
    }

}
