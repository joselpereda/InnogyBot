package com.yukinohara.innogybot.network;

import android.content.Context;

import com.yukinohara.innogybot.model.Employee.Employee;
import com.yukinohara.innogybot.model.Incident.Incident;
import com.yukinohara.innogybot.model.Order.Order;
import com.yukinohara.innogybot.model.Service.Service;

/**
 * Created by YukiNoHara on 6/19/2017.
 */

public interface NetworkCallback {
    public void onSuccess(Order order);
    public void onSuccess(Context context, Employee employee);
    public void onSuccess(Incident incident);
    public void onSuccess(Service service);
    public void onError();
    public void onFailed();
}
