package icn.proludic.models;

import android.content.Context;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;

import icn.proludic.misc.Constants;

/**
 * Author:  Bradley Wilson
 * Date: 15/05/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class FriendsModel implements Serializable {

    private String name;
    private String objectId;
    private String username;
    private int hearts;
    private String description;
    private String homePark;
    private String location = "";
    private Object profilePicture;

    public FriendsModel (String objectId, String name, String username, Object profilePicUrl, String description, int hearts, String homePark) {
        this.objectId = objectId;
        this.name = name;
        this.username = username;
        this.profilePicture = profilePicUrl;
        this.description = description;
        this.hearts = hearts;
        this.homePark = homePark;
    }

    public String getName() {
        return name;
    }

    public Object getProfilePicture() {
        return profilePicture;
    }

    public String getObjectId() { return objectId; }

    public String getUsername() { return username; }

    public String getDescription() {
        return description;
    }

    public int getHearts() { return hearts; }

    public String getHomePark() { return homePark; }

}
