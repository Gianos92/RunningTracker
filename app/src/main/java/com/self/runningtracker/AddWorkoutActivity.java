package com.self.runningtracker;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.self.runningtracker.data.WorkoutContract.WorkoutEntry;

public class AddWorkoutActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the workout data loader.
    private static final int EXISTING_WORKOUT_LOADER = 0;

    // Content URI for the existing workout (null if it is a new workout).
    private Uri mCurrentWorkoutUri;

    // EditText field to enter the workout's date.
    private EditText mDateEditText;

    // EditText field to enter the workout's distance.
    private EditText mDistanceEditText;

    // EditText field to enter the workout's duration.
    private EditText mDurationEditText;

    // EditText field to enter the workout's average pace rate.
    private EditText mAvgPaceRateEditText;

    // Spinner field to get the workout type.
    private Spinner mWorkoutTypeSpinner;

    /**
     * Type of the workout.
     * <p>
     * The only possible values are {@link WorkoutEntry#WORKOUT_TRAINING} and {@link WorkoutEntry#WORKOUT_RACE}.
     */
    private int mWorkoutType = 0;

    // Boolean flag that keeps track of whether the workout has been edited (true) or not (false).
    private boolean mWorkoutHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    //the view, and we change the mWorkoutHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mWorkoutHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_workout);

        // Examines the intent that was used to launch this activity in order to figure out
        // whether the workout is being created or edited.
        Intent intent = getIntent();
        mCurrentWorkoutUri = intent.getData();

        // If the intent DOES NOT contain a workout content URI, then we know that we are creating a workout.
        if (mCurrentWorkoutUri == null) {
            // Does nothing.
        } else {
            // Initializes a loader to read the workout data from the database
            // and displays the current values in the editor.
            getSupportLoaderManager().initLoader(EXISTING_WORKOUT_LOADER, null, this);
        }

        // Finds all relevant views needed to read user input from.
        mDateEditText = findViewById(R.id.date_sample_text);
        mDistanceEditText = findViewById(R.id.distance_sample_text);
        mDurationEditText = findViewById(R.id.duration_sample_text);
        mAvgPaceRateEditText = findViewById(R.id.average_pace_rate_sample_text);
        mWorkoutTypeSpinner = findViewById(R.id.spinner_workout_type);

        // Sets up OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mDateEditText.setOnTouchListener(mTouchListener);
        mDistanceEditText.setOnTouchListener(mTouchListener);
        mDurationEditText.setOnTouchListener(mTouchListener);
        mAvgPaceRateEditText.setOnTouchListener(mTouchListener);
        mWorkoutTypeSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();

        // Button to save workout.
        Button workoutBtn = findViewById(R.id.add_workout_btn);
        workoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Saves workout to the database.
                saveWorkout();

            }

        });

    }

    //Sets up the dropdown spinner that allows the user to select the type of workout
    private void setupSpinner() {

        // Creates the adapter for the spinner view.
        ArrayAdapter workoutSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_workout_types, R.layout.custom_layout);

        // Specifies the dropdown layout style - simple list view with 1 item per line.
        workoutSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Applies the adapter to the spinner.
        mWorkoutTypeSpinner.setAdapter(workoutSpinnerAdapter);

        // Sets the integer mSelected to the constant values.
        mWorkoutTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.workout_training))) {
                        mWorkoutType = 0; //Training
                    } else {
                        mWorkoutType = 1; //Race
                    }
                }

            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mWorkoutType = 0; //Training
            }

        });

    }

    // Gets user input from editor and saves workout into the database.
    private void saveWorkout() {

        // Reads from input fields.
        String dateString = mDateEditText.getText().toString().trim();
        String distanceString = mDistanceEditText.getText().toString().trim();
        String durationString = mDurationEditText.getText().toString().trim();
        String averagePRString = mAvgPaceRateEditText.getText().toString().trim();

        // Creates a ContentValues object where column names are the keys,
        // and workout attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(WorkoutEntry.COLUMN_DATE, dateString);
        values.put(WorkoutEntry.COLUMN_DISTANCE, distanceString);
        values.put(WorkoutEntry.COLUMN_DURATION, durationString);
        values.put(WorkoutEntry.COLUMN_AVG_P_R, averagePRString);
        values.put(WorkoutEntry.COLUMN_WORKOUT_TYPE, mWorkoutType);

        // Determines if this is a new or existing workout by checking if mCurrentWorkoutUri is null or not.
        if (mCurrentWorkoutUri == null) {
            // This is a NEW workout, so inserts a new workout into the provider,
            // returning the content URI for the new workout.
            Uri newUri = getContentResolver().insert(WorkoutEntry.CONTENT_URI, values);

            // Shows a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_workout_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_workout_successful),
                        Toast.LENGTH_SHORT).show();

                // Exits the activity.
                finish();

            }
        } else {
            // Otherwise this is an EXISTING workout, so updates the workout with content URI: mCurrentWorkoutUri
            // and passes in the new ContentValues. Passes in null for the selection and selection args
            // because mCurrentWorkoutUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentWorkoutUri, values, null, null);

            // Shows a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_insert_workout_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_workout_successful),
                        Toast.LENGTH_SHORT).show();
            }

            // Exits the activity.
            finish();
        }

    }

    // This method is called when the back button is pressed.
    @Override
    public void onBackPressed() {
        // If the workout hasn't changed, continues with handling back button press.
        if (!mWorkoutHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, sets up a dialog to warn the user.
        // Creates a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, closes the current activity.
                        finish();
                    }
                };

        // Shows dialog that there are unsaved changes.
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Defines a projection that contains all columns from the pet table.
        String[] projection = {
                WorkoutEntry._ID,
                WorkoutEntry.COLUMN_DATE,
                WorkoutEntry.COLUMN_DISTANCE,
                WorkoutEntry.COLUMN_DURATION,
                WorkoutEntry.COLUMN_AVG_P_R,
                WorkoutEntry.COLUMN_WORKOUT_TYPE};

        // This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,   // Parent activity context
                mCurrentWorkoutUri,             // Queries the content URI for the current workout
                projection,                     // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                // No selection arguments
                null);                 // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bails early if the cursor is null or there is less than 1 row in the cursor.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceeds with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            // Finds the columns of workout attributes that we're interested in
            int dateColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_DATE);
            int distanceColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_DISTANCE);
            int durationColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_DURATION);
            int avgPRColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_AVG_P_R);
            int workoutTypeColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_TYPE);

            // Extracts out the value from the Cursor for the given column index.
            String date = cursor.getString(dateColumnIndex);
            int distance = cursor.getInt(distanceColumnIndex);
            String duration = cursor.getString(durationColumnIndex);
            String averagePR = cursor.getString(avgPRColumnIndex);
            int workoutType = cursor.getInt(workoutTypeColumnIndex);

            // Updates the views on the screen with the values from the database.
            mDateEditText.setText(date);
            mDistanceEditText.setText(Integer.toString(distance));
            mDurationEditText.setText(duration);
            mAvgPaceRateEditText.setText(averagePR);

            // WorkoutType is a dropdown spinner, so maps the constant value from the database
            // into one of the dropdown options (0 is Training, 1 is Race).
            // Then calls setSelection() so that option is displayed on screen as the current selection.
            switch (workoutType) {
                case WorkoutEntry.WORKOUT_TRAINING:
                    mWorkoutTypeSpinner.setSelection(0);
                    break;
                case WorkoutEntry.WORKOUT_RACE:
                    mWorkoutTypeSpinner.setSelection(1);
                    break;
                default:
                    mWorkoutTypeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clears out all the data from the input fields.
        mDateEditText.setText("");
        mDistanceEditText.setText("");
        mDurationEditText.setText("");
        mAvgPaceRateEditText.setText("");
        mWorkoutTypeSpinner.setSelection(0); // Selects "Training" workout
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Creates an AlertDialog.Builder and sets the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismisses the dialog
                // and continues editing the workout.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Creates and shows the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}

