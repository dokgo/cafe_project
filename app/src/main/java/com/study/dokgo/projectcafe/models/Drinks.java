package com.study.dokgo.projectcafe.models;

/**
 * Created by dokgo on 29.12.16.
 */


import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Drinks {

    @SerializedName("menu")
    private List<Drink> menu = null;

    public List<Drink> getMenu() {
        return menu;
    }

    public void setMenu(List<Drink> menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        return "";
    }

}