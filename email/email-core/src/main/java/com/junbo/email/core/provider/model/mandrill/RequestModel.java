/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.provider.model.mandrill;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 * RequestModel.
 */
public class RequestModel {

    private String key;

    @JSONField(name="template_name")
    private String templateName;

    @JSONField(name="template_content")
    private Map<String,String> templateContent;

    private Message message;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, String> getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(Map<String, String> templateContent) {
        this.templateContent = templateContent;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Message Model.
     */
    public class Message {

        private String subject;

        @JSONField(name="from_email")
        private String fromEmail;

        @JSONField(name="from_name")
        private String fromName;

        @JSONField(name="to")
        private List<To> toList;

        @JSONField(name="global_merge_vars")
        private List<Map<String,String>> properties;

        public String getSubject() {
            return subject;
        }
        public void setSubject(String subject) {
            this.subject = subject;
        }
        public String getFromEmail() {
            return fromEmail;
        }
        public void setFromEmail(String fromEmail) {
            this.fromEmail = fromEmail;
        }
        public String getFromName() {
            return fromName;
        }
        public void setFromName(String fromName) {
            this.fromName = fromName;
        }
        public List<To> getToList() {
            return toList;
        }
        public void setToList(List<To> toList) {
            this.toList = toList;
        }
        public List<Map<String, String>> getProperties() {
            return properties;
        }
        public void setProperties(List<Map<String, String>> properties) {
            this.properties = properties;
        }

        /**
         * To Model.
         */
        public class To{

            private String email;

            private String name;

            private String type;

            public String getEmail() {
                return email;
            }
            public void setEmail(String email) {
                this.email = email;
            }
            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }
            public String getType() {
                return type;
            }
            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
