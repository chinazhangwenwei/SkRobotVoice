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
    String TTS_SP_KEY = "TTS_SP_KEY_TTS";


    /**
     * tts 初始化接口
     *
     * @param mContext
     */
    void init(Context mContext, InitResultListener initResultListener);

    /**
     * 合成语音接口
     *
     * @param msg
     */
    void speak(String msg);

    /**
     * 调整音量
     *
     * @param volume
     */
    void changeVolume(String volume);

    /**
     * 调整语速
     *
     * @param speed
     */
    void changeSpeed(String speed);

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
    void changeSpeaker(Context mContext,String speaker);

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
    void changePlatform(Context mContext, HashMap<String, Integer> params);

    //获取当前平台
    int getPlatform();

    //获取当前说话人
    String getCurrentSpeaker();

    //获取当前平台描述
    String getPlatDes();


    void onPause();


    void onStop();

    void onResume();

    void onDestroy();


    interface InitResultListener {
        void initSuccess();

        void initError(String message);

    }

    interface SpeakeResultListener {
        void speakSuccess();

        void speakError(int type, String message);
    }


}
