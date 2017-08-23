package com.interjoy.sktts.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.interjoy.sktts.impls.BaiduTtsImpl;
import com.interjoy.sktts.impls.HciTtsImpl;
import com.interjoy.sktts.impls.IflytekTtsImpl;
import com.interjoy.sktts.impls.YunTtsImpl;
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
    private int ttsStatus = 0;//(0,没有初始化 1 初始化成功 2 初始化失败)


    public final String arrayBaidu[] = {"0", "1", "2", "3", "4"};
    public final String arrayLingYun[] = {"wangjing", "xiaokun", "haobo", "xixi", "xiaojie",
            "serena", "daniel", "tom", "samantha", "allison", "donna", "jill", "carol", "donna"};
    public final String arrayXunFei[] = {
            "xiaoyan", "xiaoyu", "catherine", "henry", "vimary", "vixy", "xiaoqi", "vixf",
            "xiaomei", "xiaolin", "xiaorong", "xiaoqian", "xiaokun", "xiaoqiang", "vixying",
            "xiaoxin", "nannan", "vils",
    };
    private TtsProvider ttsProvider;

    private SpeakerResultListener speakerResultListener;
    private SpeakerResultListener speakerResult;
    private InitResultListener initListener;
    private InitResultListener changeTtsInitListener;


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
        int plat = sharedPreferences.getInt(TTS_PLATFORM, TTS_BAI_DU_TTS);
        initPlat(plat);
        speakerResultListener = new SpeakerResultListener() {
            @Override
            public void speakSuccess() {
                LogUtil.d(TAG, "speakSuccess");
                if (speakerResult != null) {
                    speakerResult.speakSuccess();
                }
            }

            @Override
            public void speakError(int type, String message) {
                LogUtil.d(TAG, "speakError" + message);
                if (speakerResult != null) {
                    speakerResult.speakError(type, message);
                }
            }

            @Override
            public void speakStart() {
                if (speakerResult != null) {
                    speakerResult.speakStart();
                }
            }
        };
        changeTtsInitListener = new InitResultListener() {
            @Override
            public void initSuccess() {
                ttsStatus = 1;
                if (initListener != null) {
                    initListener.initSuccess();
                }

            }

            @Override
            public void initError(String message) {
                ttsStatus = 2;
                if (initListener != null) {
                    initListener.initError(message);
                }
            }
        };

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
                ttsProvider = new YunTtsImpl();
                break;
        }
    }

    @Override
    public void init(Context mContext) {
        ttsProvider.setInitResult(changeTtsInitListener);
        ttsProvider.setSpeakerResult(speakerResultListener);
        ttsProvider.init(mContext);

    }

    @Override
    public void speak(String msg, String speed, String volume) {
        if (ttsProvider == null) {
            return;
        }
        String tempSpeeds;
        try {
            int tempSpeed = Integer.parseInt(speed);
            if (tempSpeed < 0) {
                tempSpeed = 0;
            }
            if (tempSpeed > 9) {
                tempSpeed = 9;
            }
            tempSpeeds = tempSpeed + "";
        } catch (NumberFormatException e) {
            tempSpeeds = "5";
            e.printStackTrace();
        }
        String tempVolumes;
        try {
            int tempVolume = Integer.parseInt(volume);
            if (tempVolume < 0) {
                tempVolume = 0;
            }
            if (tempVolume > 9) {
                tempVolume = 9;
            }
            tempVolumes = tempVolume + "";
        } catch (NumberFormatException e) {
            tempVolumes = "5";
            e.printStackTrace();
        }
        LogUtil.d(TAG, "tempSpeeds__" + tempSpeeds + "tempVolume__" + tempVolumes);

        ttsProvider.speak(msg, tempSpeeds, tempVolumes);

    }

    @Override
    public String getPlatDes() {
        return ttsProvider.getPlatDes();
    }

    @Override
    public boolean isSpeaking() {
        return ttsProvider.isSpeaking();
    }

    @Override
    public void changePitch(String pitch) {
        ttsProvider.changePitch(pitch);
    }

    @Override
    public boolean changeSpeaker(Context mContext, String speaker) {
        int currentType = getPlatform();
        int position = -1;
        switch (currentType) {
            case TTS_BAI_DU_TTS:
                position = findPosition(speaker, arrayBaidu);
                break;
            case TTS_LING_YUN_TYPE:
                String[] tempSpeaker = speaker.split("\\.");
                if (tempSpeaker == null) {
                    LogUtil.d(TAG, "tempSpeaker==null");
                    return false;
                }
                LogUtil.d(TAG, tempSpeaker[tempSpeaker.length - 1]);
                position = findPosition(tempSpeaker[tempSpeaker.length - 1],
                        arrayLingYun);
                break;
            case TTS_XUN_FEI_TYPE:
                position = findPosition(speaker, arrayXunFei);
                break;
            case TTS_YUN_ZHI_SHENG_TYPE:
                position = 0;
                break;
        }
        if (position == -1) {
            return false;
        }
        return ttsProvider.changeSpeaker(mContext, speaker);
    }

    @Override
    public void changeParams(HashMap<String, String> params) {

    }

    @Override
    public void changePlatform(final Context mContext, HashMap<String, String> params) {
 //   final int plat = Integer.parseInt(params.get(TTS_PLATFORM));
//        if (plat == ttsProvider.getPlatform()) {
//            return;
//        }
//        ttsProvider.onDestroy();
//        initPlat(plat);
//
//        final InitResultListener initResultListener = new InitResultListener() {
//            @Override
//            public void initSuccess() {
//                ttsStatus = 1;
//                SharedPreferences sharedPreferences = mContext.
//                        getSharedPreferences(TTS_SP_KEY, Activity.MODE_PRIVATE);
//                sharedPreferences.edit().putInt(TTS_PLATFORM, plat).apply();
//                if (initListener != null) {
//                    initListener.initSuccess();
//                }
//            }
//
//            @Override
//            public void initError(String message) {
//                ttsStatus = 2;
//                reset(mContext);
//                if (initListener != null) {
//                    initListener.initError(message);
//                }
//            }
//        };
//        ttsProvider.setInitResult(initResultListener);
//        ttsProvider.setSpeakerResult(speakerResultListener);
//
//        ttsProvider.changePlatform(mContext, params);
        final int plat = Integer.parseInt(params.get(TTS_PLATFORM));
        SharedPreferences sharedPreferences = mContext.
                getSharedPreferences(TTS_SP_KEY, Activity.MODE_PRIVATE);
        sharedPreferences.edit().putInt(TTS_TEMP_PLATFORM, plat).apply();
        ttsProvider.changePlatform(mContext, params);

    }


    public void reset(Context mContext) {
        SharedPreferences sharedPreferences = mContext.
                getSharedPreferences(TTS_SP_KEY, Activity.MODE_PRIVATE);
        int plat = sharedPreferences.getInt(TTS_PLATFORM, TTS_BAI_DU_TTS);
        ttsProvider.onDestroy();
        initPlat(plat);
        ttsProvider.setSpeakerResult(speakerResultListener);
        ttsProvider.setInitResult(changeTtsInitListener);
        ttsProvider.init(mContext);

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
            case TTS_YUN_ZHI_SHENG_TYPE:
                position = 3;
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
        int position = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(content)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    public void setSpeakerResult(SpeakerResultListener speakerResult) {
        this.speakerResult = speakerResult;
    }

    @Override
    public void setInitResult(InitResultListener initResultListener) {
        this.initListener = initResultListener;

    }
}
