/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.util;

import com.junbo.email.common.util.Utils;
import com.junbo.email.core.provider.Request;
import com.junbo.email.core.provider.model.mandrill.MandrillRequest;
import com.junbo.email.core.provider.model.mandrill.RequestModel;
import com.junbo.email.db.entity.EmailHistoryEntity;
import com.junbo.email.spec.model.Email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convert util.
 */
public class Convert {
    private static final String KEY="lLE0r20IxOJ5HrIxa3kjIA";
    private static final String URI="https://mandrillapp.com/api/1.0/messages/send-template";
    private static final String TO_TYPE="to";
    private static final String VARS_NAME ="name";
    private static final String VARS_CONTENT="content";

    private Convert(){

    }

    public static Request toRequest(EmailHistoryEntity entity) {
        MandrillRequest request = new MandrillRequest();
        request.setUri(URI);
        RequestModel model = new RequestModel();
        RequestModel.Message message = model.new Message();
        if(entity.getRecipient() != null) {
            List<String> recipients = Utils.toObject(entity.getRecipient(), List.class);
            List<RequestModel.Message.To> toList = new ArrayList<RequestModel.Message.To>();
            for(String recipient : recipients) {
                RequestModel.Message.To to = message.new To();
                to.setEmail(recipient);
                to.setType(TO_TYPE);
                toList.add(to);
            }
            message.setToList(toList);
        }
        Email email = Utils.toObject(entity.getPayload(), Email.class);
        if(email.getProperties() != null) {
            List<Map<String, String>> properties = new ArrayList<Map<String, String>>();
            Map<String, String> map = email.getProperties();
            for(String key : map.keySet()) {
                Map<String,String> temp = new HashMap<String, String>();
                temp.put(VARS_NAME,key);
                temp.put(VARS_CONTENT,map.get(key));
                properties.add(temp);
            }
            message.setProperties(properties);
        }
        model.setKey(KEY);
        String templateName = String.format("%s.%s.%s",entity.getSource(),entity.getAction(),entity.getLocale());
        model.setTemplateName(templateName);
        model.setTemplateContent(new HashMap<String,String>(){});
        model.setMessage(message);
        request.setJson(Utils.toJson(model));
        return request;
    }
}
