package com.yukinohara.innogybot.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.yukinohara.innogybot.R;
import com.yukinohara.innogybot.activity.Dialog.DialogVoiceRecognized;
import com.yukinohara.innogybot.activity.Dialog.MessageDialogFragment;
import com.yukinohara.innogybot.activity.Dialog.WelcomeDialog;
import com.yukinohara.innogybot.activity.adapter.MessageAdapter;
import com.yukinohara.innogybot.databinding.ActivityMainBinding;
import com.yukinohara.innogybot.model.Employee.Employee;
import com.yukinohara.innogybot.model.Incident.Incident;
import com.yukinohara.innogybot.model.Message;
import com.yukinohara.innogybot.model.Order.Order;
import com.yukinohara.innogybot.model.Service.Service;
import com.yukinohara.innogybot.model.VoiceRecorder;
import com.yukinohara.innogybot.network.NetworkCallback;
import com.yukinohara.innogybot.network.NetworkUtils;
import com.yukinohara.innogybot.services.SpeechService;
import com.yukinohara.innogybot.utils.DataUtils;
import com.google.gson.internal.LinkedTreeMap;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.conversation.v1.model.Entity;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.yukinohara.innogybot.utils.PreferencesUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
                          implements View.OnClickListener,
                                     MessageDialogFragment.Listener,
                                     MessageAdapter.OnItemClickListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private MessageAdapter mAdapter;
    private ArrayList<Message> mList;

    private Map<String, Object> context;
    private VoiceRecorder mVoiceRecorder;
    private SpeechService mSpeechService;
    private DialogVoiceRecognized mDialog;
    private CountDownTimer countDownTimer;
    private StreamPlayer streamPlayer;
    private TextToSpeech textToSpeech;

    //Account of APIs
    final String USERNAME_CV = "070eecf8-ef4f-4b2d-96bd-de2f069c5f4a";
//    final String USERNAME_CV = "87dc360e-18d8-4d87-b88a-c2a7593ecdf4";
    final String PASSWORD_CV = "OHrwvKnqec8s";
//    final String PASSWORD_CV = "n6ywCvdrqHZT";
    final String WORKSPACE_ID_TESTER = "a2a27ebd-70ca-425b-ba1b-b6e71a53cb44";
//    final String WORKSPACE_ID_TESTER = "a42df7a3-e568-42a5-8f44-e09fbc0f594c";
    final String USERNAME_TTS = "ba6a920e-982d-4ebd-900c-f3612b35d3ec";
//    final String USERNAME_TTS = "fa67f19f-3b29-4131-b04e-20fe0878e82a";
    final String PASSWORD_TTS = "ZDRVGZuzfytH";
//    final String PASSWORD_TTS = "xzplpnLpeDlG";

    private static final int CALL_REQUEST_CODE = 101;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";
    private boolean isDismiss = false;
    private boolean isShowDialog = false;
    private boolean isEmptyRecognized = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey("Dialog")){
                isShowDialog = savedInstanceState.getBoolean("Dialog");
            }
            if (savedInstanceState.containsKey("List")){
                Log.e(LOG_TAG, "Has key List");
                mList = savedInstanceState.getParcelableArrayList("List");
            } else {
                Log.e(LOG_TAG, "Don't have key List");
            }
            if (savedInstanceState.containsKey("State")){
                Log.e(LOG_TAG, "Has key State");
                context = (Map<String, Object>) savedInstanceState.getSerializable("State");
            } else {
                Log.e(LOG_TAG, "Don't have key State");
            }
        }
        initView();
    }
    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopVoiceRecord();
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("Dialog", isShowDialog);
        outState.putParcelableArrayList("List", mList);
        outState.putSerializable("State", (Serializable) context);
    }

    private void initView() {
        mAdapter = new MessageAdapter(this);
        if (mList == null){
            mList = new ArrayList<>();
        }
        mDialog = new DialogVoiceRecognized(this);
        textToSpeech = new TextToSpeech();
        textToSpeech.setUsernameAndPassword(USERNAME_TTS, PASSWORD_TTS);
        if (context == null){
            refreshContext();
        }
        checkPermission();
        if (!isShowDialog){
            showWelcomeDialog();
            addWelcomeMessage();
            isShowDialog = true;
        }
    }

    private void initData() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mBinding.rvMessage.setLayoutManager(linearLayoutManager);
        mBinding.rvMessage.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setData(mList);
        mBinding.rvMessage.setHasFixedSize(true);
        mBinding.rvMessage.setAdapter(mAdapter);

        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

        mBinding.rlAction.setOnClickListener(this);
        mBinding.btnSend.setOnClickListener(this);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mVoiceRecorder != null){
                    mVoiceRecorder.stop();
                    isDismiss = true;
                }
            }
        });
    }

    private void showWelcomeDialog(){
        AlertDialog.Builder builder;
        if (PreferencesUtils.getUserId(this) != null){
            builder = WelcomeDialog.getInstance(this, "Hallo " + PreferencesUtils.getUsername(this) + getString(R.string.welcome_message));
        } else {
            builder = WelcomeDialog.getInstance(this, "Hallo " + getString(R.string.welcome_message));
        }
        builder.show();

    }

    private void addWelcomeMessage(){
        if (PreferencesUtils.getUserId(this) != null){
            mList.add(new Message(2, "Hallo " + PreferencesUtils.getUsername(this) + getString(R.string.welcome_message)));
        } else {
            mList.add(new Message(2, "Hallo " + getString(R.string.welcome_message)));
        }
    }

    private void refreshContext(){
        context = new HashMap<>();
        context.put("req", "none");
        LinkedTreeMap<String, String> incidentMap = new LinkedTreeMap<>();
        incidentMap.put("status", "none");
        context.put("incident", incidentMap);
    }

    private void sendMessage(final String input) {
        mList.add(new Message(1, input));
        mAdapter.setData(mList);
        if (mAdapter.getItemCount() > 1) {
            mBinding.rvMessage.smoothScrollToPosition(mList.size() - 1);
        }
        new RequestTask().execute(input);
    }

    private void sendMessageWithoutReceivingResponse(String input){
        new RequestTaskWithoutReceivingResponse().execute(input);
    }

    private void makeRequest(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CALL_PHONE},
                CALL_REQUEST_CODE
        );
    }

    private void checkPermission(){
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest();
        }
    }

    private void playVoice(String text){
        new VoiceTask().execute(text);
    }

    private void getOrderTask(String orderNumber){
        new OrderTask().execute(orderNumber);
    }

    private void getIncidentTask(String incidentNumber){
        new IncidentTask().execute(incidentNumber);
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
                        if (isEmptyRecognized){
                            Log.e(LOG_TAG, "Play voice when expired time");
                            playVoice(getString(R.string.expired_time_dialog));
                        }
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

    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }

    private class RequestTaskWithoutReceivingResponse extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            if (checkInternetConnection()){
                NetworkUtils.getResponseFromWatson(getApplicationContext(), USERNAME_CV, PASSWORD_CV, WORKSPACE_ID_TESTER, params[0], context);
            }
            return null;
        }
    }

    private class RequestTask extends AsyncTask<String, Void, MessageResponse>{
        @Override
        protected MessageResponse doInBackground(String... params) {
            if (checkInternetConnection()){
                MessageResponse response = NetworkUtils.getResponseFromWatson(getApplicationContext(), USERNAME_CV, PASSWORD_CV, WORKSPACE_ID_TESTER, params[0], context);
                return response;
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(MessageResponse messageResponse) {
            super.onPostExecute(messageResponse);
            if (messageResponse != null){
                if (messageResponse.getContext() != null) {
                    context.clear();
                    context = messageResponse.getContext();
                }

                Log.e(LOG_TAG, "Response: " + messageResponse.toString());
                if (messageResponse.getOutput() != null && messageResponse.getOutput().containsKey("text")) {
                    ArrayList listResponse = (ArrayList) messageResponse.getOutput().get("text");
                    if (!listResponse.isEmpty()) {
                        if (context.containsKey("new_password") && context.get("new_password").equals("required")){
                            Log.e(LOG_TAG, "Password");
                            boolean isCorrectPassword = false;
                            String pass = null;
                            while (!isCorrectPassword){
                                pass = DataUtils.getDefaultPassword();
                                isCorrectPassword = DataUtils.isCorrect(pass);
                            }
                            String res = "Ihr neues Passwort ist " + pass;
                            mList.add(new Message(2, res));
                            final String text = DataUtils.convertDefaultPasswordToFinishedPassword(pass);
                            playVoice("Ihr neues Passwort ist " + text);
                            refreshContext();
                        } else if(context.containsKey("ticket_request")){
                            Log.e(LOG_TAG, "Ticket or Incident");
                            LinkedTreeMap<String, String> ticketRequestList = (LinkedTreeMap<String, String>) context.get("ticket_request");
                            List<Entity> listEntities = messageResponse.getEntities();
                            boolean isConfigOutput = false;
                            if (    ticketRequestList.get("type").equalsIgnoreCase("order") &&
                                    !ticketRequestList.get("number").equalsIgnoreCase("unknown") &&
                                    ticketRequestList.get("status").equalsIgnoreCase("confirm")){

                                if (listEntities.size() != 0){
                                    if (listEntities.get(0).getValue().equalsIgnoreCase("yes")){
                                        getOrderTask(ticketRequestList.get("number"));
                                    } else if (listEntities.get(0).getValue().equalsIgnoreCase("no")){

                                    } else {
                                        Log.e(LOG_TAG, "Unknown context and entities");
                                    }
                                } else {
                                    isConfigOutput = true;

                                }

                            } else if (ticketRequestList.get("type").equalsIgnoreCase("incident") &&
                                       !ticketRequestList.get("number").equalsIgnoreCase("unknown") &&
                                       ticketRequestList.get("status").equalsIgnoreCase("confirm") &&
                                       listEntities.size() != 0){

                                if (listEntities.size() != 0){
                                    if (listEntities.get(0).getValue().equalsIgnoreCase("yes")){
                                        getIncidentTask(ticketRequestList.get("number"));
                                    } else if (listEntities.get(0).getValue().equalsIgnoreCase("no")){

                                    } else {
                                        Log.e(LOG_TAG, "Unknown context and entities");
                                    }
                                } else {
                                    isConfigOutput = true;
                                }

                            }
                            final String text = (String) listResponse.get(0);
                            mList.add(new Message(2, text));
                            if (isConfigOutput){
                                playVoice(DataUtils.getConfigNumberStringFromResponse(text));
                            } else {
                                playVoice(text);
                            }
                        } else {
                            Log.e(LOG_TAG, "Information and Open incident");
                            final String text = (String) listResponse.get(0);
                            mList.add(new Message(2, text));
                            playVoice(text);
                        }
                        mAdapter.setData(mList);
                        if (mAdapter.getItemCount() > 1) {
                            mBinding.rvMessage.smoothScrollToPosition(mList.size() - 1);
                        }
                        if (context.containsKey("req")){
                            if (context.get("req").equals("completed")){
                                Log.e(LOG_TAG, "Context finish: " + context.toString());
                                refreshContext();
                            }
                        }

                    }
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Error")
                        .setMessage("Internet connection is interrupted")
                        .setIcon(R.drawable.icon)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.show();

            }
        }
    }

    private class VoiceTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            streamPlayer = new StreamPlayer();
            if (params[0] != null && !params[0].isEmpty()) {
                textToSpeech.setEndPoint(getString(R.string.endpoint_texttospeed));
                streamPlayer.playStream(textToSpeech.synthesize(params[0], Voice.DE_BIRGIT).execute());
            }
            else {
                streamPlayer.playStream(textToSpeech.synthesize("No Text Specified", Voice.EN_LISA).execute());
            }
            return null;
        }
    }

    private class OrderTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... params) {
            NetworkUtils.getOrderById(getApplicationContext(), params[0], new NetworkCallback() {
                @Override
                public void onSuccess(Order order) {
                    Log.e(LOG_TAG, "Order: " + order.toString());
                    String userId = String.valueOf(order.getUserId());
                    if (userId.equalsIgnoreCase(PreferencesUtils.getUserId(getApplicationContext()))){
                        LinkedTreeMap<String, String> mapNewTicketRequest = new LinkedTreeMap<String, String>();
                        mapNewTicketRequest.put("type", "order");
                        mapNewTicketRequest.put("number", String.valueOf(order.getId()));
                        mapNewTicketRequest.put("status", order.getStatus());
                        context.put("ticket_request", mapNewTicketRequest);
                        sendMessageWithoutReceivingResponse("");
                        String mess = DataUtils.convertResponseFromDataByCondition(order.getStatus(), order.getCostCenter());
                        mList.add(new Message(2, mess));
                        mAdapter.setData(mList);
                        if (mAdapter.getItemCount() > 1) {
                            mBinding.rvMessage.smoothScrollToPosition(mList.size() - 1);
                        }
                        playVoice(DataUtils.getConfigNumberStringFromResponse(mess));
                    } else {
                        LinkedTreeMap<String, String> mapNewTicketRequest = new LinkedTreeMap<String, String>();
                        mapNewTicketRequest.put("type", "order");
                        mapNewTicketRequest.put("number", "unknown");
                        mapNewTicketRequest.put("status", "ongoing");
                        context.put("ticket_request", mapNewTicketRequest);
                        sendMessageWithoutReceivingResponse("");
                        mList.add(new Message(2, "Sie haben keine Berechtigung zum Zugriff auf die Bestellung"));
                        mAdapter.setData(mList);
                        if (mAdapter.getItemCount() > 1) {
                            mBinding.rvMessage.smoothScrollToPosition(mList.size() - 1);
                        }
                        playVoice("Sie haben keine Berechtigung zum Zugriff auf die Bestellung");
                    }
                }

                @Override
                public void onSuccess(Context context, Employee employee) {

                }

                @Override
                public void onSuccess(Incident incident) {

                }

                @Override
                public void onSuccess(Service service) {

                }

                @Override
                public void onError() {
                    Log.e(LOG_TAG, "Error");
                    LinkedTreeMap<String, String> mapNewTicketRequest = new LinkedTreeMap<String, String>();
                    mapNewTicketRequest.put("type", "order");
                    mapNewTicketRequest.put("number", "unknown");
                    mapNewTicketRequest.put("status", "ongoing");
                    context.put("ticket_request", mapNewTicketRequest);
                    sendMessageWithoutReceivingResponse("");
                    mList.add(new Message(2, "Ihre Bestellungsnummer wurde nicht gefunden"));
                    mAdapter.setData(mList);
                    if (mAdapter.getItemCount() > 1) {
                        mBinding.rvMessage.smoothScrollToPosition(mList.size() - 1);
                    }
                    playVoice("Ihre Bestellungsnummer wurde nicht gefunden");
                }

                @Override
                public void onFailed() {
                    Log.e(LOG_TAG, "Failed");
                }
            });
            return null;
        }

    }

    private class IncidentTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            NetworkUtils.getIncidentById(getApplicationContext(), params[0], new NetworkCallback() {
                @Override
                public void onSuccess(Order order) {

                }

                @Override
                public void onSuccess(Context context, Employee employee) {

                }

                @Override
                public void onSuccess(Incident incident) {
                    Log.e(LOG_TAG, "Order: " + incident);
                    String userId = String.valueOf(incident.getUserId());
                    if (userId.equalsIgnoreCase(PreferencesUtils.getUserId(getApplicationContext()))){
                        LinkedTreeMap<String, String> mapNewTicketRequest = new LinkedTreeMap<String, String>();
                        mapNewTicketRequest.put("type", "order");
                        mapNewTicketRequest.put("number", String.valueOf(incident.getId()));
                        mapNewTicketRequest.put("status", incident.getStatus());
                        context.put("ticket_request", mapNewTicketRequest);
                        sendMessageWithoutReceivingResponse("");
                        mList.add(new Message(2, "Ihr Ticket-Status ist: " + incident.getStatus()));
                        mAdapter.setData(mList);
                        if (mAdapter.getItemCount() > 1) {
                            mBinding.rvMessage.smoothScrollToPosition(mList.size() - 1);
                        }
                        playVoice("Ihr Ticket-Status ist: " + incident.getStatus());
                    } else {
                        LinkedTreeMap<String, String> mapNewTicketRequest = new LinkedTreeMap<String, String>();
                        mapNewTicketRequest.put("type", "incident");
                        mapNewTicketRequest.put("number", "unknown");
                        mapNewTicketRequest.put("status", "ongoing");
                        context.put("ticket_request", mapNewTicketRequest);
                        sendMessageWithoutReceivingResponse("");
                        mList.add(new Message(2, "Sie haben keine Berechtigung zum Zugriff auf das Ticket"));
                        mAdapter.setData(mList);
                        if (mAdapter.getItemCount() > 1) {
                            mBinding.rvMessage.smoothScrollToPosition(mList.size() - 1);
                        }
                        playVoice("Sie haben keine Berechtigung zum Zugriff auf das Ticket");
                    }
                }

                @Override
                public void onSuccess(Service service) {

                }

                @Override
                public void onError() {
                    Log.e(LOG_TAG, "Error");
                    LinkedTreeMap<String, String> mapNewTicketRequest = new LinkedTreeMap<String, String>();
                    mapNewTicketRequest.put("type", "incident");
                    mapNewTicketRequest.put("number", "unknown");
                    mapNewTicketRequest.put("status", "ongoing");
                    context.put("ticket_request", mapNewTicketRequest);
                    sendMessageWithoutReceivingResponse("");
                    mList.add(new Message(2, "Ihre Ticketnummer wurde nicht gefunden"));
                    mAdapter.setData(mList);
                    if (mAdapter.getItemCount() > 1) {
                        mBinding.rvMessage.smoothScrollToPosition(mList.size() - 1);
                    }
                    playVoice("Ihre Ticketnummer wurde nicht gefunden");
                }

                @Override
                public void onFailed() {

                }
            });
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_action: {
                if (checkInternetConnection()){
                    isDismiss = false;
                    isEmptyRecognized = true;
                    listeningVoice();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Internet connection is interrupted")
                            .setIcon(R.drawable.icon)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    builder.show();
                }
                break;
            }
            case R.id.btn_send: {
                String input = mBinding.edtInputMessage.getText().toString().trim();
                sendMessage(input);
                break;
            }
        }
    }

    @Override
    public void onMessageDialogDismissed() {

    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
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
                    Log.e(LOG_TAG, "OnSpeechRecognized");
                    isEmptyRecognized = false;
                    if (isFinal) {
                        Log.e(LOG_TAG, "OnSpeechRecognized, onFinal");
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
                                    if (!isDismiss){
                                        sendMessage(text);
                                        Log.e(LOG_TAG, "!Dismissed");
                                    }

                                    mVoiceRecorder.stop();
                                    mDialog.setTextRecognized("");
                                    mDialog.dismiss();
                                    Log.e(LOG_TAG, "is final: true - text recognized : " + text);
                                } else {
                                    mDialog.setTextRecognized(text);
                                    Log.e(LOG_TAG, "is final: false - text recognized : " + text);
                                }
                            }
                        });
                    }
                }
            };

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            if (mSpeechService != null) {
                Log.e(LOG_TAG, "onVoiceStart");
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

    private boolean checkInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected){
            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public void onItemClick(int id) {
        Log.e(LOG_TAG, "Position: " + id);
    }
}
