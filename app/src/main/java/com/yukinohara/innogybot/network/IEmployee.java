package com.yukinohara.innogybot.network;

import com.yukinohara.innogybot.model.Employee.Employee;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by YukiNoHara on 6/16/2017.
 */

public interface IEmployee {
    @GET("Employees/{id}")
    Call<Employee> getEmployee(@Path("id") String id);
}
