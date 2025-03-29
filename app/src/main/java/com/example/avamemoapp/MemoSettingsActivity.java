package com.example.avamemoapp;

import android.content.Intent;
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
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.core.view.WindowInsetsCompat.Insets;
//import androidx.core.view.WindowInsetsControllerCompat;
//import androidx.core.view.WindowInsetsControllerCompat.Behavior;
//import androidx.core.view.WindowInsetsControllerCompat.Type;

//import com.google.android.material.edgeeffect.EdgeToEdge;

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
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memo_settings);

        initHome2Button(); // Home button
        initBackButton();  // Back button

//        // Handle screen edge insets
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.memo_filter_page), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // Connect views
        searchBar = findViewById(R.id.searchBar);
        priorityFilter = findViewById(R.id.priorityFilter);
        memoListView = findViewById(R.id.memoListView);

        // Load data
        MemoDataSource ds = new MemoDataSource(this);
        ds.open();
        allMemos = ds.getAllMemos();
        ds.close();

        filteredMemos = new ArrayList<>(allMemos);
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

    // Update the list view with filtered + searched memos
    private void updateList() {
        String searchText = searchBar.getText().toString().toLowerCase();
        String selectedPriority = priorityFilter.getSelectedItem().toString();

        filteredMemos.clear();

        for (memo m : allMemos) {
            //If the title OR the description includes the search word, this becomes true
            boolean matchesSearch = m.getName().toLowerCase().contains(searchText)
                    || m.getMText().toLowerCase().contains(searchText);
            //If the user chose "All" in the priority filter, we include everything, otherwise we only include the selected priority
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

    //Home button goes to MainActivity
    private void initHome2Button() {
        Button button = findViewById(R.id.buttonHome2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoSettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
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
