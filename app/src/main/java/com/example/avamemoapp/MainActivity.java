package com.example.avamemoapp;

//import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {

    private MemoDBHelper dbHelper;
    private memo currentMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new MemoDBHelper(this);
        Log.d("DB_STATUS", "Database open? " + dbHelper.getWritableDatabase().isOpen());

        //Call the method here to trigger/open the memoListActivity
        initNextButton();
        initSaveButton();
        initTextChangedEvents();
        initDateButton();
        currentMemo = new memo();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initMemo(extras.getInt("memoID"));
        }
        else {
            currentMemo = new memo();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //This takes us to the memo list activity

    private void initNextButton() {
        Button button = findViewById(R.id.buttonNext);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, memoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }



    //this method takes the info in the edit texts and saves it to the memo object
    private void initTextChangedEvents(){
        final EditText etMemoName = findViewById(R.id.titleEditText);
        etMemoName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //save the memo name
                currentMemo.setName(etMemoName.getText().toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


        });

        final EditText etMemoText = findViewById(R.id.mTextEditText);
        etMemoText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //save the memo name
                currentMemo.setText(etMemoText.getText().toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


        });

        final Spinner etMemoPriority = findViewById(R.id.prioritySpinner);
        etMemoPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPriority = parent.getItemAtPosition(position).toString();
                currentMemo.setPriority(selectedPriority);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }
    private void initSaveButton(){
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wasSuccessful = false;
                MemoDataSource ds = new MemoDataSource(MainActivity.this);
                try {
                    ds.open();

                    if (currentMemo.getMemoID() == -1) {
                        wasSuccessful = ds.insert(currentMemo);

                    }

                    else {
                        wasSuccessful = ds.update(currentMemo);
                    }
                    ds.close();
                } catch (Exception e) {
                    wasSuccessful = false;
                }
                if (wasSuccessful) {
                    Intent intent = new Intent(MainActivity.this, memoListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        });

    }

    //date picker dialog
    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setText(DateFormat.format("MM/dd/yyyy", selectedTime));
        currentMemo.setDate(selectedTime);
    }

    private void initDateButton(){
        Button dateButton = findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = new DatePickerDialog();
                datePickerDialog.show(fm, "DatePick");
            }
        });
    }
    private void initMemo(int ID){
        MemoDataSource ds = new MemoDataSource(MainActivity.this);
        try {
            ds.open();
            currentMemo = ds.getSpecificMemo(ID);
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this, "Load Contact Failed", Toast.LENGTH_SHORT).show();
        }
        EditText etMemoName = findViewById(R.id.titleEditText);
        EditText etMemoText = findViewById(R.id.mTextEditText);
        Spinner etMemoPriority = findViewById(R.id.prioritySpinner);
        TextView dateTextView = findViewById(R.id.dateTextView);

        etMemoName.setText(currentMemo.getName());
        etMemoText.setText(currentMemo.getMText());
        // Set the spinner to the current priority
        String currentPriority = currentMemo.getPriority();



    }

}