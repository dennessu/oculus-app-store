package com.junbo.fulfilment.common;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

public class JSONTest {
    @Test
    public void testDemo() {
        String testUserName = "sharkmao";

        Person person = new Person();
        person.setName(testUserName);
        person.setAge(999);

        String json = JSON.toJSONString(person);
        Assert.notNull(json, "JSON string should not be null.");

        String name = JsonPath.read(json, "name");
        Assert.isTrue(testUserName.equals(name), "Name should match.");
    }
}

class Person {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
