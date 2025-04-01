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
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MemoSettingsActivity extends AppCompatActivity {

    EditText searchBar;
    Spinner priorityFilter;
    ListView memoListView;

    List<memo> allMemos;
    List<memo> filteredMemos;
    ArrayAdapter<String> memoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_settings);

        initBackButton();  //Back button

        //Connect views
        searchBar = findViewById(R.id.searchBar);
        priorityFilter = findViewById(R.id.priorityFilter);
        memoListView = findViewById(R.id.memoListView);

        //Load data
        MemoDataSource ds = new MemoDataSource(this);
        ds.open();
        allMemos = ds.getAllMemos();
        ds.close();

        filteredMemos = new ArrayList<>(allMemos);

        //Load saved search + priority from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE);
        String savedSearch = prefs.getString("searchText", "");
        String savedPriority = prefs.getString("selectedPriority", "All");

        //Set search bar to last typed text
        searchBar.setText(savedSearch);

        //Set spinner to last selected priority
        ArrayAdapter adapter = (ArrayAdapter) priorityFilter.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(savedPriority);
            if (position >= 0) {
                priorityFilter.setSelection(position);
            }
        }

        //Initial list display
        updateList();

        //TextWatcher: search as you type
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateList();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        //Spinner: filter by priority
        priorityFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                updateList();
            }

            @Override public void onNothingSelected(AdapterView parent) {}
        });
    }

    //Update the list view with filtered + searched memos
    private void updateList() {
        String searchText = searchBar.getText().toString().toLowerCase();
        String selectedPriority = priorityFilter.getSelectedItem().toString();

        //Save the current search and filter choice
        SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("searchText", searchBar.getText().toString());
        editor.putString("selectedPriority", selectedPriority);
        editor.apply();

        filteredMemos.clear();

        for (memo m : allMemos) {
            //If the title OR the description includes the search word, this becomes true
            boolean matchesSearch = m.getName().toLowerCase().contains(searchText)
                    || m.getMText().toLowerCase().contains(searchText);
            //If the user chose "All" in the priority filter, we include everything
            boolean matchesPriority = selectedPriority.equals("All") || m.getPriority().equalsIgnoreCase(selectedPriority);

            if (matchesSearch && matchesPriority) {
                filteredMemos.add(m);
            }
        }

        List<String> displayList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        //Goes through each memo in the list and builds a short summary of that memo as a String
        for (memo m : filteredMemos) {
            String title = m.getName();
            String description = m.getMText();
            String priority = m.getPriority();
            String date = sdf.format(m.getDate().getTime());

            String info = title + "\n" + description + "\n" + priority + " - " + date;
            displayList.add(info);
        }

        memoListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        memoListView.setAdapter(memoListAdapter);
    }

    //Back button goes to memoListActivity
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
