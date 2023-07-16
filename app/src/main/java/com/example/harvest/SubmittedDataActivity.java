package com.example.harvest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SubmittedDataActivity extends AppCompatActivity {

    ListView submittedDataListView;
    SubmissionAdapter adapter;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted_data);

        submittedDataListView=findViewById(R.id.submittedDataListView);

        databaseHelper = new DatabaseHelper(this);

        List<JSONObject> submissions = loadSubmissionsFromDatabase();

        // Create a list of strings to display in the ListView
        List<String> submissionStrings = new ArrayList<>();
        for (JSONObject submission : submissions) {
            // Format the submission data and add it to the list
            submissionStrings.add(formatSubmissionData(submission));
        }

        // Create an ArrayAdapter to populate the ListView with the submission data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, submissionStrings);

        // Set the adapter on the ListView
        submittedDataListView.setAdapter(adapter);

        // Display the saved submissions in the TextView
//        displaySubmittedData(submissions);
    }

    private List<JSONObject> loadSubmissionsFromDatabase() {
        List<JSONObject> submissions = new ArrayList<>();

        // Get a readable database instance
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Define the columns to retrieve
        String[] columns = {DatabaseHelper.COLUMN_DATA};

        // Query the database to retrieve all submissions
        Cursor cursor = db.query(DatabaseHelper.TABLE_QUERIES, columns, null, null, null, null, null);

        // Iterate over the cursor to retrieve each submission
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Retrieve the data column from the current cursor row
                @SuppressLint("Range") String data = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATA));

                try {
                    // Parse the data string into a JSON object and add it to the list
                    JSONObject submission = new JSONObject(data);
                    submissions.add(submission);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Close the cursor
            cursor.close();
        }

        // Close the database connection
        db.close();

        return submissions;
    }

    private String formatSubmissionData(JSONObject submission) {
        try {
            // Retrieve the relevant data from the submission
            String caseID = submission.getString("caseID");
            String name = submission.getString("Name");
            String phone = submission.getString("Phone");
            String dateOfBirth = submission.getString("Date of Birth");
            String state = submission.getString("State");
            String district = submission.getString("District");

            // Format the submission data
            return "Case ID: " + caseID + "\n" +
                    "Name: " + name + "\n" +
                    "Phone: " + phone + "\n" +
                    "Date of Birth: " + dateOfBirth + "\n" +
                    "State: " + state + "\n" +
                    "District: " + district;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
}