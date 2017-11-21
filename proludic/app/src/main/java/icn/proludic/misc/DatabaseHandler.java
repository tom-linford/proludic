package icn.proludic.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import icn.proludic.models.Fitness;

/**
 * Author:  Bradley Wilson
 * Date: 10/05/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "fitnessManager";

    // Exercises table name
    private static final String TABLE_EXERCISES = "exercises";

    // Exercises Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_USES = "uses";
    private static final String KEY_PB = "personal_best";
    private static final String KEY_IS_EXERCISE = "is_exercise";

    private static final int KEY_ID_COL_INDEX = 0;
    private static final int KEY_NAME_COL_INDEX = 1;
    private static final int KEY_USES_COL_INDEX = 2;
    private static final int KEY_PB_COL_INDEX = 3;
    private static final int KEY_IS_EXERCISE_COL_INDEX = 4;

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_USES + " INTEGER,"
                + KEY_PB + " INTEGER,"
                + KEY_IS_EXERCISE + " INTEGER"
                + ")";
        db.execSQL(CREATE_EXERCISES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        // Create tables again
        onCreate(db);
    }

    public void insertInitialData(List<Fitness> fitnessList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Fitness fitness : fitnessList) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, fitness.getName());
            values.put(KEY_USES, fitness.getUses());
            values.put(KEY_PB, fitness.getPersonalBest());
            values.put(KEY_IS_EXERCISE, fitness.isExercise());
            // Inserting Row
            db.insert(TABLE_EXERCISES, null, values);
        }
        db.close();
    }

    public List<Fitness> getAllExercises(boolean isWorkout) {
        List<Fitness> fitnessList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES;
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor cursor = db.rawQuery(selectQuery, null);
        if (isWorkout) {
            cursor = db.query(TABLE_EXERCISES, new String[]{KEY_ID,
                            KEY_NAME, KEY_USES, KEY_PB, KEY_IS_EXERCISE}, KEY_IS_EXERCISE + "=?",
                    new String[]{(String.valueOf(0))}, null, null, KEY_USES + " DESC", null);
        } else {
           cursor = db.query(TABLE_EXERCISES, new String[]{KEY_ID,
                            KEY_NAME, KEY_USES, KEY_PB, KEY_IS_EXERCISE}, KEY_IS_EXERCISE + "=?",
                    new String[]{(String.valueOf(1))}, null, null, KEY_USES + " DESC", null);
        }
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if (Integer.valueOf(cursor.getString(KEY_USES_COL_INDEX)) > 0) {
                    Fitness fitness = new Fitness();
                    fitness.setId(Integer.parseInt(cursor.getString(KEY_ID_COL_INDEX)));
                    fitness.setName(cursor.getString(KEY_NAME_COL_INDEX));
                    fitness.setUses(Integer.parseInt(cursor.getString(KEY_USES_COL_INDEX)));
                    fitness.setPersonalBest(Integer.parseInt(cursor.getString(KEY_PB_COL_INDEX)));
                    fitness.set_exercise(Integer.parseInt(cursor.getString(KEY_IS_EXERCISE_COL_INDEX)));
                    fitnessList.add(fitness);
                }
            } while (cursor.moveToNext());
        }
        // return contact list
        cursor.close();
        db.close();
        return fitnessList;
    }

    // Getting single contact
    public Fitness getExercise(String exerciseName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query(TABLE_EXERCISES, new String[]{KEY_ID,
                        KEY_NAME, KEY_USES, KEY_PB, KEY_IS_EXERCISE}, KEY_NAME + "=?",
                    new String[]{(exerciseName)}, null, null, KEY_USES + " DESC", null);

        cursor.moveToFirst();
        Fitness fitness = new Fitness();
        fitness.setId(Integer.parseInt(cursor.getString(KEY_ID_COL_INDEX)));
        fitness.setName(cursor.getString(KEY_NAME_COL_INDEX));
        fitness.setUses(Integer.parseInt(cursor.getString(KEY_USES_COL_INDEX)));
        fitness.setPersonalBest(Long.parseLong(cursor.getString(KEY_PB_COL_INDEX)));
        fitness.set_exercise(Integer.parseInt(cursor.getString(KEY_IS_EXERCISE_COL_INDEX)));

        cursor.close();
        db.close();
        return fitness;
    }

    public void deleteCurrentRecords() {
        db.execSQL("delete from " + TABLE_EXERCISES);
    }

    public void updateFitness(Fitness fitness, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, fitness.getName());
        values.put(KEY_USES, fitness.getUses());
        values.put(KEY_PB, fitness.getPersonalBest());
        values.put(KEY_IS_EXERCISE, fitness.isExercise());
        Log.e("values", values.toString());
        db.updateWithOnConflict(TABLE_EXERCISES, values, KEY_ID + "=?",
                new String[]{String.valueOf(fitness.getID())}, SQLiteDatabase.CONFLICT_REPLACE);
        Log.e("dbUpdate", String.valueOf(db.updateWithOnConflict(TABLE_EXERCISES, values, KEY_ID + "=?",
                new String[]{String.valueOf(fitness.getID())}, SQLiteDatabase.CONFLICT_REPLACE)));
        db.close();
    }

    // Getting contacts Count
    public int getFitnessCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EXERCISES;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public ArrayList<Fitness> getAvailableExercisesByUses(List<String> exerciseNames) {
        ArrayList<Fitness> temp = new ArrayList<>();
        for (String s : exerciseNames) {
            temp.add(getExercise(s));
        }
        return temp;
    }
}
