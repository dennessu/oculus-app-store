/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.rest;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.drm.core.service.DrmService;
import com.junbo.drm.core.service.SignatureService;
import com.junbo.drm.spec.model.License;
import com.junbo.drm.spec.model.SignedLicense;
import com.junbo.drm.spec.resource.DrmResource;
import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

/**
 * DrmResourceImpl.
 */
public class DrmResourceImpl implements DrmResource {
    private DrmService drmService;

    private SignatureService signatureService;

    private MessageTranscoder messageTranscoder;

    @Required
    public void setDrmService(DrmService drmService) {
        this.drmService = drmService;
    }

    @Required
    public void setSignatureService(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @Required
    public void setMessageTranscoder(MessageTranscoder messageTranscoder) {
        this.messageTranscoder = messageTranscoder;
    }

    @Override
    public Promise<SignedLicense> postLicense(License request) {
        validateRequest(request);
        License license = drmService.createLicense(new UserId(request.getUserId()), new ItemId(request.getItemId()),
                request.getMachineHash());
        byte[] signature = signatureService.sign(messageTranscoder.encode(license));

        SignedLicense signedLicense = new SignedLicense();
        signedLicense.setLicense(license);
        signedLicense.setSignature(new String(signature));

        return Promise.pure(signedLicense);
    }

    private void validateRequest(License request) {
        if (request.getUserId() == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("userId").exception();
        }

        if (StringUtils.isEmpty(request.getItemId())) {
            throw AppCommonErrors.INSTANCE.fieldRequired("itemId").exception();
        }
    }
}
