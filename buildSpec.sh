#!/bin/sh
cd gradle/bootstrap
gradle clean install -x test
cd ../../langur
gradle clean install -x test
cd ../common
gradle clean install -x test
cd ../oom
gradle clean install -x test
cd ../identity/identity-spec
gradle clean install -x test
cd ../../entitlement/entitlement-spec
gradle clean install -x test
cd ../../fulfilment/fulfilment-spec
gradle clean install -x test
cd ../../catalog/catalog-spec
gradle clean install -x test
cd ../../rating/rating-spec
gradle clean install -x test
cd ../../cart/cart-spec
gradle clean install -x test
cd ../../billing/billing-spec
gradle clean install -x test
cd ../../ewallet/ewallet-spec
gradle clean install -x test
cd ../../order/order-spec
gradle clean install -x test
cd ../../payment/payment-spec
gradle clean install -x test
