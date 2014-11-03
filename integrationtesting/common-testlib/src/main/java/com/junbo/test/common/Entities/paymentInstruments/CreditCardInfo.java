/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.paymentInstruments;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.PaymentType;
import com.junbo.test.common.libs.RandomFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by Yunlong on 3/25/14.
 */
public class CreditCardInfo extends PaymentInstrumentBase {
    private String expireDate;
    private String encryptedCVMCode;

    public CreditCardInfo() {
        this.setType(PaymentType.CREDITCARD);
    }

    public String getExpireDate() {
        return expireDate;
    }

    public String getEncryptedCVMCode() {
        return encryptedCVMCode;
    }

    public void setEncryptedCVMCode(String encryptedCVMCode) {
        this.encryptedCVMCode = encryptedCVMCode;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public static CreditCardInfo getRandomCreditCardInfo(Country country) {
        CreditCardInfo randomCreditCard = new CreditCardInfo();
        randomCreditCard.setAccountName(RandomFactory.getRandomStringOfAlphabet(5));

        String[] creditCardArray = new String[]{"4111111111111111"};
        randomCreditCard.setAccountNum(creditCardArray[RandomFactory.getRandomInteger(1)]);
        //randomCreditCard.setAccountNum(CreditCardGenerator.VISA.getRandomNumber());
        randomCreditCard.setEncryptedCVMCode(getCVMCode(CreditCardGenerator.VISA.toString(), true));
        randomCreditCard.setExpireDate("2016-06-01");
        //randomCreditCard.setPhone(Phone.getRandomPhone());
        randomCreditCard.setAddress(Address.getRandomAddress(country));
        randomCreditCard.setValidated(false);
        randomCreditCard.setDefault(true);
        return randomCreditCard;
    }

    public static CreditCardInfo getRandomCreditCardInfo(Address address){
        CreditCardInfo randomCreditCard = new CreditCardInfo();
        randomCreditCard.setAccountName(RandomFactory.getRandomStringOfAlphabet(5));

        String[] creditCardArray = new String[]{"4111111111111111", "4012888888881881"};
        randomCreditCard.setAccountNum(creditCardArray[RandomFactory.getRandomInteger(1)]);
        //randomCreditCard.setAccountNum(CreditCardGenerator.VISA.getRandomNumber());
        randomCreditCard.setEncryptedCVMCode(getCVMCode(CreditCardGenerator.VISA.toString(), true));
        randomCreditCard.setExpireDate(getFormattedExpireDate());
        //randomCreditCard.setPhone(Phone.getRandomPhone());
        randomCreditCard.setAddress(address);
        randomCreditCard.setValidated(false);
        randomCreditCard.setDefault(true);
        return randomCreditCard;
    }

    public static CreditCardInfo getExpiredCreditCardInfo(Country country){
        CreditCardInfo randomCreditCard = new CreditCardInfo();
        randomCreditCard.setAccountName(RandomFactory.getRandomStringOfAlphabet(5));

        String[] creditCardArray = new String[]{"4111111111111111", "4012888888881881"};
        randomCreditCard.setAccountNum(creditCardArray[RandomFactory.getRandomInteger(1)]);
        //randomCreditCard.setAccountNum(CreditCardGenerator.VISA.getRandomNumber());
        randomCreditCard.setEncryptedCVMCode(getCVMCode(CreditCardGenerator.VISA.toString(), true));
        randomCreditCard.setExpireDate(getFormattedExpiredDate());
        //randomCreditCard.setPhone(Phone.getRandomPhone());
        randomCreditCard.setAddress(Address.getRandomAddress(country));
        randomCreditCard.setValidated(false);
        randomCreditCard.setDefault(true);
        return randomCreditCard;
    }


    /**
     * Enum for Credit Card Type.
     *
     * @author Yunlongzhao
     */
    public enum CreditCardGenerator {
        VISA("4", 16),
        VISA_DELTA("4", 16),
        VISA_ELECTRON("4", 16),
        DANKORT("4", 16),
        CARTE_SI("4", 16),
        AMERICAN_EXPRESS("37", 15),
        MASTERCARD("54", 16),
        DISCOVER("60110", 16),
        JCB("3528", 16),
        SOLO("6333", 16),
        CARTE_BLEUE("5817", 16),
        MAESTRO("5641", 16);

        private String prefix;
        private int numDigits;

        private CreditCardGenerator(String prefix, int numDigits) {
            this.prefix = prefix;
            this.numDigits = numDigits;
        }

        public String getRandomNumber() {
            StringBuffer ccNumber = new StringBuffer();
            String marker = "666666";
            //add the prefix
            ccNumber.append(prefix);
            ccNumber.append(marker);
            Random random = new Random();

            while (ccNumber.length() < numDigits) {
                ccNumber.append(Character.forDigit(random.nextInt(10), 10));
            }

            int val = 0;
            int multiplier = 1;
            int checkSum = 0;

            for (int index = ccNumber.length() - 1; index >= 0; index--) {
                val = multiplier * Character.getNumericValue(ccNumber.charAt(index));

                if (val > 9) {
                    val -= 9;
                }

                checkSum += val;

                multiplier = (multiplier == 1) ? 2 : 1;
            }
            //adjust so that the final digit causes the checkSum remainder to be zero
            int remainder = checkSum % 10;
            int lastDigit = Character.getNumericValue(ccNumber.charAt(ccNumber.length() - 1));
            if (remainder > 0) {
                if (lastDigit >= remainder) {
                    lastDigit = lastDigit - remainder;
                } else {
                    lastDigit = lastDigit + (10 - remainder);
                }
            }
            ccNumber.setCharAt(ccNumber.length() - 1, Character.forDigit(lastDigit, 10));
            return ccNumber.toString();
        }
    }

    public static String getCVMCode(String cardType, boolean validCVC) {
        String cvcNum;
        if (validCVC) {
            if (cardType.equals("AMERICAN_EXPRESS")) {
                cvcNum = RandomFactory.getRandomStringOfNumeric(4);
            } else {
                cvcNum = RandomFactory.getRandomStringOfNumeric(3);
            }
        } else {
            if (cardType.equals("AMERICAN_EXPRESS")) {
                cvcNum = RandomFactory.getRandomStringOfNumeric(3);
            } else {
                cvcNum = RandomFactory.getRandomStringOfNumeric(4);
            }
        }
        return cvcNum;
    }


    public static String getFormattedExpireDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Date expireDate = new Date(today.getYear() + RandomFactory.getRandomInteger(2, 5), today.getMonth()
                + RandomFactory.getRandomInteger(0, 11), today.getDay());
        return dateFormat.format(expireDate);
    }

    public static String getFormattedExpiredDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Date expireDate = new Date(today.getYear() , today.getMonth()
                - 1, today.getDay());
        return dateFormat.format(expireDate);
    }

}
