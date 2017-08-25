package com.interjoy.sktts.interfaces;

import android.content.Context;

import java.util.HashMap;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/8/7
 */
public interface TtsProvider {
    //保存SharedPreferences key
    String TTS_PLATFORM = "TTS_PLATFORM_TTS_MANAGER";
    String TTS_TEMP_PLATFORM = "TTS_TEMP_PLATFORM_TTS_MANAGER";
    String TTS_SP_KEY = "TTS_SP_KEY_TTS";


    /**
     * @param speakerResult 设置说话监听
     */
    void setSpeakerResult(SpeakerResultListener speakerResult);

    /**
     * 设置初始化监听
     *
     * @param initResultListener
     */

    void setInitResult(InitResultListener initResultListener);

    /**
     * tts 初始化接口
     *
     * @param mContext
     */
    void init(Context mContext);

    /**
     * 合成语音接口
     *
     * @param msg
     */
    void speak(String msg, String speed, String volume);


    /**
     * 调整语调
     *
     * @param pitch
     */
    void changePitch(String pitch);

    /**
     * 调整发音人
     *
     * @param speaker
     */
    boolean changeSpeaker(Context mContext, String speaker);

    /**
     * 切换参数（包括音量，速度，语调，发音人，
     * （key_volume,key_speed,key_pitch,key_speaker）)
     *
     * @param params
     */
    void changeParams(HashMap<String, String> params);


    /**
     * 切换TTS平台
     *
     * @param params
     */
    void changePlatform(Context mContext, HashMap<String, String> params);

    /**
     * 是否正在说话
     *
     * @return
     */
    boolean isSpeaking();

    //获取当前平台
    int getPlatform();

    //获取当前说话人
    String getCurrentSpeaker();

    //获取当前平台描述
    String getPlatDes();

    //获取当前TTS信息
    String getTtsInfo();


    void onPause();


    void onStop();

    void onResume();

    void onDestroy();


    interface InitResultListener {
        void initSuccess();

        void initError(String message);

    }

    interface SpeakerResultListener {
        void speakSuccess();

        void speakStart();

        void speakError(int type, String message);
    }


}
