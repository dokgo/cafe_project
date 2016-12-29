
package com.study.dokgo.projectcafe.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cafes {

    @SerializedName("cafes")
    @Expose
    private List<Cafe> cafes = new ArrayList<>();

    /**
     * 
     * @return
     *     The cafes
     */
    public List<Cafe> getCafes() {
        return cafes;
    }

    /**
     * 
     * @param cafes
     *     The cafes
     */
    public void setCafes(List<Cafe> cafes) {
        this.cafes = cafes;
    }

}
