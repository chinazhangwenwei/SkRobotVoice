package com.interjoy.skrobotvoicedemo.skasr.iflytek;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.interjoy.skrobotvoicedemo.util.LogUtil;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/8/2
 */
public class IflytekAsr {
    private SpeechRecognizer mIat;
    private SharedPreferences mSharedPreferences;

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    public static final String PREFER_NAME = "com.iflytek.setting";

    public void initAsr(final Context mContext) {
        SpeechUtility.createUtility(mContext, "appid=5924f7f4");
        mIat = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
//                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                LogUtil.d("SpeechRecognizer init() code = " + i);
                if (i != ErrorCode.SUCCESS) {
//                    showTip("初始化失败，错误码：" + code);
                    LogUtil.d("初始化失败，错误码：" + i);
                }else if(i==ErrorCode.SUCCESS){
                    mSharedPreferences = mContext.getSharedPreferences(PREFER_NAME,
                            Activity.MODE_PRIVATE);
                    // 设置参数
                    setParam();
                    // 设置音频来源为外部文件
                    mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
                }
            }
        });


    }

    /**
     * 参数设置
     * *      * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    public void reconize(RecognizerListener listener) {
        int ret = mIat.startListening(listener);
    }


    public void recongize(byte datas[], int size) {
        mIat.writeAudio(datas, 0, size);
    }

    public void stopListe() {
        mIat.stopListening();
    }

    public void canceRecong() {
        mIat.cancel();
    }


}
