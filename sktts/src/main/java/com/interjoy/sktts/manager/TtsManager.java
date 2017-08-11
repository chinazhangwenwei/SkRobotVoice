package com.interjoy.sktts.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.interjoy.sktts.impls.BaiduTtsImpl;
import com.interjoy.sktts.impls.HciTtsImpl;
import com.interjoy.sktts.impls.IflytekTtsImpl;
import com.interjoy.sktts.interfaces.TtsProvider;
import com.interjoy.sktts.util.LogUtil;

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
public class TtsManager implements TtsProvider {

    public static final int TTS_BAI_DU_TTS = 0x00000001;//百度
    public static final int TTS_XUN_FEI_TYPE = 0x00000010;//讯飞
    public static final int TTS_YUN_ZHI_SHENG_TYPE = 0x00000011;//云知声
    public static final int TTS_LING_YUN_TYPE = 0x00000100;// 灵云
    private static final String TAG = "TtsManager";//debug过滤


    public final String arrayBaidu[] = {"0", "1", "2", "3", "4"};
    public final String arrayLingYun[] = {"wangjing", "xiaokun", "haobo", "xixi", "xiaojie",
            "serena", "daniel", "tom", "samantha", "allison", "donna", "jill", "carol", "donna"};
    public final String arrayXunFei[] = {
            "xiaoyan", "xiaoyu", "catherine", "henry", "vimary", "vixy", "xiaoqi", "vixf",
            "xiaomei", "xiaolin", "xiaorong", "xiaoqian", "xiaokun", "xiaoqiang", "vixying",
            "xiaoxin", "nannan", "vils",
    };
    private TtsProvider ttsProvider;


    private volatile static TtsManager ttsManager;


    public static TtsManager getInstance(Context mContext) {
        if (ttsManager == null) {
            synchronized (TtsManager.class) {
                if (ttsManager == null) {
                    ttsManager = new TtsManager(mContext);
                }
            }
        }
        return ttsManager;
    }

    private TtsManager(Context mContext) {
        SharedPreferences sharedPreferences = mContext.
                getSharedPreferences(TTS_SP_KEY, Activity.MODE_PRIVATE);
        int plat = sharedPreferences.getInt(TTS_PLATFORM, TTS_LING_YUN_TYPE);
        initPlat(plat);
    }

    private void initPlat(int plat) {
        switch (plat) {
            case TTS_LING_YUN_TYPE:
                ttsProvider = new HciTtsImpl();
                break;
            case TTS_XUN_FEI_TYPE:
                ttsProvider = new IflytekTtsImpl();
                break;
            case TTS_BAI_DU_TTS:
                ttsProvider = new BaiduTtsImpl();
                break;
            case TTS_YUN_ZHI_SHENG_TYPE:
                break;
        }
    }

    @Override
    public void init(Context mContext, final InitResultListener initResultListener) {
        InitResultListener initResultListener1 = new InitResultListener() {
            @Override
            public void initSuccess() {
                if (initResultListener != null) {
                    initResultListener.initSuccess();
                }

            }

            @Override
            public void initError(String message) {
                if (initResultListener != null) {
                    initResultListener.initError(message);
                }
            }
        };
        ttsProvider.init(mContext, initResultListener1);
    }

    @Override
    public void speak(String msg) {
        if (ttsProvider == null) {
            return;
        }
        ttsProvider.speak(msg);

    }

    @Override
    public String getPlatDes() {
        return ttsProvider.getPlatDes();
    }

    @Override
    public void changeVolume(String volume) {
        ttsProvider.changeVolume(volume);
    }

    @Override
    public void changeSpeed(String speed) {

    }

    @Override
    public void changePitch(String pitch) {

    }

    @Override
    public void changeSpeaker(Context mContext, String speaker) {
        ttsProvider.changeSpeaker(mContext, speaker);
    }

    @Override
    public void changeParams(HashMap<String, String> params) {

    }

    @Override
    public void changePlatform(final Context mContext, HashMap<String, Integer> params) {

        final int plat = params.get(TTS_PLATFORM);
        if (plat == ttsProvider.getPlatform()) {
            return;
        }
        ttsProvider.onDestroy();
        initPlat(plat);
        InitResultListener initResultListener = new InitResultListener() {
            @Override
            public void initSuccess() {
                SharedPreferences sharedPreferences = mContext.
                        getSharedPreferences(TTS_SP_KEY, Activity.MODE_PRIVATE);
                sharedPreferences.edit().putInt(TTS_PLATFORM, plat).apply();

            }

            @Override
            public void initError(String message) {
                LogUtil.d(TAG, "initError: " + message);
                reset(mContext);
            }
        };
        ttsProvider.init(mContext, initResultListener);


    }


    public void reset(Context mContext) {
        SharedPreferences sharedPreferences = mContext.
                getSharedPreferences(TTS_SP_KEY, Activity.MODE_PRIVATE);
        int plat = sharedPreferences.getInt(TTS_PLATFORM, TTS_BAI_DU_TTS);
        ttsProvider.onDestroy();
        initPlat(plat);
        ttsProvider.init(mContext, null);
    }


    @Override
    public int getPlatform() {
        if (ttsProvider == null) {
            return -1;
        }
        return ttsProvider.getPlatform();

    }

    @Override
    public String getCurrentSpeaker() {
        return ttsProvider.getCurrentSpeaker();
    }

    public int getTtsPosition() {
        int position = 0;
        switch (getPlatform()) {
            case TTS_LING_YUN_TYPE:
                position = 1;
                break;
            case TTS_BAI_DU_TTS:
                position = 2;
                break;
            case TTS_XUN_FEI_TYPE:
                position = 0;
                break;
        }
        return position;
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public int getSpeakerPosition() {
        int position = 0;
        switch (getPlatform()) {
            case TTS_BAI_DU_TTS:
                position = findPosition(getCurrentSpeaker(), arrayBaidu);
                break;
            case TTS_LING_YUN_TYPE:
                position = findPosition(getCurrentSpeaker(), arrayLingYun);
                break;
            case TTS_XUN_FEI_TYPE:
                position = findPosition(getCurrentSpeaker(), arrayXunFei);
                break;
        }
        return position;
    }

    @Override
    public void onPause() {
        ttsProvider.onPause();
    }

    @Override
    public void onStop() {
        ttsProvider.onStop();
    }

    @Override
    public void onResume() {
        ttsProvider.onResume();
    }

    @Override
    public void onDestroy() {
        ttsProvider.onDestroy();
    }

    //找数组中某个内容的位置
    public int findPosition(String content, String array[]) {
        int position = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(content)) {
                position = i;
                break;
            }
        }
        return position;
    }
}
