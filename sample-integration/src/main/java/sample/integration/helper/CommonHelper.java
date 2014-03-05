/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.integration.helper;

import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author dw
 */
public class CommonHelper {

    public static String randomAlphabetic(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

}
