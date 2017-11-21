package icn.proludic.models;

import android.icu.text.PluralFormat;

/**
 * Author:  Bradley Wilson
 * Date: 16/05/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class LeaderboardModel {

    private String name, username, type;
    private int total;

    public LeaderboardModel(String name, int total, String username, String type) {
        this.name = name;
        this.total = total;
        this.username = username;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getTotal() {
        return total;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }
}
