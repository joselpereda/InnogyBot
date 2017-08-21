package com.yukinohara.innogybot.activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.widget.TextView;

import com.yukinohara.innogybot.R;

/**
 * Created by YukiNoHara on 6/16/2017.
 */

public class DialogVoiceRecognized extends Dialog {
    private TextView tvSpeech;

    public DialogVoiceRecognized(@NonNull Context context) {
        super(context);
    }

    public DialogVoiceRecognized(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected DialogVoiceRecognized(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_voice_recognized);

        tvSpeech = (TextView) findViewById(R.id.tv_speeching);
    }

    public void setTextRecognized(String textRecognized) {
        tvSpeech.setText(textRecognized);
    }
}