package com.yukinohara.innogybot.network;

import com.yukinohara.innogybot.model.Incident.Incident;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by YukiNoHara on 6/16/2017.
 */

public interface IIncident {
    @GET("Incidents/{id}")
    Call<Incident> getIncident(@Path("id") String id);
}
