package com.interjoy.skrobotvoicedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.interjoy.skrobotvoicedemo.record.AudioFileFunc;
import com.interjoy.skrobotvoicedemo.record.AudioRecordFunc;
import com.interjoy.skrobotvoicedemo.record.ErrorCode;
import com.interjoy.skrobotvoicedemo.skasr.iflytek.IflytekAsr;
import com.interjoy.skrobotvoicedemo.sktts.baidu.BaiduTts;
import com.interjoy.skrobotvoicedemo.util.JsonParser;
import com.interjoy.skrobotvoicedemo.util.LogUtil;

import static com.interjoy.skrobotvoicedemo.receiver.NetWorkReceiver.NETWORKSTATE;
import static com.interjoy.skrobotvoicedemo.receiver.NetWorkReceiver.NETWORK_KEY;

public class TestRecordActivity extends AppCompatActivity {

    private final static int FLAG_WAV = 0;
    private final static int FLAG_AMR = 1;
    private int mState = -1;    //-1:没再录制，0：录制wav，1：录制amr
    private Button btn_record_wav;
    //    private Button btn_record_amr;
    private Button btn_stop;
    private TextView txt;
    private UIHandler uiHandler;
    private UIThread uiThread;
    private TextView tvResult;
    private BroadcastReceiver netReceiver;

    BaiduTts baiduTts;
    IflytekAsr iflytekAsr;
    RecognizerListener recognizerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_record);
        findViewByIds();
        setListeners();
        init();
        initTts();
        initReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void initTts() {
        baiduTts = new BaiduTts(TestRecordActivity.this);
        new Thread() {
            @Override
            public void run() {
                baiduTts.initTts();
            }
        }.start();
    }

    private void initReceiver() {
        recognizerListener = new RecognizerListener() {
            @Override
            public void onVolumeChanged(int i, byte[] bytes) {

            }

            @Override
            public void onBeginOfSpeech() {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                Log.d(TAG, "onResult: "+recognizerResult.getResultString());
                String result =  JsonParser.parseIatResult(recognizerResult.getResultString());
                tvResult.setText(result);
                if (!result.isEmpty()) {
                    baiduTts.speekMessage(result);
                }
                iflytekAsr.stopListe();
                iflytekAsr.reconize(recognizerListener);
            }

            @Override
            public void onError(SpeechError speechError) {
                iflytekAsr.stopListe();
                iflytekAsr.reconize(recognizerListener);
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        };
        netReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int code = intent.getIntExtra(NETWORK_KEY, -1);
                switch (code) {
                    case 0:
                        baiduTts.speekMessage("网络连接异常");
                        iflytekAsr.stopListe();
                        break;
                    case 1:
                        baiduTts.speekMessage("欢迎回来");
                        iflytekAsr.stopListe();
                        iflytekAsr.reconize(recognizerListener);
//                        HciCloudFuncHelper.init(TestRecordActivity.this, HciCloudParams.KEY_HCI_CAP);
                        break;
                    case 2:
                        iflytekAsr.stopListe();
                        iflytekAsr.reconize(recognizerListener);
                        baiduTts.speekMessage("欢迎回来");
//                        HciCloudFuncHelper.init(TestRecordActivity.this, HciCloudParams.KEY_HCI_CAP);
                        break;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NETWORKSTATE);
        LocalBroadcastManager.getInstance(TestRecordActivity.this).
                registerReceiver(netReceiver, intentFilter);
    }


    private void findViewByIds() {
        tvResult = (TextView) findViewById(R.id.tv_result);
        btn_record_wav = (Button) this.findViewById(R.id.btn_record_wav);
//        btn_record_amr = (Button)this.findViewById(R.id.btn_record_amr);
        btn_stop = (Button) this.findViewById(R.id.btn_stop);
        txt = (TextView) this.findViewById(R.id.text);
    }

    private void setListeners() {
        btn_record_wav.setOnClickListener(btn_record_wav_clickListener);
//        btn_record_amr.setOnClickListener(btn_record_amr_clickListener);
        btn_stop.setOnClickListener(btn_stop_clickListener);
    }

    private static final String TAG = "TestRecordActivity";

    private void init() {
        uiHandler = new UIHandler();
        iflytekAsr = new IflytekAsr();
        iflytekAsr.initAsr(TestRecordActivity.this);

//        iflytekAsr.stopListe();
//        HciCloudFuncHelper.init(TestRecordActivity.this, HciCloudParams.KEY_HCI_CAP);
//        HciCloudFuncHelper.asrListner = new AsrListener() {
//            @Override
//            public void onError(int type, String errorMsg) {
//
//            }
//
//            @Override
//            public void onEvent(int type, String param) {
//
//            }
//
//            @Override
//            public void onResult(int type, final String msg) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tvResult.setText(msg);
//                        if (!msg.isEmpty()) {
//                            baiduTts.speekMessage(msg);
//                        }
//                    }
//                });
//
//            }
//        };
        AudioRecordFunc.getInstance().setRecordByteList(new AudioRecordFunc.RecordByteList() {
            @Override
            public void recordByteList(byte[] datas, int size) {
                LogUtil.d("正在录音");
                iflytekAsr.recongize(datas,size);
//                HciCloudFuncHelper.RealtimeRecog(HciCloudParams.KEY_HCI_CAP, HciCloudFuncHelper.recogConfig, "", datas, size);
            }
        });

    }

    private Button.OnClickListener btn_record_wav_clickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            record(FLAG_WAV);
        }
    };
    private Button.OnClickListener btn_record_amr_clickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            record(FLAG_AMR);
        }
    };
    private Button.OnClickListener btn_stop_clickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            stop();
        }
    };

    /**
     * 开始录音
     *
     * @param mFlag，0：录制wav格式，1：录音amr格式
     */
    private void record(int mFlag) {
        if (mState != -1) {
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_RECORDFAIL);
            b.putInt("msg", ErrorCode.E_STATE_RECODING);
            msg.setData(b);

            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            return;
        }
        int mResult = -1;
        switch (mFlag) {
            case FLAG_WAV:
                AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
                mResult = mRecord_1.startRecordAndFile();
                iflytekAsr.reconize(recognizerListener);
//                iflytekAsr.reconize(new RecognizerListener() {
//                    @Override
//                    public void onVolumeChanged(int i, byte[] bytes) {
//                        Log.d(TAG, "onVolumeChanged: ");
//
//                    }
//
//                    @Override
//                    public void onBeginOfSpeech() {
//                        Log.d(TAG, "onBeginOfSpeech: ");
//                    }
//
//                    @Override
//                    public void onEndOfSpeech() {
//                        Log.d(TAG, "onEndOfSpeech: ");
//                    }
//
//                    @Override
//                    public void onResult(RecognizerResult recognizerResult, boolean b) {
////
//
//                    }
//
//                    @Override
//                    public void onError(SpeechError speechError) {
//                        Log.d(TAG, "onError: ");
//                    }
//
//                    @Override
//                    public void onEvent(int i, int i1, int i2, Bundle bundle) {
//                        Log.d(TAG, "onEvent: ");
//                    }
//                });
                break;
            case FLAG_AMR:
//                MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
//                mResult = mRecord_2.startRecordAndFile();
                break;
        }
        if (mResult == ErrorCode.SUCCESS) {
            uiThread = new UIThread();
            new Thread(uiThread).start();
            mState = mFlag;
        } else {
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_RECORDFAIL);
            b.putInt("msg", mResult);
            msg.setData(b);

            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
        }
    }
//    private void printResult(RecognizerResult results) {
//        String text = JsonParser.parseIatResult(results.getResultString());
//
//        String sn = null;
//        // 读取json结果中的sn字段
//        try {
//            JSONObject resultJson = new JSONObject(results.getResultString());
//            sn = resultJson.optString("sn");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        mIatResults.put(sn, text);
//
//        StringBuffer resultBuffer = new StringBuffer();
//        for (String key : mIatResults.keySet()) {
//            resultBuffer.append(mIatResults.get(key));
//        }
//
//        mResultText.setText(resultBuffer.toString());
//        mResultText.setSelection(mResultText.length());
//    }

    /**
     * 停止录音
     */
    private void stop() {
        if (mState != -1) {
            switch (mState) {
                case FLAG_WAV:
                    AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
                    mRecord_1.stopRecordAndFile();
                    break;
                case FLAG_AMR:
//                    MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
//                    mRecord_2.stopRecordAndFile();
                    break;
            }
            if (uiThread != null) {
                uiThread.stopThread();
            }
            if (uiHandler != null)
                uiHandler.removeCallbacks(uiThread);
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_STOP);
            b.putInt("msg", mState);
            msg.setData(b);
            uiHandler.sendMessageDelayed(msg, 1000); // 向Handler发送消息,更新UI
            mState = -1;
        }
    }

    private final static int CMD_RECORDING_TIME = 2000;
    private final static int CMD_RECORDFAIL = 2001;
    private final static int CMD_STOP = 2002;

    class UIHandler extends Handler {
        public UIHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("MyHandler", "handleMessage......");
            super.handleMessage(msg);
            Bundle b = msg.getData();
            int vCmd = b.getInt("cmd");
            switch (vCmd) {
                case CMD_RECORDING_TIME:
                    int vTime = b.getInt("msg");
                    TestRecordActivity.this.txt.setText("正在录音中，已录制：" + vTime + " s");
                    break;
                case CMD_RECORDFAIL:
                    int vErrorCode = b.getInt("msg");
                    String vMsg = ErrorCode.getErrorInfo(TestRecordActivity.this, vErrorCode);
                    TestRecordActivity.this.txt.setText("录音失败：" + vMsg);
                    break;
                case CMD_STOP:
                    int vFileType = b.getInt("msg");
                    switch (vFileType) {
                        case FLAG_WAV:
                            AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
                            long mSize = mRecord_1.getRecordFileSize();
                            TestRecordActivity.this.txt.setText("录音已停止.录音文件:" + AudioFileFunc.getWavFilePath() + "\n文件大小：" + mSize);
                            break;
                        case FLAG_AMR:
//                            MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
//                            mSize = mRecord_2.getRecordFileSize();
//                            TestRecordActivity.this.txt.setText("录音已停止.录音文件:"+AudioFileFunc.getAMRFilePath()+"\n文件大小："+mSize);
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    ;

    class UIThread implements Runnable {
        int mTimeMill = 0;
        boolean vRun = true;

        public void stopThread() {
            vRun = false;
        }

        public void run() {
            while (vRun) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mTimeMill++;
                Log.d("thread", "mThread........" + mTimeMill);
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putInt("cmd", CMD_RECORDING_TIME);
                b.putInt("msg", mTimeMill);
                msg.setData(b);

                TestRecordActivity.this.uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            }

        }
    }

}