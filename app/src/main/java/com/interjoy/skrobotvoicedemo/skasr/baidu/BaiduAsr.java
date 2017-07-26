//package com.interjoy.skrobotvoicedemo.skasr.baidu;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.SystemClock;
//import android.speech.RecognitionListener;
//import android.speech.SpeechRecognizer;
//
//import com.baidu.speech.VoiceRecognitionService;
//import com.interjoy.skrobotvoicedemo.initializer.BaseInitializer;
//import com.interjoy.skrobotvoicedemo.skasr.AsrConstant;
//import com.interjoy.skrobotvoicedemo.skasr.BaseAsr;
//import com.interjoy.skrobotvoicedemo.util.Utils;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
///**
// * Title:
// * Description:
// * Company: 北京盛开互动科技有限公司
// * Tel: 010-62538800
// * Mail: support@interjoy.com.cn
// *
// * @author zhangwenwei
// * @date 2017/7/26
// */
//
//public class BaiduAsr extends BaseAsr implements RecognitionListener {
//    private final String TAG = BaiduAsr.class.getSimpleName();
//
//    private SpeechRecognizer mSpeechRecognizer;
//    private long speechEndTime = -1;
//    private static final int EVENT_ERROR = 11;
//    private Intent initIntent;
//
//    public BaiduAsr(BaseInitializer initializer) {
//        super(initializer);
//    }
//
//    protected void init() {
//        if (mInitializer == null || mInitializer.getString(BaiduConstant.EXTRA_APPID) == null ||
//                mInitializer.getString(BaiduConstant.EXTRA_KEY) == null ||
//                mInitializer.getString(BaiduConstant.EXTRA_SECRET) == null) {
//            //初始化失败
//            Utils.loge("INIT", TAG + " init failed !");
//            return;
//        }
//        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext, new ComponentName(mContext, VoiceRecognitionService.class));
//        mSpeechRecognizer.setRecognitionListener(this);
//        initIntent = new Intent();
//        setParams(initIntent);
//    }
//
//    public void setParams(Intent intent) {
//
//        //APPID
//        intent.putExtra(BaiduConstant.EXTRA_APPID, Integer.valueOf(mInitializer.getString(BaiduConstant.EXTRA_APPID)));
//
//        //KEY
//        intent.putExtra(BaiduConstant.EXTRA_KEY, mInitializer.getString(BaiduConstant.EXTRA_KEY));
//
//        //SECRET
//        intent.putExtra(BaiduConstant.EXTRA_SECRET, mInitializer.getString(BaiduConstant.EXTRA_SECRET));
//
//        //保存识别过程中产生的文件
//        if (mInitializer.getBoolean(BaiduConstant.EXTRA_OUTFILE, false)) {
//            intent.putExtra(BaiduConstant.EXTRA_OUTFILE, "sdcard/sk/baidu/outfile.pcm");
//        }
//
//        //采样率 16000或8000
//        if (mInitializer.contains(BaiduConstant.EXTRA_SAMPLE)) {
//            intent.putExtra(BaiduConstant.EXTRA_SAMPLE, mInitializer.getInteger(BaiduConstant.EXTRA_SAMPLE));
//        }
//
////        语种
////        cmn-Hans-CN	中文普通话
////        sichuan-Hans-CN	中文四川话（离线暂不支持）
////        yue-Hans-CN	粤语（离线暂不支持）
////        en-GB	英语（离线暂不支持）
//        if (mInitializer.contains(BaiduConstant.EXTRA_LANGUAGE)) {
//            intent.putExtra(BaiduConstant.EXTRA_LANGUAGE, mInitializer.getString(BaiduConstant.EXTRA_LANGUAGE));
//        }
//
////        语音活动检测
////        search	搜索模式，适合短句输入
////        input	输入模式，适合短信、微博内容等长句输入
//        if (mInitializer.contains(BaiduConstant.EXTRA_VAD)) {
//            intent.putExtra(BaiduConstant.EXTRA_VAD, mInitializer.getString(mInitializer.getString(BaiduConstant.EXTRA_VAD)));
//        }
//    }
//
//    @Override
//    public void start() {
//        super.start();
//    }
//
//    @Override
//    protected void startRecognize() {
//
//        mSpeechRecognizer.startListening(initIntent);
//    }
//
//    @Override
//    public void stop() {
//        super.stop();
//        mSpeechRecognizer.stopListening();
//    }
//
//    @Override
//    public void destory() {
//        Utils.logi("TTT", "1");
//        super.destory();
//        Utils.logi("TTT", "2");
//        mSpeechRecognizer.stopListening();
//        Utils.logi("TTT", "3");
//        mSpeechRecognizer.destroy();
//        Utils.logi("TTT", "4");
//    }
//
//    @Override
//    public void changeParam(String key, Object param) {
//        Utils.logi("EVENT", "CHANGE_PARAM[key：" + key + "，param：" + String.valueOf(param) + "]");
//        if (param instanceof String) {
//            initIntent.putExtra(key, String.valueOf(param));
//        } else if (param instanceof Boolean) {
//            initIntent.putExtra(key, (boolean) param);
//        } else if (param instanceof Integer) {
//            initIntent.putExtra(key, (int) param);
//        }
//    }
//
//    @Override
//    public void onReadyForSpeech(Bundle params) {
//        mEvent(AsrConstant.EVENT_RECORDING_START, "EVENT_RECORDING_START");
//    }
//
//    @Override
//    public void onBeginningOfSpeech() {
//        speechEndTime = System.currentTimeMillis();
//        mEvent(AsrConstant.EVENT_SPEECH_DETECTED, "EVENT_SPEECH_DETECTED");
//    }
//
//    @Override
//    public void onRmsChanged(float rmsdB) {
//        mEvent(AsrConstant.EVENT_VOLUMECHANGE, String.valueOf((int) rmsdB / 80));
//        updateTime();
//        Utils.logi("BUFFER", String.valueOf(rmsdB));
//    }
//
//    @Override
//    public void onBufferReceived(byte[] buffer) {
//        Utils.logi("BUFFER", String.valueOf(buffer.length));
//    }
//
//    @Override
//    public void onEndOfSpeech() {
//        mEvent(AsrConstant.EVENT_SPEECH_END, "EVENT_SPEECH_END");
//    }
//
//    @Override
//    public void onError(int error) {
//        boolean isDelay = false;
//        StringBuilder sb = new StringBuilder();
//        int e = AsrConstant.ERROR_NULL;
//        switch (error) {
//            case SpeechRecognizer.ERROR_AUDIO:
//                sb.append("音频问题");
//                e = AsrConstant.ERROR_AUDIO;
//                break;
//            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
//                sb.append("没有语音输入");
//                e = AsrConstant.ERROR_SPEECH_TIMEOUT;
//                break;
//            case SpeechRecognizer.ERROR_CLIENT:
//                sb.append("其它客户端错误");
//                e = AsrConstant.ERROR_CLIENT;
//                break;
//            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
//                sb.append("权限不足");
//                e = AsrConstant.ERROR_INSUFFICIENT_PERMISSIONS;
//                break;
//            case SpeechRecognizer.ERROR_NETWORK:
//                sb.append("网络问题");
//                e = AsrConstant.ERROR_NETWORK;
//                break;
//            case SpeechRecognizer.ERROR_NO_MATCH:
//                sb.append("没有匹配的识别结果");
//                e = AsrConstant.ERROR_NO_MATCH;
//                break;
//            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
//                sb.append("引擎忙");
//                e = AsrConstant.ERROR_RECOGNIZER_BUSY;
//                isDelay = true;
//                break;
//            case SpeechRecognizer.ERROR_SERVER:
//                sb.append("服务端错误");
//                e = AsrConstant.ERROR_SERVER;
//                break;
//            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
//                sb.append("连接超时");
//                e = AsrConstant.ERROR_NETWORK_TIMEOUT;
//                break;
//        }
//        sb.append(":" + error);
//        mError(e, sb.toString());
//        if (isDelay) {
//            SystemClock.sleep(100);
//        }
//        mSpeechRecognizer.cancel();
//        startRecognize();
//    }
//
//    @Override
//    public void onResults(Bundle results) {
//        long end2finish = System.currentTimeMillis() - speechEndTime;
//        speechEndTime = 0;
//        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//        mResult(AsrConstant.RESULT_NET, String.valueOf(end2finish) + "|" + removeBrackets(Arrays.toString(nbest.toArray(new String[nbest.size()]))));
//        mEvent(AsrConstant.EVENT_RECOGNIZE_END, "EVENT_RECOGNIZE_END");
//        startRecognize();
////        String json_res = results.getString("origin_result");
////            try {
////                print("origin_result=\n" + new JSONObject(json_res).toString(4));
////            } catch (Exception e) {
////                print("origin_result=[warning: bad json]\n" + json_res);
////            }
////            String strEnd2Finish = "";
////            if (end2finish < 60 * 1000) {
////                strEnd2Finish = "(waited " + end2finish + "ms)";
////            }
////            txtResult.setText(nbest.get(0) + strEnd2Finish);
//    }
//
//    @Override
//    public void onPartialResults(Bundle partialResults) {
////        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
////        if (nbest.size() > 0) {
////            long end2finish = System.currentTimeMillis() - speechEndTime;
////            mResult(AsrConstant.RESULT_PROCESS, String.valueOf(end2finish) + "|" + removeBrackets(Arrays.toString(nbest.toArray(new String[0]))));
////        }
//    }
//
//    @Override
//    public void onEvent(int eventType, Bundle params) {
//        switch (eventType) {
//            case EVENT_ERROR:
//                String reason = params.get("reason") + "";
//                mError(AsrConstant.EVENT_ERROR, reason);
//                break;
//            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
//                int type = params.getInt("engine_type");
//                mError(AsrConstant.EVENT_ENGINE_SWITCH, "引擎切换至" + (type == 0 ? "在线" : "离线"));
//                break;
//        }
//    }
//
//    private String removeBrackets(String msg) {
//        if (msg.length() > 1) {
//            msg = msg.substring(1, msg.length() - 1);
//        }
//        return msg;
//    }
//}
