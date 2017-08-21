package com.yukinohara.innogybot.model.Service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YukiNoHara on 6/16/2017.
 */

public class Service {
    @SerializedName("service_id")
    private int serviceId;

    @SerializedName("service_name")
    private String mServiceName;

    @SerializedName("phone_number")
    private String mPhoneNumber;

    @SerializedName("email")
    private String mEmail;

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return mServiceName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getEmail() {
        return mEmail;
    }
}
