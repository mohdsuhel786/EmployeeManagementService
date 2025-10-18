package com.hrms.user_service.dtos;

import java.util.List;

public class WarehouseDTO {
    private String product;
    private List<String> station;

    public List<String> getStation() {
        return station;
    }

    public void setStation(List<String> station) {
        this.station = station;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "WarehouseDTO{" +
                "product='" + product + '\'' +
                ", station=" + station +
                '}';
    }
}
