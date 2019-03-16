package com.self.runningtracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.self.runningtracker.data.WorkoutContract.WorkoutEntry;

public class WorkoutDbHelper extends SQLiteOpenHelper {

    public final static String LOG_TAG = WorkoutDbHelper.class.getSimpleName();

    // Name of the database file
    private static final String DATABASE_NAME = "workouts.db";

    // Database version. If one changes the database schema, tha database version number must
    //be updated as well.
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link WorkoutDbHelper}.
     *
     * @param context of the app
     */
    public WorkoutDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Calls this method when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        /** Creates a String that contains the SQL statement to create the workouts table. */
        String SQL_CREATE_WORKOUTS_TABLE = "CREATE TABLE " + WorkoutEntry.TABLE_NAME + " ("
                + WorkoutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WorkoutEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + WorkoutEntry.COLUMN_DISTANCE + " FLOAT NOT NULL, "
                + WorkoutEntry.COLUMN_DURATION + " TEXT NOT NULL, "
                + WorkoutEntry.COLUMN_AVG_P_R + " TEXT NOT NULL, "
                + WorkoutEntry.COLUMN_WORKOUT_TYPE + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_WORKOUTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){}

}
