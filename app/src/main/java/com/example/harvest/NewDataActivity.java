package com.example.harvest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewDataActivity extends AppCompatActivity {

    EditText nameEditText, phoneEditText, dobEditText,cropHarvestingDateEditText,areaHarvestedEditText;
    Spinner stateSpinner, districtSpinner,onfieldCropConditionSpinner;
    TextView caseIdTextView;
    Button bt_sync,bt_submit;
    RadioGroup surveyTypeRadioGroup;
    RadioButton postHarvestRadioButton,rb_others;
    LinearLayout postHarvestFieldsLayout,others_ll;

    String selectedState,surveyType,cropHarvestingDate,areaHarvested,onfieldCropCondition,pleaseSpecify;

    List<String> statesList;
    List<String> districtList;
    List<String> onFieldList;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_data);

        caseIdTextView = findViewById(R.id.caseIdTextView);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        dobEditText = findViewById(R.id.dateOfBirthEditText);
        stateSpinner = findViewById(R.id.stateSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        bt_sync = findViewById(R.id.bt_syncData);
        bt_submit = findViewById(R.id.bt_submit);

        surveyTypeRadioGroup = findViewById(R.id.surveyTypeRadioGroup);
        postHarvestRadioButton = findViewById(R.id.radioPostHarvest);
        rb_others = findViewById(R.id.radioOthers);
        postHarvestFieldsLayout = findViewById(R.id.postHarvestFieldsLayout);
        others_ll = findViewById(R.id.othersFieldsLayout);
        cropHarvestingDateEditText = findViewById(R.id.cropHarvestingDateEditText);
        areaHarvestedEditText = findViewById(R.id.areaHarvestedEditText);
        onfieldCropConditionSpinner = findViewById(R.id.onfieldCropConditionSpinner);

        databaseHelper = new DatabaseHelper(this);

//        Generate and display a random case ID
        String caseId = generateCaseId();
        caseIdTextView.setText("Case ID: " + caseId);

        surveyTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // Show fields based on selected survey type
                if (checkedId == R.id.radioPostHarvest) {
                    postHarvestFieldsLayout.setVisibility(View.VISIBLE);
                    others_ll.setVisibility(View.GONE);
                }else if(checkedId == R.id.radioOthers){
                    postHarvestFieldsLayout.setVisibility(View.GONE);
                    others_ll.setVisibility(View.VISIBLE);
                }
            }
        });

        // Populate the state spinner
        populateStateSpinner();
        callOnFieldSpinner();

        bt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postHarvestRadioButton.isChecked()) {
                        surveyType = "Post Harvest";
                        cropHarvestingDate = cropHarvestingDateEditText.getText().toString().trim();
                        areaHarvested = areaHarvestedEditText.getText().toString().trim();
                        onfieldCropCondition = onfieldCropConditionSpinner.getSelectedItem().toString();
                        pleaseSpecify = "";
                    } else {
                        surveyType = "Others";
                        cropHarvestingDate = "";
                        areaHarvested = "";
                        onfieldCropCondition = "";
                    }

//                Validations
                    if (surveyType.isEmpty()) {
                        Toast.makeText(NewDataActivity.this, "Please select a survey type", Toast.LENGTH_SHORT).show();
                    } else if (surveyType.equals("Post Harvest") && (cropHarvestingDate.isEmpty() || areaHarvested.isEmpty() || onfieldCropCondition.isEmpty())) {
                        Toast.makeText(NewDataActivity.this, "Please fill in all Post Harvest fields", Toast.LENGTH_SHORT).show();
                    } else if (surveyType.equals("Others") && pleaseSpecify.isEmpty()) {
                        Toast.makeText(NewDataActivity.this, "Please specify in the Others field", Toast.LENGTH_SHORT).show();
                    }else if(nameEditText.getText().toString().isEmpty()){
                        Toast.makeText(NewDataActivity.this,"Please Enter Name",Toast.LENGTH_SHORT).show();
                    }else if(selectedState.equals("Select an option")){
                        Toast.makeText(NewDataActivity.this,"Please Enter District",Toast.LENGTH_SHORT).show();
                    }else if(districtSpinner.equals("Select an option")){
                        Toast.makeText(NewDataActivity.this,"Please Enter State",Toast.LENGTH_SHORT).show();
                    }else  if(dobEditText.getText().toString().isEmpty()){
                        Toast.makeText(NewDataActivity.this,"Please Enter D.O.B",Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject payload = new JSONObject();
                        try {
                            // Create the data object
                            JSONObject data = new JSONObject();
                            data.put("caseID", generateCaseId());
                            data.put("Name", nameEditText.getText().toString());
                            data.put("Phone", phoneEditText.getText().toString());
                            data.put("Date of Birth", dobEditText.getText().toString());
                            data.put("State", selectedState);
                            data.put("District", districtSpinner);
                            data.put("Survey Type", surveyType);

                            // Create the survey type-specific fields
                            if (surveyType.equals("Post Harvest")) {
                                JSONObject postHarvestData = new JSONObject();
                                postHarvestData.put("Date of crop harvesting", cropHarvestingDate);
                                postHarvestData.put("% of crop area harvested", Integer.parseInt(areaHarvested));
                                postHarvestData.put("Onfield Crop Condition", onfieldCropCondition);
                                data.put("Post Harvest", postHarvestData);
                            } else {
                                data.put("Please Specify", pleaseSpecify);
                            }

                            // Add the data object to the payload
                            payload.put("data", data);

                            // Add the tablename to the payload
                            payload.put("tablename", "survey2");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Display the payload JSON
                        Toast.makeText(NewDataActivity.this, "Payload:\n" + payload.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        bt_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postHarvestRadioButton.isChecked()) {
                    surveyType = "Post Harvest";
                    cropHarvestingDate = cropHarvestingDateEditText.getText().toString().trim();
                    areaHarvested = areaHarvestedEditText.getText().toString().trim();
                    onfieldCropCondition = onfieldCropConditionSpinner.getSelectedItem().toString();
                    pleaseSpecify = "";
                } else {
                    surveyType = "Others";
                    cropHarvestingDate = "";
                    areaHarvested = "";
                    onfieldCropCondition = "";
                }

//                Validations
                if (surveyType.isEmpty()) {
                    Toast.makeText(NewDataActivity.this, "Please select a survey type", Toast.LENGTH_SHORT).show();
                } else if (surveyType.equals("Post Harvest") && (cropHarvestingDate.isEmpty() || areaHarvested.isEmpty() || onfieldCropCondition.isEmpty())) {
                    Toast.makeText(NewDataActivity.this, "Please fill in all Post Harvest fields", Toast.LENGTH_SHORT).show();
                } else if (surveyType.equals("Others") && pleaseSpecify.isEmpty()) {
                    Toast.makeText(NewDataActivity.this, "Please specify in the Others field", Toast.LENGTH_SHORT).show();
                } else if (nameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(NewDataActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                } else if (selectedState.equals("Select an option")) {
                    Toast.makeText(NewDataActivity.this, "Please Enter District", Toast.LENGTH_SHORT).show();
                } else if (districtSpinner.equals("Select an option")) {
                    Toast.makeText(NewDataActivity.this, "Please Enter State", Toast.LENGTH_SHORT).show();
                } else if (dobEditText.getText().toString().isEmpty()) {
                    Toast.makeText(NewDataActivity.this, "Please Enter D.O.B", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject payload = new JSONObject();
                    try {
                        // Create the data object
                        JSONObject data = new JSONObject();
                        data.put("caseID", generateCaseId());
                        data.put("Name", nameEditText.getText().toString());
                        data.put("Phone", phoneEditText.getText().toString());
                        data.put("Date of Birth", dobEditText.getText().toString());
                        data.put("State", selectedState);
                        data.put("District", districtSpinner);
                        data.put("Survey Type", surveyType);

                        // Create the Post Harvest fields
                        JSONObject postHarvestData = new JSONObject();
                        postHarvestData.put("Date of crop harvesting", cropHarvestingDate);
                        postHarvestData.put("% of crop area harvested", Integer.parseInt(areaHarvested));
                        postHarvestData.put("Onfield Crop Condition", onfieldCropCondition);
                        data.put("Post Harvest", postHarvestData);

                        // Add the data object to the payload
                        payload.put("data", data);

                        // Add the tablename to the payload
                        payload.put("tablename", "survey2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Save the payload to the database
                    databaseHelper.insertQuery(payload.toString());

                    // Display the success message
                    Toast.makeText(NewDataActivity.this, "Payload saved to database", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(NewDataActivity.this, SubmittedDataActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void callOnFieldSpinner() {
        onFieldList=new ArrayList<>();
        onFieldList.add("Select an option");
        onFieldList.add("Standing");
        onFieldList.add("Cut");
        onFieldList.add("Pile");
        onFieldList.add("No crop in field");

        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, onFieldList);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        onfieldCropConditionSpinner.setAdapter(fieldAdapter);
    }

    private void populateStateSpinner() {
        statesList = new ArrayList<>();
        statesList.add("Select an option");
        statesList.add("Odisha");
        statesList.add("Assam");
        statesList.add("Maharashtra");

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statesList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        // Set a listener to update the district spinner when a state is selected
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the selected state
                selectedState = adapterView.getItemAtPosition(position).toString();

                // Update the district spinner based on the selected state
                updateDistrictSpinner(selectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    private void updateDistrictSpinner(String selectedState) {
        // Populate the district spinner based on the selected state
        districtList = new ArrayList<>();
        if (selectedState.equals("Odisha")) {
            districtList.add("Bhadrak");
            districtList.add("Deogarh");
            districtList.add("Jajapur");
        } else if (selectedState.equals("Assam")) {
            districtList.add("Barpeta");
            districtList.add("Charaideo");
        }else if (selectedState.equals("Maharashtra")) {
            districtList.add("Beed");
            districtList.add("Akola");
            districtList.add("Hingoli");
        }

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);
    }

    private String generateCaseId() {
        // Generate a random case ID with letters and numbers
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder caseId = new StringBuilder();
        Random rand = new Random();
        while (caseId.length() < 10) {
            int index = rand.nextInt(characters.length());
            caseId.append(characters.charAt(index));
        }
        return caseId.toString();
    }
}