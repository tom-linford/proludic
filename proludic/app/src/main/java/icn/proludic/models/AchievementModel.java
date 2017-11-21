package icn.proludic.models;

import android.content.Context;

import icn.proludic.R;

/**
 * Author:  Bradley Wilson
 * Date: 16/05/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class AchievementModel {

    private String name, image, description;
    private int weight, hearts;
    private boolean isCommunity;
    private boolean isLocked;
    private boolean isStatus;
    private static String[] statusAchievements;

    public AchievementModel(Context context, String name, String image, String description, int weight, int hearts, boolean isCommunity, boolean isLocked) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.weight = weight;
        this.hearts = hearts;
        this.isCommunity = isCommunity;
        this.isLocked = isLocked;
        this.isStatus = false;

        if (statusAchievements == null) {
            statusAchievements = new String[]{context.getString(R.string.bodyweight_diamond), context.getString(R.string.bodyweight_platinum), context.getString(R.string.bodyweight_gold),
                    context.getString(R.string.bodyweight_silver), context.getString(R.string.bodyweight_bronze), context.getString(R.string.bodyweight_copper),
                    context.getString(R.string.proludic_diamond), context.getString(R.string.proludic_platinum), context.getString(R.string.proludic_gold),
                    context.getString(R.string.proludic_silver), context.getString(R.string.proludic_bronze), context.getString(R.string.proludic_copper)};
        }

        for (String achievement : statusAchievements) {
            if (name.equals(achievement)) {
                this.isStatus = true;
                break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public int getWeight() {
        return weight;
    }

    public int getHearts() {
        return hearts;
    }

    public boolean isCommunity() {
        return isCommunity;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isStatus() { return isStatus; }
}
