package com.self.runningtracker.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class WorkoutContract {

    // Empty private constructor to prevent accidental instantiation of the contract class.
    private WorkoutContract(){}

    // Content authority for the Workout Provider.
    public static final String CONTENT_AUTHORITY = "com.self.runningtracker.workouts";

    // Base of all URIs that will be used to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path to access data within the workouts table.
    public static final String PATH_WORKOUTS = "workouts";

    // Inner class that defines constant values for the workouts database table.
    public final static class WorkoutEntry implements BaseColumns{

        // Content URI to access the workouts data through the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_WORKOUTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of workouts.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single workout.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUTS;

        // Name of database table for workouts.
        public final static String TABLE_NAME = "workouts";

        // Unique identifier for the workout (only for use in the database table).
        // Type: INTEGER.
        public final static String _ID = BaseColumns._ID;

        // Date of the workout.
        // Type: TEXT.
        public final static String COLUMN_DATE = "date";

        // Distance of the workout.
        // Type: FLOAT.
        public final static String COLUMN_DISTANCE = "distance";

        // Duration of the workout.
        // Type: TEXT.
        public final static String COLUMN_DURATION = "duration";

        // Average pace rate of the workout.
        // Type: TEXT.
        public final static String COLUMN_AVG_P_R = "avg_pace_rate";

        /**
         * Type of the workout.
         *
         * The only possible values are {@link #WORKOUT_TRAINING} and {@link #WORKOUT_RACE}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_WORKOUT_TYPE = "workout_type";

        // Possible values for the type of workout.
        public static final int WORKOUT_TRAINING = 0;
        public static final int WORKOUT_RACE = 1;

    }

}
