package icn.proludic.models;

/**
 * Author:  Bradley Wilson
 * Date: 10/05/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class Fitness {

    private int _id;
    private int _uses;
    private int _exercise;
    private String _name;
    private long _personalBest;

    public Fitness() {

    }

    public Fitness(int id, int uses, String name, long personalBest, int isExercise) {
        this._id = id;
        this._uses = uses;
        this._name = name;
        this._personalBest = personalBest;
        this._exercise = isExercise;
    }

    public Fitness(int uses, String name, long personalBest, int isExercise) {
        this._uses = uses;
        this._name = name;
        this._personalBest = personalBest;
        this._exercise = isExercise;
    }

    public int getID() {
        return _id;
    }

    public int getUses() {
        return _uses;
    }

    public String getName() {
        return _name;
    }

    public long getPersonalBest() {
        return _personalBest;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public void setUses(int uses) {
        this._uses = uses;
    }

    public void setName(String name) {
        this._name = name;
    }

    public void setPersonalBest(long personalBest) {
        this._personalBest = personalBest;
    }

    public int isExercise() {
        return _exercise;
    }

    public void set_exercise(int _exercise) {
        this._exercise = _exercise;
    }

}
