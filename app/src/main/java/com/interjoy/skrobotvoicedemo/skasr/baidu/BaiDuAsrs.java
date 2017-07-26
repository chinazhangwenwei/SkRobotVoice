package com.interjoy.skrobotvoicedemo.skasr.baidu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import com.baidu.speech.VoiceRecognitionService;
import com.interjoy.skrobotvoicedemo.constant.BaiduParams;
import com.interjoy.skrobotvoicedemo.constant.BaseParams;
import com.interjoy.skrobotvoicedemo.skasr.result.ResultsListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/7/26
 */
public class BaiDuAsrs implements RecognitionListener {

    private ResultsListener resultsListener;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent initIntent;
    private Context context;


    public BaiDuAsrs(Context mContext) {
        context = mContext;
        initSpeech();
        initIntent = new Intent();
        setParams();
        mSpeechRecognizer.startListening(initIntent);

    }

    private void initSpeech() {
        if(mSpeechRecognizer!=null){
            mSpeechRecognizer.destroy();
        }
        mSpeechRecognizer = SpeechRecognizer.
                createSpeechRecognizer(context, new ComponentName(context, VoiceRecognitionService.class));
        mSpeechRecognizer.setRecognitionListener(this);
    }


    public void setResultsListener(ResultsListener resultsListener) {
        this.resultsListener = resultsListener;
    }

    public void setParams() {

        //APPID
        initIntent.putExtra(BaseParams.EXTRA_APPID, Integer.valueOf(BaiduParams.APP_ID));

        //KEY
        initIntent.putExtra(BaseParams.EXTRA_KEY, BaiduParams.API_KEY);

        //SECRET
        initIntent.putExtra(BaseParams.EXTRA_SECRET, BaiduParams.SECRET_KEY);

        //保存识别过程中产生的文件
//        if (mInitializer.getBoolean(BaiduConstant.EXTRA_OUTFILE, false)) {
//            intent.putExtra(BaseParams.EXTRA_OUTFILE, "sdcard/sk/baidu/outfile.pcm");
//        }

        //采样率 16000或8000

        initIntent.putExtra(BaseParams.EXTRA_SAMPLE, BaseParams.SAMPLE_16K);

        initIntent.putExtra(BaseParams.EXTRA_LANGUAGE, BaiduParams.LANGUAGE_PUTONG);
//        语种
//        cmn-Hans-CN	中文普通话
//        sichuan-Hans-CN	中文四川话（离线暂不支持）
//        yue-Hans-CN	粤语（离线暂不支持）
//        en-GB	英语（离线暂不支持）
//        if (mInitializer.contains(BaiduConstant.EXTRA_LANGUAGE)) {
//            intent.putExtra(BaiduConstant.EXTRA_LANGUAGE, mInitializer.getString(BaiduConstant.EXTRA_LANGUAGE));
//        }

//        语音活动检测
//        search	搜索模式，适合短句输入
//        input	输入模式，适合短信、微博内容等长句输入
//        if (mInitializer.contains(BaiduConstant.EXTRA_VAD)) {
//            intent.putExtra(BaiduConstant.EXTRA_VAD, mInitializer.getString(mInitializer.getString(BaiduConstant.EXTRA_VAD)));
//        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");

                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                initSpeech();

                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");

                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");

                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");

                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");

                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");

                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");

                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");

                break;
        }
        sb.append(":" + error);
        if (resultsListener != null) {
            resultsListener.onError(sb.toString());
        }
        if (error == 6) {
            initSpeech();
            mSpeechRecognizer.startListening(initIntent);

        } else {
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.startListening(initIntent);
        }

    }

    @Override
    public void onResults(Bundle results) {
        if (resultsListener != null) {
            String content;
            ArrayList<String> resultContent = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            content = removeBrackets(Arrays.toString(resultContent.toArray(new String[resultContent.size()])));
            resultsListener.onResult(content);
        }
        mSpeechRecognizer.cancel();
        mSpeechRecognizer.startListening(initIntent);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    private String removeBrackets(String msg) {
        if (msg.length() > 1) {
            msg = msg.substring(1, msg.length() - 1);
        }
        return msg;
    }
}
