//        Gets what the user types or selects 118
//        Filters the memo list based on search and priority 134
//        Sorts the results by the selected sort option 150
//        Saves those settings for next time 125

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

        filteredMemos = new ArrayList<>(allMemos);  //creates a copy of allMemos list and stores it here

        // Load saved filters from SharedPreferences way below
        SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE); //restores previous user settings
        String savedSearch = prefs.getString("searchText", ""); // restores the user’s choices of the search bar
        String savedPriority = prefs.getString("selectedPriority", "All"); // restores the user’s choices of the priority dropdown
        int savedSortId = prefs.getInt("selectedSortOptionId", R.id.sortByDate); // restores the user’s choices for the sort option radio buttons or Defaults to sort by date

        // Set previously saved search text
        searchBar.setText(savedSearch);

        //Restores previously selected item in priority filter dropdown (Spinner)
        // an adapter acts as a bridge between your data (like a list of priorities or memos) and a UI component (like a Spinner or ListView)
        ArrayAdapter adapter = (ArrayAdapter) priorityFilter.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(savedPriority); //finds the index
            if (position >= 0) {
                priorityFilter.setSelection(position);  //If it found a valid index (not -1), then it sets that option as the current selection in the Spinner
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

        // This code listens for when the user selects an item from your Spinner (called priorityFilter)
        // like "High", "Medium", "Low", or "All" — and updates the memo list when that happens.
        priorityFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                //view is what was selected in the Spinner (the actual dropdown item that the user clicked on).
                updateList();
            }

            @Override public void onNothingSelected(AdapterView parent) {}
        });
    }

    // This method filters, sorts, and updates the memo list
    private void updateList() {
        // this is where I get the search text the user types in the search bar
        String searchText = searchBar.getText().toString().toLowerCase();
        String selectedPriority = priorityFilter.getSelectedItem().toString();
        //check which radio button the user selected
        int selectedSortId = sortOptions.getCheckedRadioButtonId();

        // Save current search, filter, and sort options to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("searchText", searchBar.getText().toString()); //Whatever the user typed in the search bar
        editor.putString("selectedPriority", selectedPriority);  //The dropdown value they picked (e.g., "High")
        editor.putInt("selectedSortOptionId", selectedSortId); // Which radio button they selected (sort option)
        editor.apply(); //This writes everything to memory

        // Clear and rebuild the filtered list
        //this is where I filter the memo list based on search and priority
        filteredMemos.clear();
        for (memo m : allMemos) {  //allMemos = Everything from your memo database (no filters)
            //Checks if the memo’s title or description contains the user's search word.
            boolean matchesSearch = m.getName().toLowerCase().contains(searchText)
                    || m.getMText().toLowerCase().contains(searchText);
            //If user picked "All", include everything
            //Otherwise, only include memos that match the selected priority ("High", "Low", etc.)
            boolean matchesPriority = selectedPriority.equals("All")
                    || m.getPriority().equalsIgnoreCase(selectedPriority);
            //Adds that matching memo to the filtered list
            if (matchesSearch && matchesPriority) {
                filteredMemos.add(m);  //filteredMemos = Only what the user wants to see
            }
        }

        // Sort the filtered memos based on selected option
        if (selectedSortId == R.id.sortByPriority) {
            // Sort by High > Medium > Low using custom value by comparing the numeric values of their priority
            //negative means m1 comes before m2, positive means m2 comes before m1, and 0 means they are the same
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
            //displayList is a temporary list of Strings that holds the formatted text you want to show in your ListView
            //It’s built from your filtered memos — and it's what the user actually sees on the screen.
        }

        // Connects the filtered and sorted memo data to the ListView, so it can actually be shown on screen
        // an adapter acts as a bridge between your data (like a list of priorities or memos) and a UI component (like a Spinner or ListView)
        memoListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        memoListView.setAdapter(memoListAdapter);
        // call this to put the data into the ListView and show it on screen
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
