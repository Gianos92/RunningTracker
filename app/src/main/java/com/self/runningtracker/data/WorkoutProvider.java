package com.self.runningtracker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import static com.self.runningtracker.data.WorkoutContract.*;

public class WorkoutProvider extends ContentProvider {

    // Tag for the log messages.
    public static final String LOG_TAG = WorkoutProvider.class.getSimpleName();

    // URI matcher code for the content URI for the workouts table.
    private static final int WORKOUTS = 0;

    // URI matcher code for the content URI for a single workout in the workouts table.
    private static final int WORKOUT_ID = 1;

    // UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. It is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        /** Content Uri of the form "content://com.self.runningtracker.workouts/workouts",
         *  which will match to the integer code {@link #WORKOUTS}.
         */
        sUriMatcher.addURI(CONTENT_AUTHORITY,PATH_WORKOUTS,WORKOUTS);

        /** Content Uri of the form "content://com.self.runningtracker.workouts/workouts/#",
         *  which will match to the integer code {@link #WORKOUT_ID}.
         */
        sUriMatcher.addURI(CONTENT_AUTHORITY,PATH_WORKOUTS
                + "/#", WORKOUT_ID);
    }

    // Database helper object.
    private WorkoutDbHelper mDbHelper;

    // Initializes the provider and the database helper object.
    @Override
    public boolean onCreate() {
        // Creates and initializes a WorkoutDbHelper object to gain access to the workouts database.
        mDbHelper = new WorkoutDbHelper(getContext());
        // Makes sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    // Performs the query for the given URI.
    // Uses the given projection, selection, selection arguments, and sort order.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Gets a readable database object to query data from.
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Creates the cursor to hold the result of the query.
        Cursor cursor;

        // Matches the Uri to the correct code.
            int match = sUriMatcher.match(uri);
        switch (match){
            case WORKOUTS:
                cursor = database.query(WorkoutEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case WORKOUT_ID:
                selection = WorkoutEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(WorkoutEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Sets notification URI on the cursor. If any data at this URI changes, then
        // the cursor needs to be updated.
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    // Inserts new data into the provider with the given ContentValues.
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Ensures the URI matches the path to the workouts table.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUTS:
                return insertWorkout(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Inserts a workout into the database with the given content values.
    // Returns the new content URI for that specific row in the database.
    private Uri insertWorkout(Uri uri, ContentValues values){

        // Checks that the date is not null.
        String date = values.getAsString(WorkoutEntry.COLUMN_DATE);
        if (date.isEmpty()){
            Toast.makeText(this.getContext(), "Workout requires a date",
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        // Checks that the distance is not null.
        Float distance = values.getAsFloat(WorkoutEntry.COLUMN_DISTANCE);
        if (distance == null){
            Toast.makeText(this.getContext(), "Workout requires a distance",
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        // Checks that the duration is not null.
        String duration = values.getAsString(WorkoutEntry.COLUMN_DURATION);
        if (duration.isEmpty()){
            Toast.makeText(this.getContext(), "Workout requires a duration",
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        // Checks that the average pace rate is not null.
        String avgPaceRate = values.getAsString(WorkoutEntry.COLUMN_AVG_P_R);
        if (avgPaceRate.isEmpty()){
            Toast.makeText(this.getContext(), "Workout requires an avg pace rate",
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        // Gets writable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Inserts the new workout with the given values
        long id = database.insert(WorkoutEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notifies all listeners that the data has changed for the workout content URI.
        // uri: content://com.self.runningtracker.workouts/workouts.
        getContext().getContentResolver().notifyChange(uri,null);


        // Returns the new URI with the ID (of the newly inserted row) appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    // Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Ensures the URI matches the path to the workouts table.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUT_ID:
                selection = WorkoutEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateWorkout(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    // Updates a workout with the given content values.
    // Returns the new content URI for that specific row in the database.
    private int updateWorkout(Uri uri, ContentValues values, String selection, String[] selectionArgs)  {

        // Checks that the date is not null.
        String date = values.getAsString(WorkoutEntry.COLUMN_DATE);
        if (date.isEmpty()){
            Toast.makeText(this.getContext(), "Workout requires a date",
                    Toast.LENGTH_SHORT).show();
            return 0;
        }

        // Checks that the distance is not null.
        Float distance = values.getAsFloat(WorkoutEntry.COLUMN_DISTANCE);
        if (distance == null){
            Toast.makeText(this.getContext(), "Workout requires a distance",
                    Toast.LENGTH_SHORT).show();
            return 0;
        }

        // Checks that the duration is not null.
        String duration = values.getAsString(WorkoutEntry.COLUMN_DURATION);
        if (duration.isEmpty()){
            Toast.makeText(this.getContext(), "Workout requires a duration",
                    Toast.LENGTH_SHORT).show();
            return 0;
        }

        // Checks that the average pace rate is not null.
        String avgPaceRate = values.getAsString(WorkoutEntry.COLUMN_AVG_P_R);
        if (avgPaceRate.isEmpty()){
            Toast.makeText(this.getContext(), "Workout requires an avg pace rate",
                    Toast.LENGTH_SHORT).show();
            return 0;
        }

        // If there are no values to update, then no operation is going to be performed on the db.
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise gets writable database to update the data.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Performs the update on the database and gets the number of rows affected
        int rowsUpdated = database.update(WorkoutEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notifies all listeners that the data at the
        // given URI has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of rows updated.
        return rowsUpdated;

    }

    // Deletes the data at the given selection and selection arguments.
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Gets writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted.
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUT_ID:
                // Deletes a single row given by the ID in the URI.
                selection = WorkoutEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(WorkoutEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notifies all listeners that the data at the
        // given URI has changed.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of rows deleted
        return rowsDeleted;

    }

    // Returns the MIME type of data for the content URI.
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUTS:
                return WorkoutEntry.CONTENT_LIST_TYPE;
            case WORKOUT_ID:
                return WorkoutEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}