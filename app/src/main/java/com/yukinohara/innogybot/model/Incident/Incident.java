package com.yukinohara.innogybot.model.Incident;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YukiNoHara on 6/16/2017.
 */

public class Incident {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("remaining")
    private String mRemaining;

    @SerializedName("last_entry_date")
    private String mLastEntryDate;

    @SerializedName("assigned_group")
    private String mAssignedGroup;

    @SerializedName("last_entry_words")
    private String mLastEntryWords;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getRemaining() {
        return mRemaining;
    }

    public String getLastEntryDate() {
        return mLastEntryDate;
    }

    public String getAssignedGroup() {
        return mAssignedGroup;
    }

    public String getLastEntryWords() {
        return mLastEntryWords;
    }
}
