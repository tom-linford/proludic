package icn.proludic.models;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Author:  Bradley Wilson
 * Date: 25/04/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class WorkoutsModel {

    private JSONArray exercises;
    private String name, objectId;
    private boolean isAvailable, isFavourited, isBranded;
    private int uses;
    private String brandImage, brandName;

    public WorkoutsModel(String objectId, String name, JSONArray exercises, boolean isAvailable, boolean isFavourited) {
        this.exercises = exercises;
        this.name = name;
        this.isAvailable = isAvailable;
        this.objectId = objectId;
        this.isFavourited = isFavourited;
    }

    public WorkoutsModel(String objectId, String name, JSONArray exercises, boolean isAvailable, boolean isFavourited, int uses) {
        this.exercises = exercises;
        this.name = name;
        this.isAvailable = isAvailable;
        this.objectId = objectId;
        this.isFavourited = isFavourited;
        this.uses = uses;
    }

    public String getBrandImage() {
        return brandImage;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandInfo(String brandImage, String brandName) {
        this.isBranded = true;
        this.brandImage = brandImage;
        this.brandName = brandName;
    }

    public boolean getBranded() { return isBranded; }

    public JSONArray getExercises() {
        return exercises;
    }

    public String getName() {
        return name;
    }

    public boolean getAvailability() { return isAvailable; }

    public String getObjectId() {
        return objectId;
    }

    public boolean getFavourited() {
        return isFavourited;
    }

    public int getUses() {return uses;}
}
