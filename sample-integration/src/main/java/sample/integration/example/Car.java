/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.integration.example;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author dw
 */
public class Car {

    private String manufactory;
    private String model;
    private int year;

    public String getManufactory() {
        return this.manufactory;
    }

    public void setManufactory(String newManufactory) {
        this.manufactory = newManufactory;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String newModel) {
        this.model = newModel;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int newYear) {
        this.year = newYear;
    }

    public boolean calculateDiscard() {
        int now = Calendar.getInstance().get(Calendar.YEAR);
        return now - this.year > 20;
    }
}
