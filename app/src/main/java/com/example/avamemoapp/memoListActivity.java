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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.Comparator;
import java.util.List;

/// The memoListActivity class is responsible for displaying the list of memos,
/// handling UI elements such as buttons, and controlling navigation to other activities.
/// This is the activity that shows the RecyclerView with all the memos.
public class memoListActivity extends AppCompatActivity {
    RecyclerView recyclerView; // RecyclerView to display memos
    MemoAdapter memoAdapter;   // Adapter to bind data to RecyclerView
    List<memo> memoList;       // List to store memos from database
    //Spinner sortBySpinner; // Spinner for sorting memos


    /// A listener for when an item on the list is clicked and navigates to the main activity with the data populated
    private View.OnClickListener memoClickListener = new View.OnClickListener() {
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
    /// - Loads memos from the database (INITIALIZER..OR WHATEVER 🫥)
    /// - Configures RecyclerView and buttons.
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
        memoAdapter = new MemoAdapter(memoList, this); /// Attach adapter to handle memo data --> creates a new MemoAdapter that takes the memoList

        //for the memo click listener - AJ
        memoAdapter.setOnItemClickListener(memoClickListener); /// Set the click listener for the adapter (item clicks)
        recyclerView.setAdapter(memoAdapter); /// Set adapter for RecyclerView

        /// Call the method here to go back to the main activity
        /// Invesiti...I meant Initialize the buttons 😭
        initAddMemo();   // Button to add a new memo
        initNext2Button(); // Button to navigate to settings page
        initDeleteSwitch(); // Switch to enable/disable delete mode


        ///  1.A
        /// THE SPINNER!!!!!!!!!!!!!!!!!!!!!!!!!!
        /// ***This is the spinner for sorting the memos ---> sorting options***
        ///Setting up the spinner for sorting memos
        /// Initializing the Spinner
        Spinner sortBySpinner = findViewById(R.id.sortBySpinner);

        /// We stored the sorting options in the string.xml to make it easier to modify
        /// Using an ArrayAdapter Helps bind the sorting options TO THE SPINNER EFFICIENTLY
        /// ArrayAdapter.createFromResource(...): This method fetches an array resource (R.array.sort_options) and converts it into an ArrayAdapter

        /// ***Creates an adapter to bind the sorting options defined in the string.xml file to the spinner***
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);

        /// ****Set the layout for the dropdown items (how they essentially will look like)***
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /// ***Sets (assigns) the adapter to the spinner --> so that the sorting options are displayed when the user interacts with the spinner.***
        sortBySpinner.setAdapter(adapter);

        /// 1.B
        /// This is the listener for when the user selects an item from the spinner
        /// ***When the user selects an item from the spinner, it sorts the memos based on the selected option***
        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            /// The parent is the spinner itself) --> (actual item selected)  --> based on the position of the item in the spinner --> unique ID but we don't really use it (as far as rn...)
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortMemos(position); // Call the method to sort memos based on the selected option/changes
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing if no option is selected
            }
            /*Side Notes:
            Basically whatever option the user selects in the spinner is what gets used to
            update (sort) the data that’s displayed in the RecyclerView.
            This way, the displayed list is dynamically updated based on the user’s selection in the spinner.

             */
        });

        /// SHE DOESN'T EVEN GO HERE!!!! 🫥😶🤔😑
        ///*** "Appropriate" layout...
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
    }

    /// 1.C
    /// Method to sort memos based on the selected option from the spinner
    /// This method sorts the memos based on the selected option from the spinner
    /// The sorting options are defined in the string.xml file
    /// The sorting options are:
    /// 0 - Sort by date
    /// 1 - Sort by title (name/subject)
    /// 2 - Sort by priority
    private void sortMemos(int position) {

        /// Basically this part with the sorting (and Comparator) would not be able to work if the version of the android device is lower than Nougat (7.0)
        /// Which unfortunately is the case for some devices (mine included....😑)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            /// Position --> It's the index of the selected item in the spinner that represents what the user chose to sort by...
            switch (position) {
                case 0:
                    /// Sort by date
                    ///  The literal --> "::" is used to refer to a method in a class.
                    /// memo::getDate is a method reference to the getDate method of the memo class.
                    ///***C.c --> creates a comparator that sorts the memo objects by date (based on the date filed)***

                    /// Its literally just ceating a comparator that dorts the memo based off of the date
                    memoList.sort(Comparator.comparing(memo::getDate));
            break;

            case 1:                             /// Sort by title (name/subject)
                memoList.sort(Comparator.comparing(memo::getName));
            break;

            case 2: /// Sort by priority first THEN sorts by name (just in case we have multiple of the same priorities)
                    ///Sorts it by the specified priority that we created down below
                    memoList.sort(Comparator.comparingInt((memo m) -> sortByPriorityToInt(m.getPriority())).thenComparing(memo::getName));
                    //memoList.sort(Comparator.comparing(memo::getPriority).thenComparing(memo::getName));
            break;
        }
            // Log the sorted list for debugging
            for (memo m : memoList) {
                Log.d("Sorted Memo", "Name: " + m.getName() + ", Priority: " + m.getPriority());
            }
        memoAdapter.notifyDataSetChanged(); // Notify adapter to refresh the view
    }
}

    /// 1.D
    ///Sorting the memo priority high - low
    /// Basically, this method converts the priority string to an integer for sorting purposes to use above
    private int sortByPriorityToInt(String color) {
        switch (color.toLowerCase()){
            case "high":
                return 0;
            case "medium":
                return 1;
            case "low":
                return 2;
            case "all":
                return 3; // This is the "All" option
            default:
                //gets the lowest int value for the "All" option --> makes it appear first
                return Integer.MIN_VALUE; // Default...blah..blah..options or even --> unknown priorities

        }
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