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

import java.util.ArrayList;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {
    //This is the adapter class for the memo list activity
    //This class is responsible for binding the data to the RecyclerView

    private List<memo> memoList;
   //private View.OnMemoClickListener listener;

    /// 1
    ///Constructor for the MemoAdapter class
//    public MemoAdapter(List<memo> memoList, OnMemoClickListener listener) {
//        this.memoList = memoList;
//        this.listener = listener;
//    }


    public MemoAdapter(List<memo> memoList) {
        this.memoList = memoList;
    }




    /// 2
    ///Here we create the ViewHolder class
    /// This class is responsible for holding the views for the RecyclerView
    /// Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_item, parent, false);
        return new MemoViewHolder(v);
    }

    /// 3
    /// Here we bind the data to the RecyclerView
    /// This method is responsible for binding the data to the RecyclerView
    /// Also sets the priority colors
    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        memo memo = memoList.get(position);
        if (memo != null) {
            holder.memoTitle.setText(memo.getName());
            holder.memoText.setText(memo.getMText());
            //holder.memoDate.setText(memo.getDate());


// Set the priority colors
            switch (memo.getPriority().trim()) {
                case "High":
                    holder.priorityIndicator.setBackgroundColor(Color.RED);
                    break;
                case "Medium":
                    holder.priorityIndicator.setBackgroundColor(Color.rgb(255, 165, 0)); // Orange instead of Yellow
                    break;
                case "Low":
                    holder.priorityIndicator.setBackgroundColor(Color.GREEN);
                    break;
                default:
                    ((TextView) holder.priorityIndicator).setTextColor(Color.GRAY); // Default color for unknown priorities
                    break;
            }
        } else{
            // Handles the case where memo is null
            holder.memoTitle.setText("No Title");
            holder.memoText.setText("No Description");
            holder.memoDate.setText("No Date");
            holder.priorityIndicator.setBackgroundColor(Color.GRAY); // Default color for unknown priorities
        }



        /// Set the onClickListener for editing and deleting the memo actions
        //holder.editButton.setOnClickListener(v -> onMemoClickListener.onEditClick(memo));
        //holder.deleteButton.setOnClickListener(v -> onMemoClickListener.onDeleteClick(memo));
    }

    /// 4
    /// Here we get the number of items in the list
    /// This method is responsible for getting the number of items in the list
    @Override
    public int getItemCount() {
        return memoList.size();
    }

    /// 5

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



}
