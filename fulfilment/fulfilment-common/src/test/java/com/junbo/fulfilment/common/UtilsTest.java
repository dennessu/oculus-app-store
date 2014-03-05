package com.junbo.fulfilment.common;

import com.junbo.fulfilment.common.util.Utils;
import junit.framework.Assert;
import org.testng.annotations.Test;

public class UtilsTest {
    @Test
    public void testEquals() {
        Assert.assertTrue(Utils.equals("a", "a"));
        Assert.assertFalse(Utils.equals("a", null));
        Assert.assertFalse(Utils.equals(null, "a"));
        Assert.assertTrue(Utils.equals(null, null));
    }

    @Test
    public void testMap() {
        Person1 person1 = new Person1();
        person1.setName("sharkmao");

        Person2 person2 = Utils.map(person1, Person2.class);
        Assert.assertEquals("Person name should match", person1.getName(), person2.getName());
    }
}
