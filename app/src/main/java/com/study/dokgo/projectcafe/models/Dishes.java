
package com.study.dokgo.projectcafe.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Dishes {

    @SerializedName("menu")
    @Expose
    private List<Dish> menu = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Dishes() {
    }

    /**
     * 
     * @param menu
     */
    public Dishes(List<Dish> menu) {
        this.menu = menu;
    }

    /**
     * 
     * @return
     *     The menu
     */
    public List<Dish> getMenu() {
        return menu;
    }

    /**
     * 
     * @param menu
     *     The menu
     */
    public void setMenu(List<Dish> menu) {
        this.menu = menu;
    }

}
