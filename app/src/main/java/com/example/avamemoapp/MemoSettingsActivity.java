package com.example.avamemoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MemoSettingsActivity extends AppCompatActivity {

    EditText searchBar;
    Spinner priorityFilter;
    ListView memoListView;
    RadioGroup sortOptions;

    List<memo> allMemos;
    List<memo> filteredMemos;
    ArrayAdapter<String> memoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_settings);

        initBackButton(); // Setup back button

        // Connect views from layout
        searchBar = findViewById(R.id.searchBar);
        priorityFilter = findViewById(R.id.priorityFilter);
        memoListView = findViewById(R.id.memoListView);
        sortOptions = findViewById(R.id.sortOptions);

        // Handle sort option changes
        sortOptions.setOnCheckedChangeListener((group, checkedId) -> updateList());

        // Load all memos from database
        MemoDataSource ds = new MemoDataSource(this);
        ds.open();
        allMemos = ds.getAllMemos();
        ds.close();

        filteredMemos = new ArrayList<>(allMemos);

        // Load saved filters from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE);
        String savedSearch = prefs.getString("searchText", "");
        String savedPriority = prefs.getString("selectedPriority", "All");
        int savedSortId = prefs.getInt("selectedSortOptionId", R.id.sortByDate); // Default sort by date

        // Set previously saved search text
        searchBar.setText(savedSearch);

        // Set previously selected priority filter
        ArrayAdapter adapter = (ArrayAdapter) priorityFilter.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(savedPriority);
            if (position >= 0) {
                priorityFilter.setSelection(position);
            }
        }

        // Set previously selected sort option
        sortOptions.check(savedSortId);

        // Display initial list
        updateList();

        // Update list when typing in search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateList();
            }
            // Required to have these but they are not used
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Update list when a new priority is selected
        priorityFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                updateList();
            }

            @Override public void onNothingSelected(AdapterView parent) {}
        });
    }

    // This method filters, sorts, and updates the memo list
    private void updateList() {
        String searchText = searchBar.getText().toString().toLowerCase();
        String selectedPriority = priorityFilter.getSelectedItem().toString();
        int selectedSortId = sortOptions.getCheckedRadioButtonId();

        // Save current search, filter, and sort options to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("searchText", searchBar.getText().toString());
        editor.putString("selectedPriority", selectedPriority);
        editor.putInt("selectedSortOptionId", selectedSortId);
        editor.apply();

        // Clear and rebuild the filtered list
        filteredMemos.clear();
        for (memo m : allMemos) {
            boolean matchesSearch = m.getName().toLowerCase().contains(searchText)
                    || m.getMText().toLowerCase().contains(searchText);
            boolean matchesPriority = selectedPriority.equals("All")
                    || m.getPriority().equalsIgnoreCase(selectedPriority);

            if (matchesSearch && matchesPriority) {
                filteredMemos.add(m);
            }
        }

        // Sort the filtered memos based on selected option
        if (selectedSortId == R.id.sortByPriority) {
            // Sort by High > Medium > Low using custom value by comparing the numeric values of their priority
            filteredMemos.sort((m1, m2) ->
                    getPriorityValue(m1.getPriority()) - getPriorityValue(m2.getPriority()));
        } else if (selectedSortId == R.id.sortBySubject) {
            // Sort A–Z by title
            filteredMemos.sort((m1, m2) ->
                    m1.getName().compareToIgnoreCase(m2.getName()));
        } else if (selectedSortId == R.id.sortByDate) {
            // Sort by soonest to latest date
            filteredMemos.sort((m1, m2) ->
                    m1.getDate().compareTo(m2.getDate()));
        }

        // Format and display the memo list
        List<String> displayList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");  //converts a date into a readable string

        for (memo m : filteredMemos) {
            String title = m.getName();
            String description = m.getMText();
            String priority = m.getPriority();
            String date = sdf.format(m.getDate().getTime());

            String info = title + "\n" + description + "\n" + priority + " - " + date;
            displayList.add(info);
        }

        // Connects the filtered and sorted memo data to the ListView, so it can actually be shown on screen
        memoListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        memoListView.setAdapter(memoListAdapter);
    }

    // Converts priority text to a number for custom sorting
    private int getPriorityValue(String priority) {
        switch (priority.toLowerCase()) {
            case "high": return 1;
            case "medium": return 2;
            case "low": return 3;
            default: return 4;
        }
    }

    // Back button returns to the memo list activity
    private void initBackButton() {
        Button button = findViewById(R.id.buttonBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoSettingsActivity.this, memoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
