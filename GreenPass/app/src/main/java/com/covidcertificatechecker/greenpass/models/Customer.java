package com.covidcertificatechecker.greenpass.models;

public class Customer {

    String id, name, vacDate, vacCountry, storeId, inTime, outTime, available;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVacDate() {
        return vacDate;
    }

    public void setVacDate(String vacDate) {
        this.vacDate = vacDate;
    }

    public String getVacCountry() {
        return vacCountry;
    }

    public void setVacCountry(String vacCountry) {
        this.vacCountry = vacCountry;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
