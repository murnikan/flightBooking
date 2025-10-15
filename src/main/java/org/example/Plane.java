package org.example;

public class Plane {
    private String model;
    private String planeCode;
    private int maxPassengersAmount;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMaxPassengersAmount() {
        return maxPassengersAmount;
    }

    public void setMaxPassengersAmount(int maxPassengersAmount) {
        this.maxPassengersAmount = maxPassengersAmount;
    }

    public String getPlaneCode() {
        return planeCode;
    }

    public void setPlaneCode(String planeCode) {
        this.planeCode = planeCode;
    }

    public String getAllInfoPlane(){
        return "Модель самолета " + model + " | Индификационный номенр: " + planeCode + " | максимальное число пассажиров: " + maxPassengersAmount;
    }
}
