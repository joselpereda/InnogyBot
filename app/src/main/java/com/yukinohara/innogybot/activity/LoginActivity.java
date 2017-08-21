package com.yukinohara.innogybot.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.yukinohara.innogybot.R;
import com.yukinohara.innogybot.activity.Dialog.DialogVoiceRecognized;
import com.yukinohara.innogybot.activity.Dialog.MessageDialogFragment;
import com.yukinohara.innogybot.activity.Dialog.UserInformationDialog;
import com.yukinohara.innogybot.model.VoiceRecorder;
import com.yukinohara.innogybot.services.SpeechService;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.yukinohara.innogybot.databinding.ActivityLoginBinding;
import com.yukinohara.innogybot.model.Employee.Employee;
import com.yukinohara.innogybot.model.Incident.Incident;
import com.yukinohara.innogybot.model.Order.Order;
import com.yukinohara.innogybot.model.Service.Service;
import com.yukinohara.innogybot.network.NetworkCallback;
import com.yukinohara.innogybot.network.NetworkUtils;
import com.yukinohara.innogybot.utils.DataUtils;
import com.yukinohara.innogybot.utils.PreferencesUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding mBinding;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    private SpeechService mSpeechService;
    private VoiceRecorder mVoiceRecorder;

    private DialogVoiceRecognized mDialog;
    private CountDownTimer countDownTimer;
    private UserInformationDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        initViews();
    }

    private void listeningVoice(){
        //Start listening to voices
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED){

            mDialog.show();
            countDownTimer = new CountDownTimer(15000, 15000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    if (mDialog.isShowing()){
                        mDialog.dismiss();
                        mVoiceRecorder.stop();
                    }
                }
            }.start();
            startVoiceRecorder();

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)){
            showPermissionMessageDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        stopVoiceRecord();
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login: {
                login();
                break;
            }
        }
    }

    private void initViews() {
        mDialog = new DialogVoiceRecognized(this);
    }

    private void initData(){

        if (PreferencesUtils.getUserId(this) != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        mBinding.btnLogin.setOnClickListener(this);
    }

    private void login() {
        listeningVoice();
    }

    private void openInputDialog(final String id){
        dialog = new UserInformationDialog(this, new UserInformationDialog.OnClick() {
            @Override
            public void onPositive(String id) {
                getUserTask(id);
            }

            @Override
            public void onNegative() {

            }
        });
        dialog.show();
        dialog.setId(id);
    }

    private void getUserTask(String input){
        new UserTask().execute(input);
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecord(){
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
            mSpeechService.addListenerLoadCredential(mListenerLoadCredential);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }
    };

    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.stop();
                        if (countDownTimer != null){
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                    }
                    if (!TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    mVoiceRecorder.stop();
                                    Log.e(LOG_TAG, "Final = true, Result: " + text);
                                    mDialog.setTextRecognized("");
                                    mDialog.dismiss();
                                    String id = DataUtils.getNumberFromString(text);
                                    openInputDialog(id);
                                } else {
                                    Log.e(LOG_TAG, "Result: " + text);
                                    mDialog.setTextRecognized(text);
                                }
                            }
                        });
                    }
                }
            };

    private SpeechService.ListenerLoadCredential mListenerLoadCredential = new SpeechService.ListenerLoadCredential() {
        @Override
        public void onStart() {
            Log.d("MainActivity", "onStart() load credential");
        }

        @Override
        public void onStop() {
            Log.d("MainActivity", "onStop() load credential");
        }
    };

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }
    };

    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }

    private class UserTask extends AsyncTask<String, Void, Boolean>{
        ProgressDialog progressDialog = null;
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog( LoginActivity.this );

            progressDialog.setTitle("Loading...");

            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            NetworkUtils.getEmployeeById(getApplicationContext(), params[0], new NetworkCallback() {
                @Override
                public void onSuccess(Order order) {

                }

                @Override
                public void onSuccess(Context context, Employee employee) {
                    PreferencesUtils.setUserId(context, String.valueOf(employee.getPersonalNumber()));
                    PreferencesUtils.setUsername(context, employee.getFirstName().trim() + " " + employee.getLastName().trim());
                    progressDialog.dismiss();
                    dialog.dismissDialog();
                    startActivity(new Intent(context, MainActivity.class));
                }

                @Override
                public void onSuccess(Incident incident) {

                }

                @Override
                public void onSuccess(Service service) {

                }

                @Override
                public void onError() {
                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                    alert.setTitle(   R.string.error_title );
                    alert.setMessage( R.string.error_login );
                    alert.setIcon(    R.drawable.icon );
                    alert.setPositiveButton(R.string.confirm_ok, null );
                    alert.show();
                    progressDialog.dismiss();
                    dialog.dismissDialog();
                }

                @Override
                public void onFailed() {
                    progressDialog.dismiss();
                    dialog.dismissDialog();
                    Log.e(LOG_TAG, "On failed");
                }
            });
            return null;
        }
    }
}
