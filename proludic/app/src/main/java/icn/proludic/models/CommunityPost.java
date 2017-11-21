package icn.proludic.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author:  Bradley Wilson
 * Date: 26/06/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class CommunityPost implements Serializable {

    private String objectID;
    private String title;
    private String message;
    private String profileImageURL;
    private String profileUsername;
    private String profileTimeAndDate;

    private String userObjectID;
    private int repliesTotal;
    private ArrayList<String> repliesArray;

    public CommunityPost(String objectID, String message, String profileImageURL, String profileUsername, String profileTimeAndDate, String userObjectID, ArrayList<String> replies, int repliesTotal) {
        this.objectID = objectID;
        this.message = message;
        this.profileImageURL = profileImageURL;
        this.profileUsername = profileUsername;
        this.profileTimeAndDate = profileTimeAndDate;
        this.repliesArray = replies;
        this.repliesTotal = repliesTotal;
        this.userObjectID = userObjectID;
    }

    public CommunityPost(String objectID, String title, String message, String profileImageURL, String profileUsername, String profileTimeAndDate, int repliesTotal, ArrayList<String> repliesArray, String userObjectID) {
        this.objectID = objectID;
        this.title = title;
        this.message = message;
        this.profileImageURL = profileImageURL;
        this.profileUsername = profileUsername;
        this.profileTimeAndDate = profileTimeAndDate;
        this.repliesTotal = repliesTotal;
        this.repliesArray = repliesArray;
        this.userObjectID = userObjectID;
    }

    public String getUserObjectID() {
        return userObjectID;
    }

    public String getObjectID() {
        return objectID;
    }

    public ArrayList<String> getRepliesArray() {
        return repliesArray;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getProfileUsername() {
        return profileUsername;
    }

    public String getProfileTimeAndDate() {
        return profileTimeAndDate;
    }

    public int getRepliesTotal() {
        return repliesTotal;
    }
}
