/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.service;

import com.junbo.fulfilment.clientproxy.CatalogGateway;
import com.junbo.fulfilment.common.exception.FulfilmentException;
import com.junbo.fulfilment.common.exception.ResourceNotFoundException;
import com.junbo.fulfilment.common.util.Callback;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.core.FulfilmentService;
import com.junbo.fulfilment.core.FulfilmentSupport;
import com.junbo.fulfilment.core.context.FulfilmentContext;
import com.junbo.fulfilment.core.context.FulfilmentContextFactory;
import com.junbo.fulfilment.core.context.PhysicalGoodsContext;
import com.junbo.fulfilment.core.handler.HandlerRegistry;
import com.junbo.fulfilment.db.repo.FulfilmentActionRepository;
import com.junbo.fulfilment.db.repo.FulfilmentRepository;
import com.junbo.fulfilment.db.repo.FulfilmentRequestRepository;
import com.junbo.fulfilment.spec.constant.FulfilmentActionType;
import com.junbo.fulfilment.spec.constant.FulfilmentStatus;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;
import com.junbo.fulfilment.spec.fusion.Offer;
import com.junbo.fulfilment.spec.fusion.OfferAction;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * FulfilmentServiceImpl.
 */
public class FulfilmentServiceImpl extends TransactionSupport implements FulfilmentService, FulfilmentSupport {
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
        Long requestId = fulfilmentRequestRepo.existTrackingGuid(request.getTrackingGuid());
        if (requestId != null) {
            return retrieveRequest(requestId);
        }

        // ensure billing order id doesn't exist
        if (fulfilmentRequestRepo.existBillingOrderId(request.getOrderId()) != null) {
            throw new FulfilmentException("BillingOrderId [" + request.getOrderId() + "] already exists.");
        }

        // distill fulfilment actions
        distill(request);

        // persist fulfilment request
        store(request);

        // classify fulfilment actions
        ClassifyResult classifyResult = classify(request);

        // process fulfilment actions
        dispatch(request, classifyResult);

        return request;
    }

    @Override
    public FulfilmentRequest retrieveRequest(Long requestId) {
        return load(requestId);
    }

    @Override
    public FulfilmentRequest retrieveRequestByBillingOrderId(Long billingOrderId) {
        Long requestId = fulfilmentRequestRepo.existBillingOrderId(billingOrderId);
        if (requestId == null) {
            throw new ResourceNotFoundException("Fulfilment request with BillingOrderId ["
                    + billingOrderId + "] does not exist.");
        }

        return load(requestId);
    }

    @Override
    public FulfilmentItem retrieveFulfilmentItem(Long fulfilmentId) {
        FulfilmentItem item = fulfilmentRepo.get(fulfilmentId);
        if (item == null) {
            throw new ResourceNotFoundException("Fulfilment item with id [" + fulfilmentId + "] does not exist");
        }

        return item;
    }

    public void distill(FulfilmentRequest request) {
        for (FulfilmentItem item : request.getItems()) {
            Offer offer = catalogGateway.getOffer(item.getOfferId(), item.getTimestamp());
            distill(offer, item.getQuantity(), item);
        }
    }

    private void distill(Offer offer, Integer copyCount, FulfilmentItem fulfilmentItem) {
        // process fulfilment actions on current offer
        for (OfferAction action : offer.getActions()) {
            FulfilmentAction fulfilmentAction = new FulfilmentAction();

            fulfilmentAction.setType(action.getType());
            fulfilmentAction.setStatus(FulfilmentStatus.PENDING);
            fulfilmentAction.setProperties(action.getProperties());
            fulfilmentAction.setCopyCount(copyCount);

            if (Utils.equals(FulfilmentActionType.DELIVER_PHYSICAL_GOODS, action.getType())) {
                fulfilmentAction.setItems(offer.getItems());
            }

            fulfilmentItem.addFulfilmentAction(fulfilmentAction);
        }

        // expand sub offers
        for (LinkedEntry entry : offer.getSubOffers()) {
            Offer subOffer = catalogGateway.getOffer(entry.getId());
            distill(subOffer, copyCount * entry.getQuantity(), fulfilmentItem);
        }
    }

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

    public ClassifyResult classify(FulfilmentRequest request) {
        ClassifyResult result = new ClassifyResult();

        for (FulfilmentItem item : request.getItems()) {
            for (FulfilmentAction action : item.getActions()) {
                result.addFulfilmentAction(action);
            }
        }

        return result;
    }

    public void dispatch(FulfilmentRequest request, ClassifyResult classifyResult) {
        for (String actionType : classifyResult.getActionTypes()) {
            FulfilmentContext context = FulfilmentContextFactory.create(actionType);

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

    private FulfilmentRequest load(Long requestId) {
        FulfilmentRequest request = fulfilmentRequestRepo.get(requestId);
        request.setItems(fulfilmentRepo.findByRequestId(requestId));

        for (FulfilmentItem item : request.getItems()) {
            item.setActions(fulfilmentActionRepository.findByFulfilmentId(item.getFulfilmentId()));
        }

        return request;
    }
}
