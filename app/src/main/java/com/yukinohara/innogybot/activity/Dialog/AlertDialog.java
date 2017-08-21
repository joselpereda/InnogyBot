package com.yukinohara.innogybot.activity.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import com.yukinohara.innogybot.R;

/**
 * Created by YukiNoHara on 6/23/2017.
 */

public class AlertDialog {
    public static android.support.v7.app.AlertDialog.Builder getInstance(Context context,
                                                                         String title,
                                                                         String message,
                                                                         String positiveButtonLabel,
                                                                         String negativeButtonLabel,
                                                                         String neutralButtonLabel,
                                                                         final DialogCallback callback){
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.icon)
                .setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositiveButtonClick();
                    }
                })
                .setNeutralButton(neutralButtonLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onNeutralButtonClick();
                    }
                })
                .setNegativeButton(negativeButtonLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onNegativeButtonClick();
                    }
                });
        return builder;
    }

    public static android.support.v7.app.AlertDialog.Builder getInstance(Context context,
                                                                         String title,
                                                                         String message,
                                                                         String positiveButtonLabel,
                                                                         String neutralButtonLabel,
                                                                         final DialogCallback callback){
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.icon)
                .setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositiveButtonClick();
                    }
                })
                .setNeutralButton(neutralButtonLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onNeutralButtonClick();
                    }
                });
        return builder;
    }

    public interface DialogCallback{
        public void onPositiveButtonClick();
        public void onNegativeButtonClick();
        public void onNeutralButtonClick();
    }
}
