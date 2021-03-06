
package com.study.dokgo.projectcafe.models;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class Cafe {

    @SerializedName("cafeId")
    private String cafeId;
    @SerializedName("adress")
    private String adress;
    @SerializedName("rank")
    private String rank;
    @SerializedName("description")
    private String description;
    @SerializedName("name")
    private String name;
    @SerializedName("src")
    private String src;
    @SerializedName("avg")
    private String avg;

    public Cafe(
            String id,
            String adress,
            String rank,
            String description,
            String name,
            String src
    ) {
        setCafeId(id);
        setAddress(adress);
        setRank(rank);
        setDescription(description);
        setName(name);
        setSrc(src);
    }


    /**
     * @return The cafeId
     */
    public String getCafeId() {
        return cafeId;
    }

    /**
     * @param cafeId The cafeId
     */
    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }

    /**
     * @return The adress
     */
    public String getAdress() {
        return adress;
    }

    /**
     * @param address The address
     */
    public void setAddress(String address) {
        this.adress = address;
    }

    /**
     * @return The rank
     */
    public String getRank() {
        return rank;
    }

    /**
     * @param rank The rank
     */
    public void setRank(String rank) {
        this.rank = rank;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The src
     */
    public String getSrc() {
        return src;
    }

    /**
     * @param src The src
     */
    public void setSrc(String src) {
        this.src = src;
    }


    /**
     * @return The avg
     */
    public String getAvg() {
        return avg;
    }

    /**
     * @param avg The src
     */
    public void setAvg(String avg) {
        this.avg = avg;
    }

    @Override
    public String toString() {
        return String.format(
                Locale.ENGLISH,
                "id: %d, Name: %s, description: %s",
                Integer.parseInt(getCafeId()),
                getName(),
                getDescription()
        );
    }

    public int compareToUp(Cafe other) {
        int a = Integer.parseInt(this.getRank());
        int b = Integer.parseInt(other.getRank());

        return Integer.compare(a, b);
    }

    public int compareToDown(Cafe other) {
        int a = Integer.parseInt(this.getRank());
        int b = Integer.parseInt(other.getRank());

        return Integer.compare(b, a);
    }

}
