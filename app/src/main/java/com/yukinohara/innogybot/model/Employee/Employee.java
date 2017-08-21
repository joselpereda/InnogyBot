package com.yukinohara.innogybot.model.Employee;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YukiNoHara on 6/16/2017.
 */

public class Employee {
    @SerializedName("personal_number")
    private int personalNumber;

    @SerializedName("first_name")
    private String mFirstName;

    @SerializedName("last_name")
    private String mLastName;

    public int getPersonalNumber() {
        return personalNumber;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }
}
