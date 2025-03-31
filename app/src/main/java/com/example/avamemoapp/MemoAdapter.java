package com.example.avamemoapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/// //This is the adapter class for the memo list activity
///This class is responsible for binding the data to the RecyclerView
///
/// **MemoAdapter - Adapter class for RecyclerView in memoListActivity**
/// - Binds memo data to RecyclerView.
/// - Handles memo item layout and priority indicators.
public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {


    private List<memo> memoList; /// List holding all memos

    private View.OnClickListener memoClickListener; /// Listener for memo item click

    /// 1
    ///Constructor for the MemoAdapter class
    /// This constructor initializes the memoList with the provided list of memos
    public MemoAdapter(List<memo> memoList) {
        this.memoList = memoList;
    }

    /// 2
    ///Here we create the ViewHolder class
    /// This class is responsible for holding the views for each item in the RecyclerView
    /// Create new views (invoked by the layout manager)
    ///
    /// Creates and returns a ViewHolder for each item**
    ///  Inflates (BOOOOM 💥) the layout for each memo item.
    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_item, parent, false);
        return new MemoViewHolder(v);
    }

    /// 3
    /// Here we bind the data to the ViewHolder
    /// Sets memo title, description, and formatted date
    /// This method is responsible for binding the data to the RecyclerView
    /// Also sets/updates the priority colors
    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        memo memo = memoList.get(position);
        if (memo != null) {
            holder.memoTitle.setText(memo.getName());
            holder.memoText.setText(memo.getMText());

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

        /// Set the onClickListener for editing and deleting the memo actions

    }

    /// 4
    /// Here we get the number of items in the list
    /// This method is responsible for getting the number of items in the list
    /// It returns the size of the memoList
    ///
    @Override
    public int getItemCount() {
        return memoList.size();
    }

    /// 5
    /// Holds view references for each memo item
    /// This class is responsible for holding the views for each item in the RecyclerView
    /// It holds the views for the memo title, description, date, and priority indicator
    /// It also holds the edit and delete buttons
    ///
    public static class MemoViewHolder extends RecyclerView.ViewHolder {
        TextView memoTitle, memoText, memoDate;
        View priorityIndicator;
        //ImageButton editButton, deleteButton;

        public MemoViewHolder(@NonNull View itemView) {
            super(itemView);
            memoTitle = itemView.findViewById(R.id.memoTitle);
            memoText = itemView.findViewById(R.id.memoDescription);
            memoDate = itemView.findViewById(R.id.memoDate);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
            //editButton = itemView.findViewById(R.id.editButton);
            //deleteButton = itemView.findViewById(R.id.deleteButton);
        }

    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        memoClickListener = listener;
    }



}
