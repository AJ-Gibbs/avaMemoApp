package com.example.avamemoapp;

/*
This is the memoListActivity class. This class is responsible for the memo list activity:
- This class is responsible for the memo list activity and the functionality.
1) Create an adapter to display the list of memos --> it binds the data to the RecyclerView
2) Create a ViewHolder class to hold the views for the RecyclerView
3) Create a method to initialize the add memo button
4) Create a method to initialize the home button
5) Create a method to initialize the next button
6) Handling the Sorting --> add a listener to the spinner and sorting list

 */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/// The memoListActivity class is responsible for displaying the list of memos,
/// handling UI elements such as buttons, and controlling navigation to other activities.
/// This is the activity that shows the RecyclerView with all the memos.
public class memoListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MemoAdapter memoAdapter;
    private List<memo> memoList;

    /// 1
    /// The onCreate method is called when the activity is first created.
    /// ***It sets up the layout, adjusts for system window insets and initializes various buttons (Add Memo, Home, and Next buttons).***
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memo_list);

        // Initialize MemoDataSource
        MemoDataSource dataSource = new MemoDataSource(this);
        dataSource.open();

        // Load memos from the database
        memoList = dataSource.getAllMemos();  // Assume this method retrieves all memos from the database
        dataSource.close();



        // Initialize RecyclerView
        recyclerView = findViewById(R.id.memoRecyclerView);  // Assume you've defined recyclerView in your layout XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        memoAdapter = new MemoAdapter(memoList);
        recyclerView.setAdapter(memoAdapter);

        //Call the method here to go back to the main activity
        initAddMemo();
        initHomeButton();
        initNext2Button();

        //This is where the memo list and recycler view is created

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
    }



    /// 2
    /// ***initAddMemo(): This method sets up a button (buttonAddMemo) that, when clicked, navigates the user back to the main activity (MainActivity).***
    //This takes us back to the main activity
    private void initAddMemo() {
        Button button = findViewById(R.id.buttonAddMemo);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(memoListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /// 3
    ///  Similar to initAddMemo(), but this button takes the user back to the main activity.
    //This takes us back to the main activity (basically does the same thing as the initAddMemo method just wanted to try something different...kind of...ehhh)
    private void initHomeButton() {
        Button button = findViewById(R.id.buttonHome);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(memoListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /// 4
    /// This button navigates the user to a MemoSettingsActivity.
    //This takes us to the settings page
    private void initNext2Button() {
        Button button = findViewById(R.id.buttonNext2);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(memoListActivity.this, MemoSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    //This is for the Swiper-to-delete functionality






}