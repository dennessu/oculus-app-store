/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.fulfilment.utility;

import com.junbo.common.id.OrderId;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.ShardIdHelper;
import com.junbo.test.fulfilment.apihelper.FulfilmentService;
import com.junbo.test.fulfilment.apihelper.impl.FulfilmentServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yunlongzhao on 5/14/14.
 */
public class FulfilmentTestDataProvider extends BaseTestDataProvider{
    private FulfilmentService fulfilmentClient = FulfilmentServiceImpl.getInstance();

    public String postFulfilment(String uid, String orderId) throws Exception {
        return postFulfilment(uid, orderId, false);

    }

    public String postFulfilment(String uid, String orderId, boolean hasPhysicalGood) throws Exception {
        Order order = Master.getInstance().getOrder(orderId);

        FulfilmentRequest fulfilmentRequest = new FulfilmentRequest();
        fulfilmentRequest.setOrderId(order.getId().getValue());
        List<FulfilmentItem> fulfilmentItems = new ArrayList<>();
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            FulfilmentItem fulfilmentItem = new FulfilmentItem();
            OrderItem orderItem = order.getOrderItems().get(i);
            String orderItemId = getOrderItemId(uid, IdConverter.hexStringToId(OrderId.class, orderId),
                    orderItem.getOffer().getValue());
            fulfilmentItem.setOrderItemId(IdConverter.hexStringToId(OrderId.class, orderItemId));
            fulfilmentItem.setOfferId(orderItem.getOffer().getValue());
            fulfilmentItem.setQuantity(Integer.parseInt(orderItem.getQuantity().toString()));
            fulfilmentItems.add(fulfilmentItem);
        }
        fulfilmentRequest.setItems(fulfilmentItems);
        fulfilmentRequest.setUserId(order.getUser().getValue());
        fulfilmentRequest.setTrackingGuid(UUID.randomUUID().toString());

        if (hasPhysicalGood) {
            fulfilmentRequest.setShippingMethodId("1L");
            fulfilmentRequest.setShippingAddressId(Master.getInstance().getUser(uid).getAddresses()
                    .get(0).getValue().getValue());
        }
        return fulfilmentClient.postFulfilment(fulfilmentRequest);
    }


    private String getOrderItemId(String uid, Long orderId, String offerId) throws Exception {
        String sqlStr = String.format(
                "select order_item_id from shard_%s.order_item where order_id='%s' and product_item_id='%s'",
                ShardIdHelper.getShardIdByUid(uid), orderId, offerId);
        return dbHelper.executeScalar(sqlStr, DBHelper.DBName.ORDER);
    }

    public String getFulfilmentByOrderId(String orderId) throws Exception {
        return fulfilmentClient.getFulfilmentByOrderId(orderId);
    }

    public FulfilmentItem getFulfilmentItem(String fulfilmentId) throws Exception {
        return fulfilmentClient.getFulfilment(fulfilmentId);
    }

}
