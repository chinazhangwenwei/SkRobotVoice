package com.interjoy.skrobotvoicedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.interjoy.skrobotvoicedemo.skasr.baidu.BaiDuAsrs;
import com.interjoy.skrobotvoicedemo.skasr.result.ResultsListener;
import com.interjoy.skrobotvoicedemo.util.LogUtil;

public class MainActivity extends AppCompatActivity implements ResultsListener {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAsr();
        initTts();
    }


    public void initView() {
        tvResult = (TextView) findViewById(R.id.tv_result);
    }

    private void initAsr() {
        BaiDuAsrs baiduAsrs = new BaiDuAsrs(MainActivity.this);
        baiduAsrs.setResultsListener(this);

    }

    private void initTts() {

    }

    @Override
    public void onResult(String result) {
        LogUtil.d(result);
        tvResult.setText(result);

    }

    @Override
    public void onError(String error) {
        LogUtil.d(error);
    }
}
