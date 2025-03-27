package com.example.avamemoapp;

import java.util.Calendar;

public class memo {

    private int memoID;
    private String name;

    private String text;

    private String priority;

    private Calendar date;

    public memo (){
        memoID = -1;
        date = Calendar.getInstance();
    }

    public int getMemoID (){
        return memoID;
    }

    public void setMemoID (int memoID){
        this.memoID = memoID;
    }

    public String getName (){
        return name;
    }

    public void setName (String name){
        this.name = name;
    }

    public String getText (){
        return text;
    }

    public void setText (String text){
        this.text = text;
    }

    public String getPriority (){
        return priority;
    }

    public void setPriority (String priority){
        this.priority = priority;
    }

    public Calendar getDate (){
        return date;
    }

    public void setDate (Calendar date){
        this.date = date;
    }


}
