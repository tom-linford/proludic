package icn.proludic.models;

/**
 * Author:  Bradley Wilson
 * Date: 25/04/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class ExercisesModel {

    private String name, imageURL, videoURL, description, objectId;
    private boolean isAvailable, isFavourited;
    private int uses;

    public ExercisesModel(String imageURL, String videoURL) {
        this.imageURL = imageURL;
        this.videoURL = videoURL;
    }
    public ExercisesModel(String objectId, String name, String imageURL, String videoURL, String description, boolean isAvailable, boolean isFavourited) {
        this.name = name;
        this.imageURL = imageURL;
        this.videoURL = videoURL;
        this.description = description;
        this.isAvailable = isAvailable;
        this.objectId = objectId;
        this.isFavourited = isFavourited;
    }

    public ExercisesModel(String objectId, String name, String imageURL, String videoURL, String description, boolean isAvailable, boolean isFavourited, int uses) {
        this.name = name;
        this.imageURL = imageURL;
        this.videoURL = videoURL;
        this.description = description;
        this.isAvailable = isAvailable;
        this.objectId = objectId;
        this.isFavourited = isFavourited;
        this.uses = uses;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getDescription() {
        return description;
    }

    public boolean getAvailability() {return isAvailable;}

    public String getObjectId() {
        return objectId;
    }

    public boolean getFavourited() {
        return isFavourited;
    }

    public int getUses() {
        return uses;
    }
}
