package com.example.avamemoapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/// This is the adapter class for the memo list activity
/// 🌟This class is responsible for binding the data to the RecyclerView
/// 🌟 **MemoAdapter - Adapter class for RecyclerView in memoListActivity**
/// - Binds memo data to RecyclerView.
/// - Handles memo item layout and priority indicators.
///
/// Where we bind our memo data to the recycler view --> which is where we display a scrollable list of our memo items
public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {
    private List<memo> memoList; /// List holding all memos that will be displayed in the RecyclerView
    private static View.OnClickListener memoClickListener; /// Listener for memo item click
    private Context Context; /// Context of the parent activity...It helps to access resources, start new activities, or interact with the system.
    private boolean isDeleting = false; /// Flag to indicate if deleting is enabled helps control the visibility of delete buttons or the ability to delete items from the list.

    /// 1
    ///Constructor for the MemoAdapter class
    /// It initializes the memoList (the list of memos to display) and the Context (which is the parent activity context --> helps it interact with the app's resources).
    /// 🌟The memoList holds the data that will be displayed in the UI (like a list of items in a RecyclerView)
    public MemoAdapter(List<memo> memoList, Context Context) {
        this.Context = Context;
        this.memoList = memoList;
    }

    /// 2
    ///Here we create the ViewHolder class
    /// 🌟 This class is responsible for holding the views for each item in the RecyclerView
    /// Create new views (invoked by the layout manager)
    /// Creates and returns a ViewHolder for each item**
    ///  Inflates (BOOOOM 💥) the layout for each memo item.
    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //🌟 This class is responsible for holding the views for each item in the RecyclerView
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_item, parent, false);
        return new MemoViewHolder(v);
    }

    /// 3
    /// Here we bind the data to the ViewHolder
    /// Sets memo title, description, and formatted date
    /// 🌟 This method is responsible for binding the data to the RecyclerView
    /// Also sets/updates the priority colors
    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        //🌟 This method is responsible for binding the data to the RecyclerView
        /// 🌟 Takes an item from the list memoList at a specific position and stores it in a variable called memo
        memo memo = memoList.get(position);
        if (memo != null) { /// Check if memo is not null
        /// 🌟 holder is the ViewHolder that holds the views for each item in the RecyclerView
        /// holder --> is an instance -->
            holder.memoTitle.setText(memo.getName()); /// Set the memo title
            holder.memoText.setText(memo.getMText()); /// Set the memo description

            /// Convert Calendar to formatted date string
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(memo.getDate().getTime()); /// Convert Calendar to Date first
            holder.memoDate.setText(formattedDate);

            /// Set the priority colors ---> INDICATORSSSSSSSSSS
            switch (memo.getPriority().trim()) {
                case "High":
                    holder.priorityIndicator.setBackgroundColor(Color.RED);
                    break;
                case "Medium":
                    holder.priorityIndicator.setBackgroundColor(Color.rgb(255, 165, 0)); /// Orange instead of Yellow
                    break;
                case "Low":
                    holder.priorityIndicator.setBackgroundColor(Color.GREEN);
                    break;
                default:
                    holder.priorityIndicator.setBackgroundColor(Color.LTGRAY); /// Default color for unknown priorities (basically the option that says ALLLLLL)
                    break;
            }
        } else{
            /// Handles the case where memo is null
            holder.memoTitle.setText("No Title");
            holder.memoText.setText("No Description");
            holder.memoDate.setText("No Date");
            holder.priorityIndicator.setBackgroundColor(Color.GRAY); // Default color for unknown priorities
        }

        /// Handle Delete Mode
        /// isDeleting is a boolean flag that determines whether delete mode is active.
        if (isDeleting) {
            /// This makes the delete button visible for the memo item in the RecyclerView.
            holder.deleteButton.setVisibility(View.VISIBLE);
            /// Set the delete button's click listener
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /// When the delete button is clicked, it calls the deleteItem method
                    /// and gets the position of the memo in the list and deletes it from the database and updates the RecyclerView.
                    deleteItem(holder.getAdapterPosition());
                }
            });
            /// If isDeleting is false, the delete button remains hidden
        } else {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }
    } //End of BIND

    /// 4
    /// Holds view references for each memo item
    /// 🌟 This class is responsible for holding the views for each memo item in the RecyclerView
    /// 🌟 It holds the views for the memo title, description, date, and priority indicator
    /// It also holds the edit and delete buttons
    ///
    public class MemoViewHolder extends RecyclerView.ViewHolder { ///subclass of RecyclerView.ViewHolder
        // 🌟 This class is responsible for holding the views for each memo item in the RecyclerView
        public View deleteButton; ///delete button
        TextView memoTitle, memoText, memoDate; /// text fields for memo title, description, and date
        View priorityIndicator; /// priority indicator (color bar)

        public MemoViewHolder(@NonNull View itemView) { ///constructor for the ContactViewHolder class --> runs when a new list item is created
            super(itemView); ///calls the constructor of the parent class RecyclerView.ViewHolder

            ///These down below find the text fields and button from the item's layout file
            memoTitle = itemView.findViewById(R.id.memoTitle);
            memoText = itemView.findViewById(R.id.memoDescription);
            memoDate = itemView.findViewById(R.id.memoDate);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
            deleteButton = itemView.findViewById(R.id.buttonDeleteMemo);

            ///TAGS
            itemView.setTag(this); ///sets the tag of the view (so that we can identify which item was clicked)
            itemView.setOnClickListener(memoClickListener);///sets the onClickListener for the view --> this allows us to detect when someone clicks a memo.

        }

    }// End of ViewHolder class

    /// 5
    /// 🌟 Here is where we have the click listener that opens up the memo we selected to edit it
    public void setOnItemClickListener(View.OnClickListener listener) {
        memoClickListener = listener;
    }


    /// 6
    /// Here we get the number of items in the list
    /// 🌟 This method is responsible for getting the number of items in the list
    /// 🌟 It returns the size of the memoList

    @Override
    public int getItemCount() {//gets the size of the list of memo (of the database)
        return memoList.size();
    }

    /// 7
    /// 🌟 This method is used to delete a memo from the list
    /// 🌟 It removes the memo from the list and notifies the adapter of the change
    private void deleteItem(int position) {// removes a memo from the list when the delete button is clicked
        /// 🌟 Takes an item from the list memoList at a specific position and stores it in a variable called memo
        memo memo = memoList.get(position); //gets the memo at the given position in the list
        MemoDataSource dbHelper = new MemoDataSource(Context);//creates a new MemoDataSource object --> used to access the database

        try {//tries to delete the memo
            dbHelper.open();//opens the database
            boolean didDelete = dbHelper.deleteMemo(memo.getMemoID());//deletes the memo
            dbHelper.close();//closes the database

            if (didDelete) {//if the memo was deleted
                memoList.remove(position); // Remove item from the list
                notifyDataSetChanged(); //notifies the adapter that the data has changed and updates it
                //Toast.makeText(Context, "Memo Deleted", Toast.LENGTH_SHORT).show();

            }
            else {//if the memo was not deleted
                Toast.makeText(Context, "Delete Failed!", Toast.LENGTH_SHORT).show();//shows a pop-up message
            }
        }
        catch (Exception e) {//if there is an exception, show a pop-up message
            //Toast.makeText(Context, "Error Deleting Memo!", Toast.LENGTH_SHORT).show();
            //e.printStackTrace(); // Log the error for debugging

        }
    }

    /// 8
    /// Here this method turns delete mode (switch) on or off
    /// In MemoListActivity, this method is called when the delete button is clicked
    public void setDelete(boolean isDeleting) {
        //controls whether delete functionality is active.
        this.isDeleting = isDeleting; //sets the isDeleting flag to the given value
    }


}

