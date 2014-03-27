/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.async;

/**
 * MyContext.
 */
public class MyContext {
    private static ThreadLocal<MyContext> current = new ThreadLocal<>();

    private String name;
    private Integer age;
    private Long salary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public static MyContext current() {
        MyContext ctx = current.get();
        if (ctx == null) {
            ctx = new MyContext();
            current.set(ctx);
        }

        return ctx;
    }
}
