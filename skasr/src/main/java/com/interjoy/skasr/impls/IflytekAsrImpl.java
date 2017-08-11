package com.interjoy.skasr.impls;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.interjoy.skasr.interfaces.AsrProvider;
import com.interjoy.skasr.manager.AsrManager;
import com.interjoy.util.JsonParser;
import com.interjoy.util.LogUtil;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.interjoy.skasr.manager.AsrManager.KEY_XUN_FEI_TYPE;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/8/3
 * <p>
 * 科大讯飞Asr模块
 */
public class IflytekAsrImpl implements AsrProvider {
    private SpeechRecognizer mIat;

    private static final String KEY_VAD_BOS = "4000";
    private static final String KEY_VAD_EOS = "1000";
    private static final String KEY_ASR_PPT = "1";
    private static final String KEY_AUDIO_FORMAT = "pcm";
    private static final String KEY_AUDIO_SOURCE = "-1";

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private RecognizerListener recognizerListener;
    private AsrResultListener asrListener;
    private AsrInitListener asrInitListener;

    private static final String TAG = "IflytekAsrImpl";

    public static final String KEY_XUN_APP_ID = "APP_FLY_ID";

        public String appId = "5924f7f4";
//    public String appId = "598912bd";
    private String tempAppid = null;
    private Lock lock = new ReentrantLock();

    @Override
    public void init(final Context mContext) {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences
                (AsrManager.KEY_ASR_SHARE_PRE, Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(tempAppid)) {
            appId = sharedPreferences.getString(KEY_XUN_APP_ID, appId);
        }
        SpeechUtility.createUtility(mContext, "appid=" + appId);

        mIat = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                LogUtil.d(TAG, "onInit:SpeechRecognizer init() code =  " + i);
                if (i != ErrorCode.SUCCESS) {
                    LogUtil.d(TAG, "onInit: " + "初始化失败，错误码：" + i);
                    if (asrInitListener != null) {
                        asrInitListener.initError(i, "失败");
                    }
                } else if (i == ErrorCode.SUCCESS) {
                    LogUtil.d(TAG, "onInit: initSuccess");
                    if (asrInitListener != null) {
                        asrInitListener.initSuccess();
                    }
                    if (!TextUtils.isEmpty(tempAppid)) {
                        sharedPreferences.edit().putString(IflytekAsrImpl.KEY_XUN_APP_ID,
                                tempAppid).apply();
                        tempAppid = null;
                    }
                    sharedPreferences.edit().putInt(AsrManager.KEY_ASR_PLAT_TYPE,
                            AsrManager.KEY_XUN_FEI_TYPE).apply();
                    setParam();
                }
            }
        });
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
                LogUtil.d(TAG, "onResult: " + recognizerResult.getResultString());
                String result = JsonParser.parseIatResult(recognizerResult.getResultString());
                if (!result.isEmpty()) {
                    if (asrListener != null) {
                        asrListener.success(result);
                    }
                }
                stop();
                start();
            }

            @Override
            public void onError(SpeechError speechError) {
                if (asrListener != null) {
                    asrListener.error("讯飞识别错误码：" + speechError.getErrorDescription());
                }
                LogUtil.d(TAG, "讯飞识别错误码：" + speechError.getErrorDescription());
                stop();
                start();

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        };
        start();
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

        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS,
                KEY_VAD_BOS);

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS,
                KEY_VAD_EOS);
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,
                KEY_ASR_PPT);

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, KEY_AUDIO_FORMAT);
        //设置后数据来源为外部
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, KEY_AUDIO_SOURCE);

    }

    @Override
    public void changeAsr(Context mContext,
                          Map<String, String> params) {
        tempAppid = params.get(KEY_XUN_APP_ID);
        appId = tempAppid;
        //        mContext.getSharedPreferences()
        init(mContext);

    }

    @Override
    public int getPlatform() {
        return KEY_XUN_FEI_TYPE;
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public void setAsrInitListener(AsrInitListener asrInitListener) {
        this.asrInitListener = asrInitListener;
    }

    @Override
    public void setAsrListener(AsrResultListener asrListener) {
        this.asrListener = asrListener;
    }


    @Override
    public void start() {
        lock.lock();
        try {
            if (mIat == null) {
                return;
            }
            mIat.startListening(recognizerListener);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setPcmData(byte[] data, int size) {
        lock.lock();
        try {
            if (mIat == null) {
                return;
            }
            mIat.writeAudio(data, 0, size);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            if (mIat == null) {
                return;
            }
            mIat.stopListening();
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void destroy() {
        lock.lock();
        try {
            if (mIat.isListening()) {
                mIat.stopListening();
            }
            SpeechUtility.getUtility().destroy();
            mIat.destroy();
            mIat = null;
        } finally {
            lock.unlock();
        }
    }
}
