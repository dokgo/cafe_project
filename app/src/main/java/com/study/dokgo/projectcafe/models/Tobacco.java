package com.study.dokgo.projectcafe.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dokgo on 30.12.16.
 */

public class Tobacco {

    @SerializedName("tobaccoId")
    private String tobaccoId;
    @SerializedName("cafeId")
    private String cafeId;
    @SerializedName("name")
    private String name;
    @SerializedName("line")
    private String line;
    @SerializedName("price")
    private String price;

    public String getTobaccoId() {
        return tobaccoId;
    }

    public void setTobaccoId(String tobaccoId) {
        this.tobaccoId = tobaccoId;
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

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return getName();
    }

    public int compareToName(Tobacco other) {
        String a = this.getName();
        String b = other.getName();

        return a.compareTo(b);
    }

    public int compareToPrice(Tobacco other) {
        int a = Integer.parseInt(this.getPrice());
        int b = Integer.parseInt(other.getPrice());

        return Integer.compare(b, a);
    }

}