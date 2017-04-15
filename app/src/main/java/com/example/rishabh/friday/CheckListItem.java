package com.example.rishabh.friday;

/**
 * Created by rishabh on 14/04/17.
 */
public class CheckListItem {

    private int checkStatus;
    private String itemString;

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getItemString() {
        return itemString;
    }

    public void setItemString(String itemString) {
        this.itemString = itemString;
    }

    public CheckListItem(int checkStatus, String itemString){
        this.checkStatus = checkStatus;
        this.itemString = itemString;
    }

}

