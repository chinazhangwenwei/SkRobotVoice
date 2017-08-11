package com.interjoy.skrobotvoicedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;


import com.interjoy.skrobotvoicedemo.skasr.result.ResultsListener;
import com.interjoy.skrobotvoicedemo.sktts.baidu.BaiduTts;
import com.interjoy.skrobotvoicedemo.util.LogUtil;

import static com.interjoy.skrobotvoicedemo.receiver.NetWorkReceiver.NETWORKSTATE;
import static com.interjoy.skrobotvoicedemo.receiver.NetWorkReceiver.NETWORK_KEY;

public class MainActivity extends AppCompatActivity implements ResultsListener {

    private TextView tvResult;
    private BroadcastReceiver netReceiver;
    BaiduTts baiduTts;
    private Button btBroad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAsr();
        initTts();
        initReceiver();
    }


    private void initReceiver() {
        netReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int code = intent.getIntExtra(NETWORK_KEY, -1);
                switch (code) {
                    case 0:
                        baiduTts.speekMessage("网络连接异常");
                        break;
                    case 1:
                        baiduTts.speekMessage("欢迎回来");
                        break;
                    case 2:
                        baiduTts.speekMessage("欢迎回来");
                        break;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NETWORKSTATE);
        LocalBroadcastManager.getInstance(MainActivity.this).
                registerReceiver(netReceiver, intentFilter);
    }


    public void initView() {
        tvResult = (TextView) findViewById(R.id.tv_result);
        btBroad = (Button) findViewById(R.id.btn_switch);

    }

    private void initAsr() {
//        BaiDuAsrs baiduAsrs = new BaiDuAsrs(MainActivity.this);
//        baiduAsrs.setResultsListener(this);

    }


    private void initTts() {
        baiduTts = new BaiduTts(MainActivity.this);
        new Thread() {
            @Override
            public void run() {
                baiduTts.initTts();
            }
        }.start();


    }

    @Override
    public void onResult(String result) {
        LogUtil.d(result);
        tvResult.setText(result);
        baiduTts.speekMessage(result);


    }

    @Override
    public void onError(String error) {
        LogUtil.d(error);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(netReceiver);
        super.onDestroy();
    }
}
