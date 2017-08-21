package com.yukinohara.innogybot.activity.Dialog;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.yukinohara.innogybot.R;

/**
 * Created by YukiNoHara on 6/15/2017.
 */

public class WelcomeDialog{
    public static AlertDialog.Builder getInstance(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Welcome")
                .setMessage(message)
                .setIcon(R.drawable.icon)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder;
    }
}
