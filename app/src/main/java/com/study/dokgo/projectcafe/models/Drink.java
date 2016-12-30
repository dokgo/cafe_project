package com.study.dokgo.projectcafe.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dokgo on 29.12.16.
 */

public class Drink {

    @SerializedName("drinkId")
    private String drinkId;
    @SerializedName("cafeId")
    private String cafeId;
    @SerializedName("name")
    private String name;
    @SerializedName("cost")
    private String cost;
    @SerializedName("volume")
    private String volume;

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }

    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "";
    }

    public int compareToName(Drink other) {
        String a = this.getName();
        String b = other.getName();

        return a.compareTo(b);
    }

    public int compareToCost(Drink other) {
        int a = Integer.parseInt(this.getCost());
        int b = Integer.parseInt(other.getCost());

        return Integer.compare(a, b);
    }


    public int compareToVolume(Drink other) {
        int a = Integer.parseInt(this.getVolume());
        int b = Integer.parseInt(other.getVolume());

        return Integer.compare(a, b);
    }

}