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
    /// The onCreate method is called when the activity is first created --> Sets up the layout.
    /// 🌟 Loads memos from the database (INITIALIZER..OR WHATEVER 🫥)
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

        ///For the memo click listener - AJ
        memoAdapter.setOnItemClickListener(memoClickListener); /// Set the click listener for the adapter (item clicks)
        recyclerView.setAdapter(memoAdapter); /// Set adapter for RecyclerView

        /// Invesiti...I meant Initialize the buttons 😭
        initAddMemo();   // Button to add a new memo
        initNext2Button(); // Button to navigate to settings page
        initDeleteSwitch(); // Switch to enable/disable delete mode


        ///  1.A
        /// THE SPINNER!!!!!!!!!!!!!!!!!!!!!!!!!!
        /// 🌟 ***This is the spinner for sorting the memos ---> sorting options*** it is initializing the Spinner
        Spinner sortBySpinner = findViewById(R.id.sortBySpinner);

        /// We stored the sorting options in the string.xml to make it easier to modify
        /// 🌟 Using an ArrayAdapter Helps bind the sorting options TO THE SPINNER EFFICIENTLY
        /// ArrayAdapter.createFromResource(...): This method fetches an array resource (R.array.sort_options) and converts it into an ArrayAdapter
        /// 🌟 ***Creates an adapter to bind the sorting options defined in the string.xml file to the spinner***
        /// 🌟 CharSequence represents a sequence of characters (like a string) that can be used in the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);

        /// 🌟 ****Set the layout for the dropdown items (how they essentially will look like)***
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /// 🌟 ***Sets (assigns) the adapter to the spinner --> so that the sorting options are displayed when the user interacts with the spinner.***
        sortBySpinner.setAdapter(adapter);

        /// 1.B
        /// This is the listener for when the user selects an item from the spinner
        /// 🌟 ***When the user selects an item from the spinner, it sorts the memos based on the selected option***
        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            /// The parent is the spinner itself) --> (actual item selected)  --> based on the position of the item in the spinner --> unique ID but we don't really use it (as far as rn...)
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                sortMemos(); // Call the method to sort memos based on the selected option/changes
            }
            @Override
            public void onNothingSelected(AdapterView parent) {
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
    /// 🌟Method to sort memos based on the selected option from the spinner (that the user selected)
    private void sortMemos() {
        /// Gets a reference to the spinner from the layout
        Spinner sortBySpinner = findViewById(R.id.sortBySpinner);

        /// 🌟 Grabs the CURRENT selection (what the user picked) from the spinner and then returns it as a string so we can use it later (DOWN THE LINE)
        String selectedOption = sortBySpinner.getSelectedItem().toString(); // Get the selected option from the spinner

        ///This is just for debugging purposes ----> to makes sure the selected option is being logged correctly
        Log.d("MemoListActivity", "Selected sorting option: " + selectedOption); // Log the selected option for debugging

        /// 🌟 Sort the memoList based on the selected option ---> (the string we got from the spinner)
        switch (selectedOption) {
            case "Date":
                /// This line checks if the version of Android the user is running supports certain sorting methods (N ---> is Android Nougat)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    /// 🌟 Sorts the memo based on the date AND compares each memo date using the getDate() method in the memo class
                    /// Sort Date from earliest/oldest to newest/most recent

                    /// 🌟 Why use COMPARATOR??? Because it allows us to compare objects (OUR MEMOS) in a customizable way,
                    /// 🌟 We're basically telling java the specific way we want our memos to be sorted
                    /// 🌟 The :: operator is essentially referencing a method by its name.
                    memoList.sort(Comparator.comparing(memo::getDate));
                }
                break; // just stops here and doesn't check the other cases

            case "Priority":
                /// Sort by High > Medium > Low
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    /// Sorts the memo based on the priority AND compares each memo priority using the getPriority() method in the memo class
                    /// Sorts the memo based on the priority (High > Medium > Low) using a custom method to get the priority value
                    /// 🌟 This is done by comparing the priority values using the getPriorityValue() method
                    /// This method is defined below and returns an integer value for each priority level
                    /// The memoList is sorted in descending order (High > Medium > Low)

                    /// 🌟 Compares the memo priorities using the getPriority() method and converts them to
                    /// 🌟 integer values so that we can sort them from highest to lowest

                    /// 🌟 This code is comparing the priority values of the two memo objects (memo1 and memo2) based on their priority strings, and sorting them in a specific order.
                    memoList.sort((memo1, memo2) -> Integer.compare(getPriorityValue(memo2.getPriority()), getPriorityValue(memo1.getPriority())));
                }
                break;

            case "Subject":
                /// 🌟 Sort by Subject (Title) A-Z
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    /// Sorts the memo based on the name AND compares each memo name using the getName() method in the memo class
                    /// Sorts the memo based on the name (Title) in alphabetical order (A-Z)
                    /// This is done by comparing the memo names using the getName() method
                    /// The memoList is sorted in ascending order (A-Z)

                    /// 🌟 Compares each memo's name using the getName() method and sorts them in alphabetical order
                    memoList.sort(Comparator.comparing(memo::getName)); // Sort Alphabetically by name (Title/Subject)
                }
                break;
            default:
                break;
        }

        /// Notify the adapter that the data has changed to refresh the view to show the new sorted list of memos
        memoAdapter.notifyDataSetChanged();
    }
    private int getPriorityValue(String priority) {
        /// 🌟This method helps convert a string priority (like "High") into a number(int) value (like 3 for "High").
        /// 🌟It’s used to make the priority sorting work.

        switch (priority) {
            case "High": //If the priority is "High," it returns 3, which represents high priority.
                return 3;
            case "Medium": //If the priority is "Medium," it returns 2, which represents medium priority.
                return 2;
            case "Low": //If the priority is "Low," it returns 1, which represents low priority.
                return 1;
            default: // If the priority is not any of the options above, it returns 0 as a default value.
                return 0; // Default value for unknown priorities
        }
    }

    /// 2
    /// initAddMemo() - Handles "Add Memo" button click.
    /// When clicked, it navigates back to the MainActivity where we enter the memo details.
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
    /// More like a button to delete it just like we did in our contacts list
    /// 🌟 If the switch is turned on, enables delete mode.
    /// 🌟 Initializes delete switch for enabling/disabling delete mode
    private void initDeleteSwitch() {
        Switch deleteSwitch = findViewById(R.id.switchDelete);
        deleteSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            memoAdapter.setDelete(isChecked); /// Update delete mode in adapter
            memoAdapter.notifyDataSetChanged(); /// Notify adapter to refresh the view
        });
    }

}