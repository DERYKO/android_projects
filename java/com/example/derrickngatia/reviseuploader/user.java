package com.example.derrickngatia.reviseuploader;

/**
 * Created by DERRICK NGATIA on 10/8/2017.
 */
public class user {

    public  String unit_name;
    public  String unit_code;
    public  String year_sem;

    public user() {
    }

    public user(String unit_name, String unit_code, String year_sem) {
        this.unit_name = unit_name;
        this.unit_code = unit_code;
        this.year_sem = year_sem;
    }


    public String getUnit_name() {
        return unit_name;
    }

    public String getUnit_code() {
        return unit_code;
    }

    public String getYear_sem() {
        return year_sem;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public void setUnit_code(String unit_code) {
        this.unit_code = unit_code;
    }

    public void setYear_sem(String year_sem) {
        this.year_sem = year_sem;
    }
}

