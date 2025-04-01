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

        initBackButton();

        //Connect views to layout
        searchBar = findViewById(R.id.searchBar);
        priorityFilter = findViewById(R.id.priorityFilter);
        memoListView = findViewById(R.id.memoListView);
        sortOptions = findViewById(R.id.sortOptions);

        //When user changes sort option, update the list
        sortOptions.setOnCheckedChangeListener((group, checkedId) -> updateList());

        //Load memos from database
        MemoDataSource ds = new MemoDataSource(this);
        ds.open();
        allMemos = ds.getAllMemos();
        ds.close();

        filteredMemos = new ArrayList<>(allMemos);

        //Load saved search + filter from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE);
        String savedSearch = prefs.getString("searchText", "");
        String savedPriority = prefs.getString("selectedPriority", "All");

        //Restore previous search text
        searchBar.setText(savedSearch);

        //Restore previously selected priority
        ArrayAdapter adapter = (ArrayAdapter) priorityFilter.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(savedPriority);
            if (position >= 0) {
                priorityFilter.setSelection(position);
            }
        }

        //Show list at first load
        updateList();

        //Update list as user types
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateList();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        //Update list when user picks a new priority
        priorityFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                updateList();
            }

            @Override public void onNothingSelected(AdapterView parent) {}
        });
    }

    //Filter, sort, and display the memo list
    private void updateList() {
        String searchText = searchBar.getText().toString().toLowerCase();
        String selectedPriority = priorityFilter.getSelectedItem().toString();

        //Save current filter and search in SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("searchText", searchBar.getText().toString());
        editor.putString("selectedPriority", selectedPriority);
        editor.apply();

        //Clear the filtered list and re-fill based on search and priority
        filteredMemos.clear();

        for (memo m : allMemos) {
            boolean matchesSearch = m.getName().toLowerCase().contains(searchText)
                    || m.getMText().toLowerCase().contains(searchText);

            boolean matchesPriority = selectedPriority.equals("All") ||
                    m.getPriority().equalsIgnoreCase(selectedPriority);

            if (matchesSearch && matchesPriority) {
                filteredMemos.add(m);
            }
        }

        //Sort the filtered memos based on selected sort option
        int selectedSortId = sortOptions.getCheckedRadioButtonId();
        //case-insensitive
        if (selectedSortId == R.id.sortByPriority) {
            // Sort alphabetically by priority (High, Low, Medium by default text order)
            filteredMemos.sort((m1, m2) -> m1.getPriority().compareToIgnoreCase(m2.getPriority()));
        } else if (selectedSortId == R.id.sortBySubject) {
            // Sort alphabetically by memo title
            filteredMemos.sort((m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName()));
        } else if (selectedSortId == R.id.sortByDate) {
            // Sort by date (newest first)
            filteredMemos.sort((m1, m2) -> m1.getDate().compareTo(m1.getDate()));
        }

        //Create the display list
        List<String> displayList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        for (memo m : filteredMemos) {
            String title = m.getName();
            String description = m.getMText();
            String priority = m.getPriority();
            String date = sdf.format(m.getDate().getTime());

            String info = title + "\n" + description + "\n" + priority + " - " + date;
            displayList.add(info);
        }

        //Set up the list adapter to display the results
        memoListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        memoListView.setAdapter(memoListAdapter);
    }

    //Back button takes user back to memo list
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
