package com.yukinohara.innogybot.network;

import android.content.Context;
import android.util.Log;

import com.yukinohara.innogybot.R;
import com.yukinohara.innogybot.model.Employee.Employee;
import com.yukinohara.innogybot.model.Incident.Incident;
import com.yukinohara.innogybot.model.Order.Order;
import com.yukinohara.innogybot.model.Service.Service;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YukiNoHara on 6/14/2017.
 */

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    public static MessageResponse getResponseFromWatson(Context mContext, String username, String password, String workspaceId, String input, Map<String, Object> context){
        ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
        service.setUsernameAndPassword(username, password);
        service.setEndPoint(mContext.getString(R.string.endpoint_conversation));
        MessageRequest message = new MessageRequest.Builder().inputText(input).context(context).build();
        Log.e(LOG_TAG, "Message request: " + message.toString());
        MessageResponse response = service.message(workspaceId, message).execute();
        return response;
    }

    public static void getIncidentById(Context context, String id, final NetworkCallback callback){
        IIncident iIncident = ApiService.getRetrofitInstance(context).create(IIncident.class);
        Call<Incident> call = iIncident.getIncident(id);
        call.enqueue(new Callback<Incident>() {
            @Override
            public void onResponse(Call<Incident> call, Response<Incident> response) {
                if (response.isSuccessful() && response.code() == 200){
                    Incident incident = response.body();
                    callback.onSuccess(response.body());
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<Incident> call, Throwable t) {
                callback.onFailed();
            }
        });
    }

    public static void getServiceById(Context context, String id, final NetworkCallback callback){
        IService iService = ApiService.getRetrofitInstance(context).create(IService.class);
        Call<Service> call = iService.getService(id);
        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful() && response.code() == 200){
                    callback.onSuccess(response.body());
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                callback.onFailed();
            }
        });
    }

    public static void getEmployeeById(final Context context, String id, final NetworkCallback callback){
        IEmployee iEmployee = ApiService.getRetrofitInstance(context).create(IEmployee.class);
        Call<Employee> call = iEmployee.getEmployee(id);
        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if (response.isSuccessful() && response.code() == 200){
                    callback.onSuccess(context, response.body());
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                callback.onFailed();
            }
        });

    }

    public static void getOrderById(Context context, String id, final NetworkCallback callback){
        IOrder iOrder = ApiService.getRetrofitInstance(context).create(IOrder.class);
        Call<Order> call = iOrder.getOrder(id);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.code() == 200){
                    callback.onSuccess(response.body());
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                callback.onFailed();
            }
        });
    }
}
