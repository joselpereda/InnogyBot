package com.yukinohara.innogybot.network;

import com.yukinohara.innogybot.model.Service.Service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by YukiNoHara on 6/16/2017.
 */

public interface IService {
    @GET("Services/{id}")
    Call<Service> getService(@Path("id") String id);
}
