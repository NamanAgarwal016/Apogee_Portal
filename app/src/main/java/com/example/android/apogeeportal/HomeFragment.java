package com.example.android.apogeeportal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private final String securityPasscode = "9061";
    private Spinner modifyEventSpinner, eventTypeSpinner, locationSpinner;
    private static final String[] allEvents = {"Pick An event to modify", "feature yet to be added", "Not working"};
    private Button btnDatePicker, btnTimePicker;
    private EditText txtDate, txtTime, eventNameTextView;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private static final String[] eventTypes = {"Category Of Event", "coding and fintech", "quizzing and strategy", "mechatronics", "civil", "electronics and robotics", "sciences", "others", "live events"};

    ArrayList<String> locations = new ArrayList<String>();
    String latitude;
    String longitude;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, null);
        passcode();

        eventNameTextView = (EditText) view.findViewById(R.id.event_name_edit_text);

        eventTypeSpinner = (Spinner) view.findViewById(R.id.eventTypeSpinner);
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, eventTypes);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(adapter1);

        btnDatePicker = (Button) view.findViewById(R.id.btn_date);
        btnTimePicker = (Button) view.findViewById(R.id.btn_time);
        txtDate = (EditText) view.findViewById(R.id.in_date);
        txtTime = (EditText) view.findViewById(R.id.in_time);



        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "T");

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtTime.setText(hourOfDay + ":" + minute + ":00Z");
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        //-------------locations -------------------

        String s1 = "Location of event";
        locations.add(s1);

        db.collection("Locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getId().trim();
                                locations.add(name);
                            }
                        } else {
                            Toast.makeText(getContext(), "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        locationSpinner = (Spinner) view.findViewById(R.id.locationSpinner);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, locations);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter2);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DocumentReference docRef = db.collection("Locations").document(locationSpinner.getSelectedItem().toString().trim());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //String edit = document.getData()

                                //  Toast.makeText(getContext(), "DocumentSnapshot data: " + document.get("lat").toString(), Toast.LENGTH_SHORT).show();
                                latitude = document.get("lat").toString().toLowerCase().trim();
                                longitude = document.get("long").toString().toLowerCase().trim();
                                // txtDate.setText(document.get("lat").toString());
                                //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                //Toast.makeText(getContext(), "No such document", Toast.LENGTH_SHORT).show();
                                // Log.d(TAG, "No such document");
                            }
                        } else {
                            Toast.makeText(getContext(), "get failed with " + task.getException(), Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button addEventButton = (Button) view.findViewById(R.id.add_event_btn);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (locationSpinner.getSelectedItem().toString().equals("Location of event") || eventTypeSpinner.getSelectedItem().toString().equals("Category Of Event") || eventNameTextView.getText().toString().trim().equals("") || txtTime.getText().toString().trim().equals(null) || txtDate.getText().toString().trim().equals(null) || txtTime.getText().toString().trim().equals("") || txtDate.getText().toString().trim().equals(null)) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    //------------

                    final String eventName = eventNameTextView.getText().toString().trim();

                    //code to enter event in database   document.get("name")
                    Map<String, Object> events = new HashMap<>();
                    events.put("Name", eventName);
                    events.put("Time", getDateFromString(txtDate.getText().toString().trim() + txtTime.getText().toString().trim()));
                    events.put("Type", eventTypeSpinner.getSelectedItem().toString().trim());
                    events.put("lat", Double.parseDouble(latitude.trim()));
                    events.put("long", Double.parseDouble(longitude.trim()));

// Add a new document with a generated ID
                    db.collection("Events")
                            .add(events)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getContext(), "Event Added", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to add Event", Toast.LENGTH_SHORT).show();
                                }
                            });
                    //Make all texts clear
                    eventNameTextView.setText(null);
                    txtDate.setText(null);
                    txtTime.setText(null);
                    //eventTypeSpinner.setSelection(adapter1.getPosition());

                    //---------------

                }

            }
        });

        modifyEventSpinner = (Spinner) view.findViewById(R.id.modifyEventSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, allEvents);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modifyEventSpinner.setAdapter(adapter);
        modifyEventSpinner.setOnItemSelectedListener(this);

        Button lockPortalButton = (Button) view.findViewById(R.id.modify_event_btn);
        lockPortalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                    passcode();

            }
        });


        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //methods for date and time selection
    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public Date getDateFromString(String datetoSaved) {

        try {
            Date date = format.parse(datetoSaved);
            return date;
        } catch (ParseException e) {
            return null;
        }

    }

    public void passcode(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        //final EditText etUsername = alertLayout.findViewById(R.id.et_username);
        final EditText passcode = alertLayout.findViewById(R.id.passcode);
        final CheckBox cbToggle = alertLayout.findViewById(R.id.cb_show_pass);

        cbToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // to encode password in dots
                    passcode.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // to display the password in normal text
                    passcode.setTransformationMethod(null);
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Apogee Portal 2020");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               // String user = etUsername.getText().toString();
                String pass = passcode.getText().toString();

                if (pass.equals(securityPasscode)){
                    Toast.makeText(getContext(),"You are an authorised member", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(),"Incorrect passcode", Toast.LENGTH_SHORT).show();
                    passcode();
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }


}