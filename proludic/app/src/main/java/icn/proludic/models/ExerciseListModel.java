package icn.proludic.models;

import java.io.Serializable;

/**
 * Author:  Bradley Wilson
 * Date: 18/05/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class ExerciseListModel implements Serializable {

    private String imageURL;
    private String exerciseName;
    private String totalWeightString;
    private int totalReps;
    private long averageMillis;
    private int totalWeight;
    private long finalTime;

    public ExerciseListModel(String imageURL, String exerciseName, int totalReps, long averageMillis, String weight, int totalWeight) {
        this.imageURL = imageURL;
        this.exerciseName = exerciseName;
        this.totalReps = totalReps;
        this.averageMillis = averageMillis;
        this.totalWeightString = weight;
        this.totalWeight = totalWeight;
    }

    public long getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(long finalTime) {
        this.finalTime = finalTime;
    }
    
    public String getImageURL() {
        return imageURL;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getTotalReps() {
        return totalReps;
    }

    public long getTotalMillis() {
        return averageMillis;
    }

    public String getTotalWeightString() {
        return totalWeightString;
    }

    public int getTotalWeight() {return totalWeight;};
}
