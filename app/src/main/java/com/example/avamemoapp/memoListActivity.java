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
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

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
    RecyclerView recyclerView; // RecyclerView to display memos
    MemoAdapter memoAdapter;   // Adapter to bind data to RecyclerView
    List<memo> memoList;       // List to store memos from database

    //a listener for when an item on the list is clicked and navigates to the main activity with the data populated
    private View.OnClickListener memoClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            int memoID = memoList.get(position).getMemoID();
            Intent intent = new Intent(memoListActivity.this, MainActivity.class);
            intent.putExtra("memoID", memoID);
            startActivity(intent);
        }
    };


    /// 1
    /// The onCreate method is called when the activity is first created.
    /// - Sets up the layout.
    /// - Loads memos from the database.
    /// - Configures RecyclerView and buttons.
    ///
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memo_list);

        ///  Initialize and open database connection
        MemoDataSource dataSource = new MemoDataSource(this);
        dataSource.open();

        /// Load memos from the database...GIVE ME ALL THE MEMOS!!!!
        memoList = dataSource.getAllMemos();
        dataSource.close();

        /// Initialize RecyclerView.,..gotta love the RecyclerView..let's make sure it's all set up NICELY 🌞 🐳
        recyclerView = findViewById(R.id.memoRecyclerView);  /// Get RecyclerView from XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); /// Arrange items in a vertical list
        memoAdapter = new MemoAdapter(memoList, this); /// Attach adapter to handle memo data

        //for the memo click listener - AJ
        memoAdapter.setOnItemClickListener(memoClickListener);
        recyclerView.setAdapter(memoAdapter); /// Set adapter for RecyclerView

        /// Call the method here to go back to the main activity
        /// Invesiti...I meant Initialize the buttons 😭
        initAddMemo();   // Button to add a new memo
        initNext2Button(); // Button to navigate to settings page
        initDeleteSwitch(); // Switch to enable/disable delete mode

        /// SHE DOESN'T EVEN GO HERE!!!! 🫥😶🤔😑
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
    }

    /// 2
    /// initAddMemo() - Handles "Add Memo" button click.
    /// When clicked, it navigates back to the MainActivity.
    /// //This takes us back to the main activity
    private void initAddMemo() {
        Button button = findViewById(R.id.buttonAddMemo);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(memoListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clears activity stack
                startActivity(intent); // Start MainActivity
            }
        });
    }


    /// 3
    /// This button navigates the user to a MemoSettingsActivity.
    /// This takes us to the MEMO settings page... Is aiden a thing? I said what I said!
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

    /// 4
    ///This is for the Swiper-to-delete functionality
    /// This section will be used to implement swipe-to-delete functionality for memos.
    /// More like a button to delete it just like we did in our contacts list
    ///If the switch is turned on, enables delete mode.
    /// Initializes delete switch for enabling/disabling delete mode
    private void initDeleteSwitch() {
        Switch deleteSwitch = findViewById(R.id.switchDelete);
        deleteSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            memoAdapter.setDelete(isChecked); // Update delete mode in adapter
            memoAdapter.notifyDataSetChanged(); // Notify adapter to refresh the view
        });
    }

}