
package com.study.dokgo.projectcafe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Dish {

    @SerializedName("dishId")
    @Expose
    private String dishId;
    @SerializedName("menuId")
    @Expose
    private String menuId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("cuisine")
    @Expose
    private String cuisine;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("portion")
    @Expose
    private String portion;
    @SerializedName("cost")
    @Expose
    private String cost;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Dish() {
    }

    /**
     * 
     * @param time
     * @param cuisine
     * @param name
     * @param menuId
     * @param dishId
     * @param type
     * @param cost
     * @param portion
     */
    public Dish(String dishId, String menuId, String name, String type, String cuisine, String time, String portion, String cost) {
        this.dishId = dishId;
        this.menuId = menuId;
        this.name = name;
        this.type = type;
        this.cuisine = cuisine;
        this.time = time;
        this.portion = portion;
        this.cost = cost;
    }

    /**
     * 
     * @return
     *     The dishId
     */
    public String getDishId() {
        return dishId;
    }

    /**
     * 
     * @param dishId
     *     The dishId
     */
    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    /**
     * 
     * @return
     *     The menuId
     */
    public String getMenuId() {
        return menuId;
    }

    /**
     * 
     * @param menuId
     *     The menuId
     */
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The cuisine
     */
    public String getCuisine() {
        return cuisine;
    }

    /**
     * 
     * @param cuisine
     *     The cuisine
     */
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    /**
     * 
     * @return
     *     The time
     */
    public String getTime() {
        return time;
    }

    /**
     * 
     * @param time
     *     The time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 
     * @return
     *     The portion
     */
    public String getPortion() {
        return portion;
    }

    /**
     * 
     * @param portion
     *     The portion
     */
    public void setPortion(String portion) {
        this.portion = portion;
    }

    /**
     * 
     * @return
     *     The cost
     */
    public String getCost() {
        return cost;
    }

    /**
     * 
     * @param cost
     *     The cost
     */
    public void setCost(String cost) {
        this.cost = cost;
    }

    public int compareToUpPortion(Dish other) {
        int a = Integer.parseInt(this.getPortion());
        int b = Integer.parseInt(other.getPortion());

        return Integer.compare(a, b);
    }

    public int compareToDownPortion(Dish other) {
        int a = Integer.parseInt(this.getPortion());
        int b = Integer.parseInt(other.getPortion());

        return Integer.compare(b, a);
    }

    public int compareToUpCost(Dish other) {
        int a = Integer.parseInt(this.getCost());
        int b = Integer.parseInt(other.getCost());

        return Integer.compare(a, b);
    }

    public int compareToDownCost(Dish other) {
        int a = Integer.parseInt(this.getCost());
        int b = Integer.parseInt(other.getCost());

        return Integer.compare(b, a);
    }

}
