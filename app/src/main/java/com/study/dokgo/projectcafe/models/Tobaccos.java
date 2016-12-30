package com.study.dokgo.projectcafe.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dokgo on 30.12.16.
 */

public class Tobaccos {

    @SerializedName("tobaccos")
    private List<Tobacco> tobaccos = null;

    public List<Tobacco> getTobaccos() {
        return tobaccos;
    }

    public void setTobaccos(List<Tobacco> tobaccos) {
        this.tobaccos = tobaccos;
    }

    @Override
    public String toString() {
        return "";
    }

}
