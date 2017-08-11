package com.interjoy.testasr;

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

import com.interjoy.skasr.impls.HciAsrImpl;
import com.interjoy.skasr.impls.IflytekAsrImpl;
import com.interjoy.skasr.interfaces.AsrProvider;
import com.interjoy.skasr.manager.AsrManager;

import java.util.HashMap;

import static com.interjoy.skasr.manager.AsrManager.KEY_ASR_PLAT_TYPE;

public class MainActivity extends AppCompatActivity {

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
    private static final String TAG = "MainActivity";

    private AsrManager asrManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sk_test);


        tvContent = (TextView) findViewById(R.id.tv_content);
        tvTTsContent = (TextView) findViewById(R.id.tv_tt_content);
        btSpeaker = (Button) findViewById(R.id.bt_tts_speaker);

        View tvChange = findViewById(R.id.tv_change);
        params = new HashMap<>();
        asrInitListener = new AsrProvider.AsrInitListener() {
            @Override
            public void initSuccess() {

                appId = AsrManager.getInstance(MainActivity.this).getAppId();
                Log.d(TAG, "initSuccess: " + appId);


            }

            @Override
            public void initError(int code, String message) {
                Log.d(TAG, "initError: " + message);
            }
        };
        asrResultListener = new AsrProvider.AsrResultListener() {
            @Override
            public void success(final String result) {
                if (TextUtils.isEmpty(result)) {
                    return;
                }
//                ttsManager.speak(result);
                stringBuilder.setLength(0);
//                appId = AsrManager.getInstance(MainActivity.this).getAppId();
                stringBuilder.append(appId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        int type = AsrManager.getInstance(MainActivity.this).getPlatform();
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
                Log.d(TAG, "error: ");
            }
        };
        asrManager = AsrManager.getInstance(MainActivity.this);


        asrManager.setAsrInitListener(asrInitListener);
        asrManager.setAsrListener(asrResultListener);
        asrManager.init(MainActivity.this);


        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = AsrManager.getInstance(MainActivity.this).getPlatform();

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
                                Toast.makeText(MainActivity.this, "灵云", Toast.LENGTH_SHORT).show();
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

                                Toast.makeText(MainActivity.this, "科大讯飞", Toast.LENGTH_SHORT).show();
                                params.put(IflytekAsrImpl.KEY_XUN_APP_ID, etAppid.getText().toString());
                                break;
                        }
                        AsrManager.getInstance(MainActivity.this).changeAsr(MainActivity.this, params);
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
        btSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asrManager.resetAsr();
            }
        });

    }

    @Override
    protected void onDestroy() {
        asrManager.destroy();
        super.onDestroy();
    }
}
