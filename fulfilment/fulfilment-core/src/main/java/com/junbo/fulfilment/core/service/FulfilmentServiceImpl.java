/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.service;

import com.junbo.fulfilment.clientproxy.CatalogGateway;
import com.junbo.fulfilment.common.util.Callback;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.core.FulfilmentService;
import com.junbo.fulfilment.core.FulfilmentSupport;
import com.junbo.fulfilment.core.context.FulfilmentContext;
import com.junbo.fulfilment.core.context.FulfilmentContextFactory;
import com.junbo.fulfilment.core.context.PhysicalGoodsContext;
import com.junbo.fulfilment.core.handler.HandlerRegistry;
import com.junbo.fulfilment.core.util.ModelUtils;
import com.junbo.fulfilment.db.repo.FulfilmentActionRepository;
import com.junbo.fulfilment.db.repo.FulfilmentRepository;
import com.junbo.fulfilment.db.repo.FulfilmentRequestRepository;
import com.junbo.fulfilment.spec.constant.FulfilmentActionType;
import com.junbo.fulfilment.spec.constant.FulfilmentStatus;
import com.junbo.fulfilment.spec.error.AppErrors;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;
import com.junbo.fulfilment.spec.fusion.Offer;
import com.junbo.fulfilment.spec.fusion.OfferAction;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;

/**
 * FulfilmentServiceImpl.
 */
public class FulfilmentServiceImpl extends TransactionSupport implements FulfilmentService, FulfilmentSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(FulfilmentServiceImpl.class);

    @Autowired
    private FulfilmentRepository fulfilmentRepo;

    @Autowired
    private FulfilmentRequestRepository fulfilmentRequestRepo;

    @Autowired
    private FulfilmentActionRepository fulfilmentActionRepository;

    @Autowired
    private CatalogGateway catalogGateway;

    @Override
    @Transactional
    public FulfilmentRequest fulfill(FulfilmentRequest request) {
        // check tracking GUID
        Long requestId = fulfilmentRequestRepo.existTrackingGuid(request.getUserId(), request.getTrackingUuid());
        if (requestId != null) {
            return retrieveRequest(requestId);
        }

        // validate fulfilment request
        validate(request);

        // distill fulfilment actions
        distill(request);

        // persist fulfilment request
        store(request);

        // classify fulfilment actions
        ClassifyResult classifyResult = classify(request);

        // dispatch fulfilment actions
        dispatch(request, classifyResult);

        return request;
    }

    @Override
    public FulfilmentRequest retrieveRequest(Long requestId) {
        return load(requestId);
    }

    @Override
    public FulfilmentRequest retrieveRequestByOrderId(Long billingOrderId) {
        Long requestId = fulfilmentRequestRepo.existBillingOrderId(billingOrderId);

        if (requestId == null) {
            String errorMessage = "Fulfilment request with BillingOrderId [" + billingOrderId + "] does not exist.";
            LOGGER.error(errorMessage);
            throw AppErrors.INSTANCE.common(errorMessage).exception();
        }

        return load(requestId);
    }

    @Override
    public FulfilmentItem retrieveFulfilmentItem(Long fulfilmentId) {
        FulfilmentItem item = fulfilmentRepo.get(fulfilmentId);
        if (item == null) {
            LOGGER.error("Fulfilment item with id [" + fulfilmentId + "] does not exist");
            throw AppErrors.INSTANCE.notFound("Fulfilment", fulfilmentId).exception();
        }

        item.setActions(fulfilmentActionRepository.findByFulfilmentId(item.getFulfilmentId()));

        return item;
    }

    @Override
    public void validate(FulfilmentRequest request) {
        // ensure order id does not exist
        Long requestId = fulfilmentRequestRepo.existBillingOrderId(request.getOrderId());
        if (requestId != null) {
            String errorMessage = "Fulfilment request with orderId ["
                    + request.getOrderId() + "] has already been issued.";
            LOGGER.error(errorMessage);
            throw AppErrors.INSTANCE.common(errorMessage).exception();
        }

        // ensure fulfilment items are specified
        if (request.getItems() == null) {
            String errorMessage = "No fulfilment item specified.";
            LOGGER.error(errorMessage);
            throw AppErrors.INSTANCE.common(errorMessage).exception();
        }
    }

    @Override
    public void distill(FulfilmentRequest request) {
        for (FulfilmentItem item : request.getItems()) {
            Offer offer = catalogGateway.getOffer(item.getOfferId(), item.getTimestamp());
            item.setActions(new ArrayList<FulfilmentAction>());

            _distill(offer, item.getQuantity(), item);
        }
    }

    @Override
    public void store(final FulfilmentRequest request) {
        executeInNewTransaction(new Callback() {
            @Override
            public void apply() {
                // store fulfilment request
                fulfilmentRequestRepo.create(request);

                // store fulfilment items
                for (FulfilmentItem item : request.getItems()) {
                    item.setRequestId(request.getRequestId());
                    fulfilmentRepo.create(item);

                    if (item.getActions() == null) {
                        continue;
                    }

                    // store fulfilment actions
                    for (FulfilmentAction action : item.getActions()) {
                        action.setFulfilmentId(item.getFulfilmentId());
                        action.setStatus(FulfilmentStatus.PENDING);
                        fulfilmentActionRepository.create(action);
                    }
                }
            }
        });
    }

    @Override
    public ClassifyResult classify(FulfilmentRequest request) {
        ClassifyResult result = new ClassifyResult();

        for (FulfilmentItem item : request.getItems()) {
            for (FulfilmentAction action : item.getActions()) {
                result.addFulfilmentAction(action);
            }
        }

        return result;
    }

    @Override
    public void dispatch(FulfilmentRequest request, ClassifyResult classifyResult) {
        Map<Long, FulfilmentItem> items = ModelUtils.buildFulfilmentItemMap(request);

        for (String actionType : classifyResult.getActionTypes()) {
            FulfilmentContext context = FulfilmentContextFactory.create(actionType);

            context.setItems(items);
            context.setUserId(request.getUserId());
            context.setOrderId(request.getOrderId());
            context.setActions(classifyResult.get(actionType));

            if (Utils.equals(FulfilmentActionType.DELIVER_PHYSICAL_GOODS, actionType)) {
                PhysicalGoodsContext pgContext = (PhysicalGoodsContext) context;

                pgContext.setShippingAddressId(request.getShippingAddressId());
                pgContext.setShippingMethodId(request.getShippingMethodId());
            }

            HandlerRegistry.resolve(actionType).process(context);
        }
    }

    private void _distill(Offer offer, Integer copyCount, FulfilmentItem fulfilmentItem) {
        // process fulfilment actions on current offer
        for (OfferAction action : offer.getActions()) {
            FulfilmentAction fulfilmentAction = new FulfilmentAction();

            fulfilmentAction.setItemId(action.getItemId());
            fulfilmentAction.setType(action.getType());
            fulfilmentAction.setStatus(FulfilmentStatus.PENDING);
            fulfilmentAction.setProperties(action.getProperties());
            fulfilmentAction.setCopyCount(copyCount);
            fulfilmentAction.setTimestamp(fulfilmentItem.getTimestamp());

            // copy offer level items to fulfilment action
            fulfilmentAction.setItems(offer.getItems());

            fulfilmentItem.addFulfilmentAction(fulfilmentAction);
        }

        // expand sub offers
        for (LinkedEntry entry : offer.getSubOffers()) {
            Offer subOffer = catalogGateway.getOffer(entry.getId(), fulfilmentItem.getTimestamp());
            _distill(subOffer, copyCount * entry.getQuantity(), fulfilmentItem);
        }
    }

    private FulfilmentRequest load(Long requestId) {
        FulfilmentRequest request = fulfilmentRequestRepo.get(requestId);
        request.setItems(fulfilmentRepo.findByRequestId(requestId));

        for (FulfilmentItem item : request.getItems()) {
            item.setActions(fulfilmentActionRepository.findByFulfilmentId(item.getFulfilmentId()));
        }

        return request;
    }
}
