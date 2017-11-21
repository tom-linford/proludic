package icn.proludic.models;

/**
 * Author:  Bradley Wilson
 * Date: 22/05/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class FriendRequestsModel {

    private String imageUrl, name, objectId, requestObjectId;
    private boolean isPending, isAccepted, isChallenge, isWeight;
    private int length;

    public FriendRequestsModel(String objectId, String requestObjectId, String imageUrl, String name, boolean isPending, boolean isAccepted, boolean isChallenge, boolean isWeight, int length) {
        this.objectId = objectId;
        this.requestObjectId = requestObjectId;
        this.imageUrl = imageUrl;
        this.name = name;
        this.isPending = isPending;
        this.isAccepted = isAccepted;
        this.isChallenge = isChallenge;
        this.isWeight = isWeight;
        this.length = length;
    }

    public boolean isChallenge() {
        return isChallenge;
    }

    public boolean isWeight() {
        return isWeight;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public boolean isPending() {
        return isPending;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getRequestObjectId() {
        return requestObjectId;
    }

    public int getLength() {return length;}
}
