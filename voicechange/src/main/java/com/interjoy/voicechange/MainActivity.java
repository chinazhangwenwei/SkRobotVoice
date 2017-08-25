package com.interjoy.voicechange;

import android.content.DialogInterface;
import android.os.Bundle;
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

import com.alibaba.fastjson.JSON;
import com.interjoy.SKRobotContextEventInterface;
import com.interjoy.SKRobotSDK;
import com.interjoy.voicechange.bean.AsrInfo;
import com.interjoy.voicechange.bean.TtsInfo;

import java.io.File;
import java.util.HashMap;

import static com.interjoy.voicechange.bean.AsrInfo.KEY_ASR_PLAT_TYPE;
import static com.interjoy.voicechange.bean.AsrInfo.KEY_LING_YUN_TYPE;
import static com.interjoy.voicechange.bean.AsrInfo.KEY_XUN_FEI_TYPE;
import static com.interjoy.voicechange.bean.TtsInfo.KEY_HCI_COLUND_URL_TTS;
import static com.interjoy.voicechange.bean.TtsInfo.KEY_HCI_DEVELOP_TTS;
import static com.interjoy.voicechange.bean.TtsInfo.KEY_HCI_TTS;


public class MainActivity extends AppCompatActivity {
    private static final String TTS_PLATFORM = "TTS_PLATFORM_TTS_MANAGER";
    public static final int TTS_BAI_DU_TTS = 0x00000001;//百度
    public static final int TTS_XUN_FEI_TYPE = 0x00000010;//讯飞
    public static final int TTS_YUN_ZHI_SHENG_TYPE = 0x00000011;//云知声
    public static final int TTS_LING_YUN_TYPE = 0x00000100;// 灵云

    //百度
    public static String KEY_BAI_DU_API = "KEY_BAI_DU_API";
    public static String KEY_BAI_DU_ID = "KEY_BAI_DU_ID";
    public static String KEY_BAI_DU_SECRET = "KEY_BAI_DU_SECRET";


    private Button btTts;
    private Button btSpeaker;
    private TextView tvContent;
    private String[] speakerArray;
    private static final String TAG = "MainActivity";

    SKRobotSDK skRobotSDK;
    private Button btAsr;
    private TextView tvAsrContent;
    private TextView tvTtsSpeaker;
    private TextView tvAsrResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btTts = (Button) findViewById(R.id.bt_tts);
        btSpeaker = (Button) findViewById(R.id.bt_tts_speaker);
        tvContent = (TextView) findViewById(R.id.tv_tt_content);
        tvTtsSpeaker = (TextView) findViewById(R.id.tv_tts_speaker);

        btAsr = (Button) findViewById(R.id.bt_asr);
        tvAsrContent = (TextView) findViewById(R.id.tv_as_content);
        tvAsrResult = (TextView) findViewById(R.id.tv_as_result);
        String DIR = "/data/SKRobotPlatform/Resource/Media/";
        File file = new File(DIR);
        if (file.exists()) {
            for (File childFile : file.listFiles()) {
                Log.d(TAG, "onCreate: " + childFile.getAbsolutePath());
            }
        } else {
            Log.d(TAG, "不存在新建一个: " + file.mkdirs() + file.getAbsolutePath());
        }
        initListener();
        initSkRobot();

    }

    private void initSkRobot() {
        skRobotSDK = new SKRobotSDK();
        skRobotSDK.SKROBOT_RegisterEvent("播放设备", new SKRobotContextEventInterface() {
            @Override
            public void ContextEvent(byte[] bytes, int i, float v) {

            }
        });
        skRobotSDK.SKROBOT_RegisterEvent("TtsInfo", new SKRobotContextEventInterface() {
            @Override
            public void ContextEvent(byte[] bytes, int i, float v) {
                final String content = new String(bytes);
                if (content != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvContent.setText(content);
                        }
                    });
                }
            }
        });
        skRobotSDK.SKROBOT_RegisterEvent("AsrInfo", new SKRobotContextEventInterface() {
            @Override
            public void ContextEvent(byte[] bytes, int i, float v) {
                final String content = new String(bytes);
                if (content != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvAsrContent.setText(content);
                        }
                    });
                }
            }
        });
        skRobotSDK.SKROBOT_RegisterEvent("语音", new SKRobotContextEventInterface() {
            @Override
            public void ContextEvent(byte[] bytes, int i, float v) {
                final String content = new String(bytes);
                if (content != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvAsrResult.setText(content);
                        }
                    });
                }
            }
        });
        skRobotSDK.SKROBOT_RegisterEvent("SpeakContent", new SKRobotContextEventInterface() {
            @Override
            public void ContextEvent(byte[] bytes, int i, float v) {
                final String content = new String(bytes);
                if (content != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTtsSpeaker.setText(content);
                        }
                    });
                }
            }
        });


    }

    private TtsInfo ttsInfo;
    private AsrInfo asrInfo;

    private void initAsrListener() {

        btAsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> params = new HashMap<>();
                String content = skRobotSDK.SKROBOT_UseContextServer("语音", "GetAsrInfo", "你好",
                        SKRobotSDK.APP_AUTH);

                tvAsrContent.setText(content);
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                Log.d(TAG, "onClick: " + content);
                try {
                    asrInfo = JSON.parseObject(content, AsrInfo.class);
                } catch (com.alibaba.fastjson.JSONException e) {
                    e.printStackTrace();
                    asrInfo = null;
                }
                int type = -1;
                if (asrInfo == null) {
                    type = KEY_XUN_FEI_TYPE;
                } else {
                    type = asrInfo.getPlatForm();
                }
                int currentPlatform = 0;
                switch (type) {
                    case KEY_LING_YUN_TYPE:
                        currentPlatform = 1;
                        break;
                    case KEY_XUN_FEI_TYPE:
                        currentPlatform = 0;
                        break;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View views = View.inflate(MainActivity.this, R.layout.dialog_change_asr, null);

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
                                        KEY_XUN_FEI_TYPE + "");
                                break;
                            case 1:
                                views.findViewById(R.id.ll_develop_key).setVisibility(View.VISIBLE);
                                views.findViewById(R.id.ll_url).setVisibility(View.VISIBLE);
                                params.put(KEY_ASR_PLAT_TYPE,
                                        KEY_LING_YUN_TYPE + "");
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
                            case KEY_LING_YUN_TYPE:
                                Toast.makeText(MainActivity.this, "灵云", Toast.LENGTH_SHORT).show();
                                params.put(AsrInfo.KEY_HCI, etAppid.getText().toString());
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
                                params.put(AsrInfo.KEY_HCI_COLUND_URL, etUrl.getText().toString());
                                params.put(AsrInfo.KEY_HCI_DEVELOP, etDevelop.getText().toString());
                                break;
                            case KEY_XUN_FEI_TYPE:
                                Toast.makeText(MainActivity.this, "科大讯飞", Toast.LENGTH_SHORT).show();
                                params.put(AsrInfo.KEY_XUN_APP_ID, "5924f7f4");
                                break;
                        }
                        String content = skRobotSDK.SKROBOT_UseContextServer("语音",
                                "SetAsrPlat", JSON.toJSONString(params), SKRobotSDK.APP_AUTH);
                        Log.d(TAG, "onClick: " + content);
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

    }


    private void initListener() {
        initAsrListener();
        btTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = skRobotSDK.SKROBOT_UseContextServer("播放设备", "GetTTSInfo", "你好",
                        SKRobotSDK.APP_AUTH);
//                LogUtil.d(TAG, content);
                tvContent.setText(content);
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                try {
                    ttsInfo = JSON.parseObject(content, TtsInfo.class);
                } catch (com.alibaba.fastjson.JSONException e) {
                    e.printStackTrace();
                    ttsInfo = null;
                }
                int ttsPosition = 0;
                if (ttsInfo != null) {
                    ttsPosition = ttsInfo.getTtsPosition();
                }

                final HashMap<String, String> ttsParams = new HashMap<>();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).
                        setTitle("切换tts平台")
                        .setSingleChoiceItems(R.array.tts_platform, ttsPosition,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case 0:
                                                ttsParams.put(TTS_PLATFORM, TTS_XUN_FEI_TYPE + "");
                                                break;
                                            case 1:
                                                ttsParams.put(TTS_PLATFORM, TTS_LING_YUN_TYPE + "");
                                                ttsParams.put(KEY_HCI_TTS, "905d5423");
                                                ttsParams.put(KEY_HCI_DEVELOP_TTS, "4f6eb9f03584f3c5cf82798f7c5ea70f");
                                                ttsParams.put(KEY_HCI_COLUND_URL_TTS, "test.api.hcicloud.com:8888");
                                                break;
                                            case 2:
                                                ttsParams.put(TTS_PLATFORM, TTS_BAI_DU_TTS + "");
                                                ttsParams.put(KEY_BAI_DU_ID, "10007892");
                                                ttsParams.put(KEY_BAI_DU_API, "DDSTkGS6yIZL4GvXlbETquHj");
                                                ttsParams.put(KEY_BAI_DU_SECRET, "4526eee4fb680d973820ef8651fa7912");
//                                                public String apiKey = "DDSTkGS6yIZL4GvXlbETquHj";
//                                                public String appId = "10007892";
//                                                public String secretKey = "4526eee4fb680d973820ef8651fa7912";
                                                break;
                                            case 3:
                                                ttsParams.put(TTS_PLATFORM, TTS_YUN_ZHI_SHENG_TYPE + "");
                                                break;
                                        }

                                        skRobotSDK.SKROBOT_UseContextServer("播放设备", "SetTtsPlat",
                                                JSON.toJSONString(ttsParams),
                                                SKRobotSDK.APP_AUTH);
//                                        setTTsInfo();
                                        Log.d(TAG, "onClick:TTS " + JSON.toJSONString(ttsParams));

                                        dialog.dismiss();

                                    }
                                });
                builder.create().show();

            }
        });
        btSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String contents = skRobotSDK.SKROBOT_UseContextServer("播放设备", "GetTTSInfo", "你好",
                        SKRobotSDK.APP_AUTH);
//                LogUtil.d(TAG, content);
                tvContent.setText(contents);
                if (TextUtils.isEmpty(contents)) {
                    return;
                }
                try {
                    ttsInfo = JSON.parseObject(contents, TtsInfo.class);
                } catch (com.alibaba.fastjson.JSONException e) {
                    e.printStackTrace();
                    ttsInfo = null;
                }
                int tempType = TTS_LING_YUN_TYPE;
                int speakPosition = 0;
                if (ttsInfo != null) {
                    tempType = ttsInfo.getPlatForm();
                    speakPosition = ttsInfo.getSpeakerPosition();
                }
                final int type = tempType;
                int array = R.array.baidu_tts;
                switch (type) {
                    case TTS_BAI_DU_TTS:
                        array = R.array.baidu_tts;
                        break;
                    case TTS_LING_YUN_TYPE:
                        array = R.array.ling_yun_tts_des;
                        speakerArray = getResources().getStringArray(R.array.ling_yun_tts);
                        break;
                    case TTS_XUN_FEI_TYPE:
                        array = R.array.xun_fei_tts_des;
                        speakerArray = getResources().getStringArray(R.array.xun_fei_tts);
                        break;

                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).
                        setTitle("切换发音人")
                        .setSingleChoiceItems(array, speakPosition,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String speaker = "";
                                        if (type == TTS_BAI_DU_TTS) {
                                            speaker = which + "";
                                        } else {
                                            speaker = speakerArray[which];
                                        }
                                        String content = skRobotSDK.SKROBOT_UseContextServer("播放设备",
                                                "SetSpeaker", speaker,
                                                SKRobotSDK.APP_AUTH);

                                        Log.d(TAG, "onClick: " + content);
                                        dialog.dismiss();
                                    }
                                });
                builder.create().show();


            }
        });
    }

    String platForm;

    private void setTTsInfo() {
        String content = skRobotSDK.SKROBOT_UseContextServer("播放设备", "GetTTSInfo", "",
                SKRobotSDK.APP_AUTH);
//                LogUtil.d(TAG, content);
        tvContent.setText(content);
        if (TextUtils.isEmpty(content)) {
            return;
        }
        ttsInfo = JSON.parseObject(content, TtsInfo.class);

        platForm = ttsInfo.getPlatDes();
        final StringBuilder sb = new StringBuilder(platForm);
        sb.append(":");
        switch (ttsInfo.getPlatForm()) {
            case TTS_BAI_DU_TTS:
                sb.append(dealBaiduDes(ttsInfo.getSpeaker()));
                break;
            default:
                sb.append(ttsInfo.getSpeaker());
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvContent.setText(sb.toString());
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


}
