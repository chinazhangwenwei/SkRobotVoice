package com.interjoy.sktts.impls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.interjoy.sktts.interfaces.TtsProvider;
import com.interjoy.sktts.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.interjoy.sktts.manager.TtsManager.TTS_XUN_FEI_TYPE;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/8/9
 */
public class IflytekTtsImpl implements TtsProvider {
    public static String TTS_SP_IF_SPEAKER_KEY = "TTS_SP_SPEAKER_IF";//说话人的sp key值
    private static final String PLAT_DES = "科大讯飞";//平台名称
    private String speed = "50";
    private String volume = "50";
    private String pitch = "50";
    private SpeechSynthesizer mTts;    // 语音合成对象
    private String speaker = "xiaoyan";   // 默认发音人
    private static final String TAG = "IflytekTtsImpl";
    public String appId = "598912bd";
    private SpeakerResultListener speakResult;
    private InitResultListener initResultListener;

    @Override
    public void init(final Context mContext) {
        SpeechUtility.createUtility(mContext, "appid=" + appId);
        mTts = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                LogUtil.d(TAG, "InitListener init() code = " + i);
                if (i != ErrorCode.SUCCESS) {
                    if (initResultListener != null) {
                        initResultListener.initError(PLAT_DES + "初始化失败,错误码：" + i);
                    }
                } else {
                    // 初始化成功，之后可以调用startSpeaking方法
                    // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调
                    // 用startSpeaking进行合成，
                    // 正确的做法是将onCreate中的startSpeaking调用移至这里
                    if (initResultListener != null) {
                        initResultListener.initSuccess();
                        setParam(mContext);
                    }
                }
            }
        });

    }

    /**
     * 参数设置
     *
     * @param
     * @return
     */
    private void setParam(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TTS_SP_KEY,
                Activity.MODE_PRIVATE);
        sharedPreferences.getString(TTS_SP_IF_SPEAKER_KEY, speaker);
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, speaker);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, speed);
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, pitch);
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, volume);

//        //设置播放器音频流类型
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
//        // 设置播放合成音频打断音乐播放，默认为true
//        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    @Override
    public void speak(String msg, String speed, String volume) {
//
        if (mTts == null) {
            return;
        }
        String tempSpeed;
        String tempVolume;
        try {
            tempSpeed = Integer.parseInt(speed) * 10 + 9 + "";
            tempVolume = Integer.parseInt(volume) * 10 + 9 + "";
        } catch (NumberFormatException e) {
            tempSpeed = this.speed;
            tempVolume = this.volume;
            e.printStackTrace();
        }
        Log.d(TAG, "speak: " + tempSpeed + "tempVolume" + tempVolume);
        mTts.setParameter(SpeechConstant.SPEED, tempSpeed);
        mTts.setParameter(SpeechConstant.VOLUME, tempVolume);
        mTts.startSpeaking(msg, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                if (speakResult != null) {
                    speakResult.speakStart();
                }

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                if (speakResult != null) {
                    if (speechError == null) {
                        speakResult.speakSuccess();
                    } else {
                        speakResult.speakError(speechError.getErrorCode(),
                                speechError.getErrorDescription());
                    }
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });

    }

    @Override
    public boolean isSpeaking() {
        return mTts.isSpeaking();
    }

    @Override
    public String getTtsInfo() {

        String info = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appId", appId);
            info = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            info = "";
        }
        return info;
    }

    @Override
    public void setSpeakerResult(SpeakerResultListener speakerResult) {
        this.speakResult = speakerResult;
    }

    @Override
    public void setInitResult(InitResultListener initResultListener) {
        this.initResultListener = initResultListener;
    }

    @Override
    public void changePitch(String pitch) {
        mTts.setParameter(SpeechConstant.PITCH, pitch);
    }

    @Override
    public boolean changeSpeaker(Context mContext, String speaker) {
        this.speaker = speaker;
        if (mTts == null) {
            return false;
        }
        mTts.setParameter(SpeechConstant.VOICE_NAME, speaker);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TTS_SP_KEY,
                Activity.MODE_PRIVATE);
        sharedPreferences.edit().putString(TTS_SP_IF_SPEAKER_KEY, speaker).apply();
        return true;
    }

    @Override
    public void changeParams(HashMap<String, String> params) {

    }

    @Override
    public void changePlatform(Context mContext, HashMap<String, String> params) {
        init(mContext);
    }

    @Override
    public int getPlatform() {
        return TTS_XUN_FEI_TYPE;
    }

    @Override
    public String getCurrentSpeaker() {
        return speaker;
    }

    @Override
    public String getPlatDes() {
        return PLAT_DES;
    }

    @Override
    public void onPause() {
        if (mTts == null) {
            return;
        }
        mTts.pauseSpeaking();
    }

    @Override
    public void onStop() {
        if (mTts == null) {
            return;
        }
        mTts.stopSpeaking();
    }

    @Override
    public void onResume() {
        if (mTts == null) {
            return;
        }
        mTts.resumeSpeaking();
    }

    @Override
    public void onDestroy() {
        if (mTts == null) {
            return;
        }
        mTts.destroy();
//        SpeechUtility.getUtility().destroy();
        mTts = null;
    }
}
