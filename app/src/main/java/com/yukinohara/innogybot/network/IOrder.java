package com.yukinohara.innogybot.network;

import com.yukinohara.innogybot.model.Order.Order;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by YukiNoHara on 6/16/2017.
 */

public interface IOrder {
    @GET("Orders/{id}")
    Call<Order> getOrder(@Path("id") String id);
}
