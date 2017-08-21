package com.yukinohara.innogybot.activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yukinohara.innogybot.R;

/**
 * Created by YukiNoHara on 6/26/2017.
 */

public class UserInformationDialog extends Dialog implements View.OnClickListener{
    private EditText edt_id;
    private Button btn_confirm_no;
    private Button btn_confirm_yes;
    private OnClick mListener;

    public UserInformationDialog(@NonNull Context context, OnClick mListener) {
        super(context);
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm_no: {
                mListener.onNegative();
                this.dismissDialog();
                break;
            }
            case R.id.btn_confirm_yes: {
                String id = getId();
                mListener.onPositive(id);
                break;
            }
        }
    }

    public interface OnClick{
        void onPositive(String id);
        void onNegative();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_info);
        edt_id = (EditText) findViewById(R.id.edt_input_id);
        btn_confirm_no = (Button) findViewById(R.id.btn_confirm_no);
        btn_confirm_yes = (Button) findViewById(R.id.btn_confirm_yes);

        btn_confirm_no.setOnClickListener(this);
        btn_confirm_yes.setOnClickListener(this);
    }

    public void setId(String id){
        if (!id.equalsIgnoreCase("")){
            edt_id.setText(id);
        } else {
            edt_id.setText("null");
        }
    }

    public String getId(){
        return edt_id.getText().toString().trim();
    }

    public void dismissDialog(){
        this.dismiss();
    }
}
