package com.interjoy.skrobotvoicedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.interjoy.skasr.impls.HciAsrImpl;
import com.interjoy.skasr.impls.IflytekAsrImpl;
import com.interjoy.skasr.interfaces.AsrProvider;
import com.interjoy.skasr.manager.AsrManager;
import com.interjoy.sktts.interfaces.TtsProvider;
import com.interjoy.sktts.manager.TtsManager;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;

import java.util.HashMap;

import static com.interjoy.skrobotvoicedemo.receiver.NetWorkReceiver.NETWORKSTATE;
import static com.interjoy.skrobotvoicedemo.receiver.NetWorkReceiver.NETWORK_KEY;
import static com.interjoy.skasr.manager.AsrManager.KEY_ASR_PLAT_TYPE;

public class SkTestActivity extends AppCompatActivity {

    private TextView tvContent;
    private HashMap<String, String> params;
    //    BaiduTtsImpl baiduTts;
    private AsrProvider.AsrResultListener asrResultListener;
    StringBuilder stringBuilder = new StringBuilder();
    private Button btTts;

    private TextView tvTTsContent;
    private Button btSpeaker;
    private String[] speakerArray;
    private AsrProvider.AsrInitListener asrInitListener;

    private String appId;
    private static final String TAG = "SkTestActivitys";
    private TtsManager ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HciCloudSys.hciRelease();
        initReciver();
        setContentView(R.layout.activity_sk_test);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvTTsContent = (TextView) findViewById(R.id.tv_tt_content);

        View tvChange = findViewById(R.id.tv_change);
        params = new HashMap<>();
        asrInitListener = new AsrProvider.AsrInitListener() {
            @Override
            public void initSuccess() {
                Log.d(TAG, "initSuccess: ");
                appId = AsrManager.getInstance(SkTestActivity.this).getAppId();
                Log.d(TAG, "initSuccess: " + appId);


            }

            @Override
            public void initError(int code, String message) {

            }
        };
        asrResultListener = new AsrProvider.AsrResultListener() {
            @Override
            public void success(final String result) {
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                ttsManager.speak(result);
                stringBuilder.setLength(0);
//                appId = AsrManager.getInstance(SkTestActivity.this).getAppId();
                stringBuilder.append(appId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        int type = AsrManager.getInstance(SkTestActivity.this).getPlatform();
                        switch (type) {
                            case AsrManager.KEY_XUN_FEI_TYPE:
                                stringBuilder.append("科大讯飞：");
                                break;
                            case AsrManager.KEY_LING_YUN_TYPE:
                                stringBuilder.append("捷通华声：");
                                break;
                        }
                        stringBuilder.append(result);
                        tvContent.setText(stringBuilder.toString());
                    }
                });
            }

            @Override
            public void error(String error) {
                com.interjoy.skrobotvoicedemo.util.LogUtil.d(error);
            }
        };
        AsrManager.getInstance(SkTestActivity.this).setAsrInitListener(asrInitListener);
        AsrManager.getInstance(SkTestActivity.this).setAsrListener(asrResultListener);
        AsrManager.getInstance(SkTestActivity.this).init(SkTestActivity.this);
        initTts();

//        ttsManager.speak(" 你好");

        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = AsrManager.getInstance(SkTestActivity.this).getPlatform();

                int currentPlatform = 0;

                switch (type) {
                    case AsrManager.KEY_LING_YUN_TYPE:
                        currentPlatform = 1;
//                        params.put(AsrManager.KEY_ASR_PLAT_TYPE,
//                                AsrManager.KEY_XUN_FEI_TYPE + "");
                        break;
                    case AsrManager.KEY_XUN_FEI_TYPE:
                        currentPlatform = 0;
//                        params.put(AsrManager.KEY_ASR_PLAT_TYPE,
//                                AsrManager.KEY_LING_YUN_TYPE + "");
                        break;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(SkTestActivity.this);

                final View views = View.inflate(SkTestActivity.this, R.layout.dialog_change_asr, null);

                AppCompatSpinner spinner = (AppCompatSpinner) views.findViewById(R.id.sp_platform);
                spinner.setSelection(currentPlatform);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        switch (position) {
                            case 0:
                                views.findViewById(R.id.ll_develop_key).setVisibility(View.GONE);
                                views.findViewById(R.id.ll_url).setVisibility(View.GONE);
                                params.put(KEY_ASR_PLAT_TYPE,
                                        AsrManager.KEY_XUN_FEI_TYPE + "");
                                break;
                            case 1:
                                views.findViewById(R.id.ll_develop_key).setVisibility(View.VISIBLE);
                                views.findViewById(R.id.ll_url).setVisibility(View.VISIBLE);
                                params.put(KEY_ASR_PLAT_TYPE,
                                        AsrManager.KEY_LING_YUN_TYPE + "");
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                builder.setView(views);
                final AlertDialog dialog = builder.create();
                dialog.show();
                View btConfirm = views.findViewById(R.id.bt_confirm);
                View btCancel = views.findViewById(R.id.bt_cancel);
                final EditText etAppid = (EditText) views.findViewById(R.id.et_appId);
                final EditText etDevelop = (EditText) views.findViewById(R.id.et_develop_key);
                final EditText etUrl = (EditText) views.findViewById(R.id.et_url);

                etAppid.setText("e05d542b");
                etDevelop.setText("b99be26181529d657a88af657f47a093");
                etUrl.setText("test.api.hcicloud.com:8888");
                btConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int type = Integer.parseInt(params.get(KEY_ASR_PLAT_TYPE));
                        String id = etAppid.getText().toString();
                        if (TextUtils.isEmpty(id)) {
                            etAppid.requestFocus();
                            return;
                        }
                        switch (type) {
                            case AsrManager.KEY_LING_YUN_TYPE:
                                Toast.makeText(SkTestActivity.this, "灵云", Toast.LENGTH_SHORT).show();
                                params.put(HciAsrImpl.KEY_HCI, etAppid.getText().toString());
                                String url = etUrl.getText().toString();
                                String develop = etDevelop.getText().toString();
                                if (TextUtils.isEmpty(url)) {
                                    etUrl.requestFocus();
                                    return;
                                }
                                if (TextUtils.isEmpty(develop)) {
                                    etDevelop.requestFocus();
                                    return;
                                }
                                params.put(HciAsrImpl.KEY_HCI_COLUND_URL, etUrl.getText().toString());
                                params.put(HciAsrImpl.KEY_HCI_DEVELOP, etDevelop.getText().toString());
                                break;
                            case AsrManager.KEY_XUN_FEI_TYPE:

                                Toast.makeText(SkTestActivity.this, "科大讯飞", Toast.LENGTH_SHORT).show();
                                params.put(IflytekAsrImpl.KEY_XUN_APP_ID, etAppid.getText().toString());
                                break;
                        }
                        AsrManager.getInstance(SkTestActivity.this).changeAsr(SkTestActivity.this, params);
                        dialog.dismiss();
                    }
                });
                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });


        btTts = (Button) findViewById(R.id.bt_tts);
        btSpeaker = (Button) findViewById(R.id.bt_tts_speaker);
        btTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final HashMap<String, Integer> params = new HashMap<>();

                AlertDialog.Builder builder = new AlertDialog.Builder(SkTestActivity.this).
                        setTitle("切换tts平台")
                        .setSingleChoiceItems(R.array.tts_platform, ttsManager.
                                        getTtsPosition(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                params.put(TtsProvider.TTS_PLATFORM, TtsManager.TTS_XUN_FEI_TYPE);
                                                break;
                                            case 1:
                                                params.put(TtsProvider.TTS_PLATFORM, TtsManager.TTS_LING_YUN_TYPE);
                                                break;
                                            case 2:
                                                params.put(TtsProvider.TTS_PLATFORM, TtsManager.TTS_BAI_DU_TTS);
                                                break;
                                            case 3:
                                                params.put(TtsProvider.TTS_PLATFORM, TtsManager.TTS_YUN_ZHI_SHENG_TYPE);
                                                break;
                                        }
                                        ttsManager.changePlatform(SkTestActivity.this, params);
                                        setTTsInfo();
                                        dialog.dismiss();

                                    }
                                });
                builder.create().show();
            }
        });
        btSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int type = ttsManager.getPlatform();
                int array = R.array.baidu_tts;
                switch (type) {
                    case TtsManager.TTS_BAI_DU_TTS:
                        array = R.array.baidu_tts;
                        break;
                    case TtsManager.TTS_LING_YUN_TYPE:
                        array = R.array.ling_yun_tts_des;
                        speakerArray = getResources().getStringArray(R.array.ling_yun_tts);
                        break;
                    case TtsManager.TTS_XUN_FEI_TYPE:
                        array = R.array.xun_fei_tts_des;
                        speakerArray = getResources().getStringArray(R.array.xun_fei_tts);
                        break;

                }
                AlertDialog.Builder builder = new AlertDialog.Builder(SkTestActivity.this).
                        setTitle("切换发音人")
                        .setSingleChoiceItems(array, ttsManager.
                                        getSpeakerPosition(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (type == TtsManager.TTS_BAI_DU_TTS) {
                                            ttsManager.changeSpeaker(SkTestActivity.this, which + "");
                                        } else {
                                            ttsManager.changeSpeaker(SkTestActivity.this, speakerArray[which]);
                                        }
                                        setTTsInfo();
                                        dialog.dismiss();
//                                        Toast.makeText(SkTestActivity.this, which + "", Toast.LENGTH_SHORT).show();
                                    }
                                });
                builder.create().show();
            }
        });
    }

    String platForm;

    private void initTts() {
        ttsManager = TtsManager.getInstance(SkTestActivity.this);
        ttsManager.init(SkTestActivity.this, null);
        setTTsInfo();

    }

    private void setTTsInfo() {
        platForm = ttsManager.getPlatDes();
        final StringBuilder sb = new StringBuilder(platForm);
        sb.append(":");
        switch (ttsManager.getPlatform()) {
            case TtsManager.TTS_BAI_DU_TTS:
                sb.append(dealBaiduDes(ttsManager.getCurrentSpeaker()));
                break;
            default:
                sb.append(ttsManager.getCurrentSpeaker());
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTTsContent.setText(sb.toString());
            }
        });

    }

    private String dealBaiduDes(String des) {
        String dess = "";
        switch (des) {
            case "0":
                dess = "百度普通女生";
                break;
            case "1":
                dess = "百度普通男生";
                break;
            case "2":
                dess = "百度特别男生";
                break;
            case "3":
                dess = "百度-情感男声-度逍遥";
                break;
            case "4":
                dess = "百度-情感儿童声-度丫丫";
                break;

        }
        return dess;
    }

    BroadcastReceiver netReceiver;

    private void initReciver() {
        netReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int code = intent.getIntExtra(NETWORK_KEY, -1);
                switch (code) {
                    case 0:
                        ttsManager.speak("网络连接异常");
                        AsrManager.getInstance(SkTestActivity.this).destroy();
                        break;
                    case 1:

                        if (AsrManager.getInstance(SkTestActivity.this).isDestroy()) {
                            ttsManager.speak("欢迎回来");
                            AsrManager.getInstance(SkTestActivity.this).setAsrInitListener(asrInitListener);
                            AsrManager.getInstance(SkTestActivity.this).setAsrListener(asrResultListener);
                            AsrManager.getInstance(SkTestActivity.this).init(SkTestActivity.this);

                        }
//                        HciCloudFuncHelper.init(TestRecordActivity.this, HciCloudParams.KEY_HCI_CAP);
                        break;
                    case 2:
                        if (AsrManager.getInstance(SkTestActivity.this).isDestroy()) {
                            ttsManager.speak("欢迎回来");
                            AsrManager.getInstance(SkTestActivity.this).setAsrInitListener(asrInitListener);
                            AsrManager.getInstance(SkTestActivity.this).setAsrListener(asrResultListener);
                            AsrManager.getInstance(SkTestActivity.this).init(SkTestActivity.this);

                        }
//                        ttsManager.speak("欢迎回来");
//                        HciCloudFuncHelper.init(TestRecordActivity.this, HciCloudParams.KEY_HCI_CAP);
                        break;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NETWORKSTATE);
        LocalBroadcastManager.getInstance(SkTestActivity.this).
                registerReceiver(netReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsManager.onDestroy();
        AsrManager.getInstance(SkTestActivity.this).destroy();
        LocalBroadcastManager.getInstance(SkTestActivity.this).unregisterReceiver(netReceiver);

    }
}
