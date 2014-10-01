/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson;

import com.junbo.common.cloudant.CloudantMarshaller;
import com.junbo.common.cloudant.DefaultCloudantMarshaller;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.webflow.state.Conversation;
import com.junbo.langur.core.webflow.state.FlowState;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;


/**
 * JacksonCustomizationTest.
 */
public class CloudantMarshallerTest {
    private CloudantMarshaller mapper = DefaultCloudantMarshaller.instance();

    @Test
    public void testFlowState() throws Exception {
        // prepare data
        MyResourceMeta myResourceMeta = new MyResourceMeta();
        myResourceMeta.setId(new UserId(12345L));
        myResourceMeta.setUsername("hehe");

        FlowState flowState = new FlowState();
        Map<String, Object> states = new HashMap<>();
        flowState.setScope(states);

        states.put("test", myResourceMeta);
        states.put("test1", 123);

        // do serialization
        String json = mapper.marshall(flowState);

        FlowState state2 = mapper.unmarshall(json, FlowState.class);
        String json2 = mapper.marshall(state2);

        assertEquals(json, json2);

        MyResourceMeta resourceMeta2 = (MyResourceMeta)state2.getScope().get("test");
        assertEquals(resourceMeta2.getId(), new UserId(12345L));
        assertEquals(resourceMeta2.getUsername(), "hehe");
    }

    @Test
    public void testConversation() throws Exception {
        // prepare data
        MyResourceMeta myResourceMeta = new MyResourceMeta();
        myResourceMeta.setId(new UserId(12345L));
        myResourceMeta.setUsername("hehe");

        Conversation conversation = new Conversation();
        Map<String, Object> states = new HashMap<>();
        conversation.setScope(states);

        states.put("test", myResourceMeta);
        states.put("test1", 123);

        List<FlowState> flowStack = new ArrayList<>();
        conversation.setFlowStack(flowStack);

        FlowState flowState = new FlowState();
        flowStack.add(flowState);

        Map<String, Object> states2 = new HashMap<>();
        flowState.setScope(states2);

        states2.put("test", myResourceMeta);
        states2.put("test1", 124);


        // do serialization
        String json = mapper.marshall(conversation);

        Conversation conv2 = mapper.unmarshall(json, Conversation.class);
        String json2 = mapper.marshall(conv2);

        assertEquals(json, json2);

        MyResourceMeta resourceMeta2 = (MyResourceMeta)conv2.getScope().get("test");
        assertEquals(resourceMeta2.getId(), new UserId(12345L));
        assertEquals(resourceMeta2.getUsername(), "hehe");

        assertEquals(conv2.getFlowStack().get(0).getScope().get("test1"), 124);
    }
}
