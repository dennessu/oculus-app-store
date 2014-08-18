/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.Identity;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.libs.RandomFactory;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weiyu_000 on 8/15/14.
 */
public class UserInfo {
    private static final String pwd = "Test1234";

    private UserInfo() {
        vats = new HashMap<>();
        emails = new ArrayList<>();
        phones = new ArrayList<>();
        addressInfos = new ArrayList<>();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Map<String, String> getVats() {
        return vats;
    }

    public void setVats(Map<String, String> vats) {
        this.vats = vats;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phone) {
        this.phones = phone;
    }

    public List<AddressInfo> getAddressInfos() {
        return addressInfos;
    }

    public void setAddressInfos(List<AddressInfo> addressInfos) {
        this.addressInfos = addressInfos;
    }

    public void addAddress(AddressInfo addressInfo) {
        addressInfos.add(addressInfo);
    }

    public void addVat(String country, String vatId) {
        this.vats.put(country, vatId);
    }

    public void addEmail(String emailAddress) {
        this.emails.add(emailAddress);
    }

    public void addPhone(String phone) {
        this.phones.add(phone);
    }

    /**
     * User Gender.
     */
    public enum Gender {
        male, female;
    }

    private String nickName;
    private String firstName;
    private String lastName;
    private String dob;
    private String userName;
    private String password;
    private String pin;
    private Country country;
    private Gender gender;
    private Map<String, String> vats;
    private List<String> emails;
    private List<String> phones;
    private List<AddressInfo> addressInfos;

    public static UserInfo getRandomUserInfo() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(RandomFactory.getRandomStringOfAlphabet(5));
        userInfo.setFirstName(RandomFactory.getRandomStringOfAlphabet(5));
        userInfo.setLastName(RandomFactory.getRandomStringOfAlphabet(5));
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        userInfo.setDob(format.format(RandomFactory.nextDate().getTime()));
        userInfo.setUserName(RandomFactory.getRandomStringOfAlphabet(6));
        userInfo.setPassword(pwd);
        userInfo.setPin(RandomFactory.getRandomStringOfNumeric(4));
        userInfo.setCountry(Country.DEFAULT);
        userInfo.setGender(Gender.male);
        userInfo.addAddress(AddressInfo.getRandomAddressInfo());
        userInfo.addEmail(URLEncoder.encode(RandomFactory.getRandomEmailAddress(), "UTF-8"));
        return userInfo;
    }

}
