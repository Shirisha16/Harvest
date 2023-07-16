package com.example.harvest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.SubmissionViewHolder> {

    private List<JSONObject> submissions;

    public SubmissionAdapter(List<JSONObject> submissions) {
        this.submissions = submissions;
    }

    @NonNull
    @Override
    public SubmissionAdapter.SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_submission, parent, false);
        return new SubmissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionAdapter.SubmissionViewHolder holder, int position) {
        JSONObject submission = submissions.get(position);
        // Bind the data to the ViewHolder views
        try {
            String caseID = submission.getString("caseID");
            holder.textViewCaseID.setText(caseID);

            // Bind other data to respective TextViews
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }

    public class SubmissionViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCaseID;

        public SubmissionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCaseID = itemView.findViewById(R.id.textViewCaseID);
        }
    }
}
