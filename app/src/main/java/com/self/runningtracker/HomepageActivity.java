package com.self.runningtracker;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.self.runningtracker.data.WorkoutContract.*;

public class HomepageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the pet data loader.
    private static final int WORKOUT_LOADER = 0;

    // Adapter for the ListView.
    WorkoutCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomepageActivity.this, AddWorkoutActivity.class);
                startActivity(intent);
            }
        });

        // Finds the ListView which will be populated with the workout data.
        ListView workoutListView =  findViewById(R.id.list);

        // Finds and sets empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        workoutListView.setEmptyView(emptyView);

        // Sets up an adapter to create a list item for each row of a workout data in the Cursor.
        // There is no workout data yet (until the Loader finishes) so passes in null for the Cursor.
        mCursorAdapter = new WorkoutCursorAdapter(this, null);
        workoutListView.setAdapter(mCursorAdapter);

        // Sets up the item click listener.
        workoutListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Creates new intent to go to {@link AddWorkoutActivity}
                Intent intent = new Intent(HomepageActivity.this, AddWorkoutActivity.class);

                // Forms the content URI that represents the specific workout that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link WorkoutEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.self.runningtracker.workouts/workouts/2"
                // if the workout with ID 2 was clicked on.
                Uri currentWorkoutUri = ContentUris.withAppendedId(WorkoutEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentWorkoutUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Kicks off the loader.
        getSupportLoaderManager().initLoader(WORKOUT_LOADER,null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Defines a projection that specifies which columns from the database
        // will actually be used after the query.
        String[] projection = {
                WorkoutEntry._ID,
                WorkoutEntry.COLUMN_DATE,
                WorkoutEntry.COLUMN_DISTANCE,
                WorkoutEntry.COLUMN_DURATION,
                WorkoutEntry.COLUMN_AVG_P_R,
                WorkoutEntry.COLUMN_WORKOUT_TYPE};

        // Executes the ContentProvider's query  method on a background thread.
        return new CursorLoader(this,                // parent activity context.
                WorkoutEntry.CONTENT_URI,           // Content URI of the workouts table.
                projection,                         // Columns to return for each row.
                null,                               // Selection criteria.
                null,                               // Selection criteria.
                null);                              // Sort order for the returned rows.
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Updates {@link WorkoutCursorAdapter} with this new cursor containing updated workout data.
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted.
        mCursorAdapter.swapCursor(null);
    }
}
