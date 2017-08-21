package com.yukinohara.innogybot.model.Order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YukiNoHara on 6/16/2017.
 */

public class Order {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("cost_center")
    private String mCostCenter;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getCostCenter() {
        return mCostCenter;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", mStatus='" + mStatus + '\'' +
                ", mCostCenter='" + mCostCenter + '\'' +
                '}';
    }
}
