package icn.proludic.models;

/**
 * Author:  Bradley Wilson
 * Date: 08/05/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class HomeParkModel {

    private String objectID, name, imageURL;

    public HomeParkModel(String objectID, String name, String imageURL) {
        this.objectID = objectID;
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getObjectID() {
        return objectID;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }


}
