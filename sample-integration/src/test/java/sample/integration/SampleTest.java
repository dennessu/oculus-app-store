/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.integration;

import sample.integration.example.Car;
import sample.integration.helper.AsyncHttpClientHelper;
import sample.integration.helper.CommonHelper;

import org.testng.annotations.*;
import static org.testng.AssertJUnit.*;

/**
 *
 * @author dw
 */
public class SampleTest {

    private Car car = new Car();

    @BeforeClass
    public void setUp() {
        car.setManufactory("Ford");
        car.setModel("lazer");
        car.setYear(2000);
    }

    @Test(groups = "pre-setup")
    public void carNoNeedDiscard() {
        assertEquals("needs to discard", false, car.calculateDiscard());
    }

    @Test(groups = "runtime-setup")
    public void carNeedDiscard() {
        Car car1 = new Car();
        car1.setManufactory("Ford");
        car1.setModel("lazer");
        car1.setYear(1990);
        assertEquals("needs to discard", true, car1.calculateDiscard());
    }

    @Test(groups = "catalog")
    public void testCategoryDao() {
        String categoryName = CommonHelper.randomAlphabetic(10);

        AsyncHttpClientHelper clientHelper = new AsyncHttpClientHelper();
        String response = clientHelper.simplePost(
                "http://localhost:8081/rest/categories",
                "{\"name\":\"" + categoryName + "\"}");
        System.out.println(response);
        String[] results = response.split(",");
        String entityId = null;
        for (String s : results) {
            if (s.contains("entityId")) {
                entityId = s.replace("{\"entityId\":\"", "").replace(
                        "\"", "").trim();
                System.out.println(entityId);
            }
        }

        response = clientHelper.simpleGet(
                "http://localhost:8081/rest/categories/" + entityId + "/draft");
        System.out.println(response);
        results = response.split(",");
        boolean found = false;
        for (String s : results) {
            if (s.contains("name")) {
                assertEquals("get created category " + categoryName,
                        true, s.contains(categoryName));
                found = true;
                break;
            }
        }
        assertEquals("found created category " + categoryName,
                true, found);
    }
}
