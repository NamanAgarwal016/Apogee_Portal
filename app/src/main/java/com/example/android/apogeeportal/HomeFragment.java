package com.example.android.apogeeportal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Spinner modifyEventSpinner, eventTypeSpinner;
    private static final String[] allEvents = {"Pick An event to modify", "item 2", "item 3"};
    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private static final String[] eventTypes = {"Category Of Event", "coding and fintech", "quizzing and strategy", "mechatronics", "civil", "electronics and robotics", "sciences", "others", "live events"};


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, null);

        eventTypeSpinner = (Spinner) view.findViewById(R.id.eventTypeSpinner);
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, eventTypes);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(adapter1);
        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (parent.getItemAtPosition(position).equals("Category Of Event"))
                {
                    //do nothing.
                }
                else
                {
                    // write code on what you want to do with the item selection
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        Button addEventButton = (Button) view.findViewById(R.id.add_event_btn);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText eventNameTextView = (EditText) view.findViewById(R.id.event_name_edit_text);
                String eventName = eventNameTextView.getText().toString();

                //code to enter event in database
                Map<String, Object> events = new HashMap<>();
                events.put("Name", eventName);
                events.put("Time", getDateFromString(txtDate.getText().toString() + txtTime.getText().toString()));
                events.put("Type", eventTypeSpinner.getSelectedItem().toString());
                events.put("lat", 28.36592);
                events.put("long", 75.588224);

// Add a new document with a generated ID
                db.collection("Naman")
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
                        eventNameTextView.setText("");
                        txtDate.setText("");
                        txtTime.setText("");
                        //eventTypeSpinner.setSelection(adapter1.getPosition());
            }
        });

        modifyEventSpinner = (Spinner) view.findViewById(R.id.modifyEventSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, allEvents);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modifyEventSpinner.setAdapter(adapter);
        modifyEventSpinner.setOnItemSelectedListener(this);

        Button ModifyEventButton = (Button) view.findViewById(R.id.modify_event_btn);
        ModifyEventButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
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


}