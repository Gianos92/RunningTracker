package com.self.runningtracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.self.runningtracker.data.WorkoutContract;

import static com.self.runningtracker.data.WorkoutContract.*;

/**
 * {@link WorkoutCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of workout data as its data source. This adapter knows
 * how to create list items for each row of workout data in the {@link Cursor}.
 */
public class WorkoutCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link WorkoutCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public WorkoutCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the workout data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current workout can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // TODO Add view for workout type.
        // Finds individual views that we want to populate in the list_item layout.
        TextView dateTextView = view.findViewById(R.id.mDate);
        TextView distanceTextView = view.findViewById(R.id.mDistance);
        TextView durationTextView = view.findViewById(R.id.mDuration);
        TextView averagePRTextView = view.findViewById(R.id.mAvgPR);

        // Finds the columns of workout attributes that we are interested in.
        int dateColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_DATE);
        int distanceColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_DISTANCE);
        int durationColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_DURATION);
        int averagePRColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_AVG_P_R);

        //Reads the workout attributes from the Cursor for the current pet.
        String workoutDate = cursor.getString(dateColumnIndex);
        String workoutDistance = cursor.getString(distanceColumnIndex);
        String workoutDuration = cursor.getString(durationColumnIndex);
        String workoutAveragePaceRate = cursor.getString(averagePRColumnIndex);

        // Updates the TextViews with the attributes from the current pet.
        dateTextView.setText(workoutDate);
        distanceTextView.setText(workoutDistance);
        durationTextView.setText(workoutDuration);
        averagePRTextView.setText(workoutAveragePaceRate);
    }
}