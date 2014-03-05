package com.junbo.fulfilment.common.collection;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class MapTest {
    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "Key.*not found.*")
    public void testSevereMap() {
        Map<String, String> props = new HashMap();
        props.put("key1", "value1");

        Map<String, String> wrapper = new SevereMap(props);
        Assert.assertEquals(wrapper.get("key1"), "value1", "value should match.");

        wrapper.get("not_exist_key");
    }

    @Test
    public void testCountingMap() {
        Counter counter = new Counter();
        counter.count("key1", 10);
        counter.count("key1", 30);
        counter.count("key3", 10);

        Assert.assertEquals(counter.get("key1"), new Integer(40), "value should match.");
        Assert.assertEquals(counter.get("key2"), new Integer(0), "value should match.");
        Assert.assertEquals(counter.get("key3"), new Integer(10), "value should match.");
    }

    @Test
    public void testClassifyMap() {
        Classifier<Integer> classifier = new Classifier();
        classifier.classify("key1", new Integer(1));
        classifier.classify("key2", new Integer(2));
        classifier.classify("key1", new Integer(3));

        Assert.assertEquals(classifier.get("key1").size(), 2, "size should match.");
        Assert.assertEquals(classifier.get("key2").size(), 1, "size should match.");
    }
}
