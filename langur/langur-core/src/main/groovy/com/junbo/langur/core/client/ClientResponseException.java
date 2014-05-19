/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.client;

import com.ning.http.client.Response;

import java.io.IOException;

/**
 * Java doc.
 */
public class ClientResponseException extends RuntimeException {

    private final Response response;

    public ClientResponseException(Response response) {
        super(generateMessage(null, response));

        this.response = response;
    }

    public ClientResponseException(String message, Response response) {
        super(generateMessage(message, response));

        this.response = response;
    }

    public ClientResponseException(String message, Response response, Throwable t) {
        super(generateMessage(message, response), t);

        this.response = response;
    }

    private static String generateMessage(String message, Response response) {
        if (response == null) {
            return message;
        }

        // more detailed messages.
        String responseText = "HTTP " + response.getStatusCode() + ' ' + response.getStatusText();

        try {
            responseText += ' ' + response.getResponseBody();
        } catch(IOException ex) {
            responseText += ' ' + response.toString();
        }

        return message + " Response: " + responseText;
    }

    public Response getResponse(){
        return response;
    }
}
