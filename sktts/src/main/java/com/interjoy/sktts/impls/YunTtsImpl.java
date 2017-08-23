package com.interjoy.sktts.impls;

import android.content.Context;

import com.interjoy.sktts.interfaces.TtsProvider;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;

import java.util.HashMap;

import static com.interjoy.sktts.manager.TtsManager.TTS_YUN_ZHI_SHENG_TYPE;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/8/14
 */
public class YunTtsImpl implements TtsProvider {
    private SpeechSynthesizer mTTSPlayer = null;
    private static final String PLAT_DES = "云知声";//平台名称
    private static final String TAG = "YunTtsImpl";
    private String speaker = "defaultSpeaker";
    private SpeakerResultListener speakResult;
    private InitResultListener initResultListener;


    public String appKey = "pbm7th2ifvq4hhij6kfmgo2as3uapfbds4lnbfig";
    public String secret = "92b7af3dc000491fa6916c44bb87e7c4";


    @Override
    public void init(Context mContext) {
        mTTSPlayer = new SpeechSynthesizer(mContext, appKey, secret);
        mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_NET);
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_NAME, "");
        // 设置语音合成回调监听
        mTTSPlayer.setTTSListener(new SpeechSynthesizerListener() {

            @Override
            public void onEvent(int type) {
                switch (type) {
                    case SpeechConstants.TTS_EVENT_INIT:
                        // 初始化成功回调
                        if (initResultListener != null) {
                            initResultListener.initSuccess();
                        }
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                        // 开始合成回调
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                        // 合成结束回调
                        if (speakResult != null) {
                            speakResult.speakSuccess();
                        }
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                        // 开始缓存回调
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_READY:
                        // 缓存完毕回调
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_START:
                        // 开始播放回调
                        if (speakResult != null) {
                            speakResult.speakStart();
                        }
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_END:
                        // 播放完成回调
                        if (speakResult != null) {
                            speakResult.speakSuccess();
                        }
                        break;
                    case SpeechConstants.TTS_EVENT_PAUSE:
                        // 暂停回调
                        break;
                    case SpeechConstants.TTS_EVENT_RESUME:
                        // 恢复回调
                        break;
                    case SpeechConstants.TTS_EVENT_STOP:
                        // 停止回调
                        break;
                    case SpeechConstants.TTS_EVENT_RELEASE:
                        // 释放资源回调
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onError(int type, String errorMSG) {
                // 语音合成错误回调
//                hitErrorMsg(errorMSG);
                if (speakResult != null) {
                    speakResult.speakError(type, errorMSG);
                }

            }
        });
        mTTSPlayer.init("");
    }

    @Override
    public void speak(String msg, String speed, String volume) {
//        mTTSPlayer.synthesizeText(msg);
        mTTSPlayer.playText(msg);

    }

    @Override
    public boolean isSpeaking() {
        return mTTSPlayer.isPlaying();
    }

    @Override
    public void changePitch(String pitch) {

    }

    @Override
    public boolean changeSpeaker(Context mContext, String speaker) {
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
        return TTS_YUN_ZHI_SHENG_TYPE;
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
        mTTSPlayer.pause();
    }

    @Override
    public void onStop() {
        mTTSPlayer.stop();
    }

    @Override
    public void onResume() {
        mTTSPlayer.resume();
    }

    @Override
    public void onDestroy() {
        mTTSPlayer.stop();

    }

    @Override
    public void setSpeakerResult(SpeakerResultListener speakerResult) {
        this.speakResult = speakerResult;
    }

    @Override
    public void setInitResult(InitResultListener initResultListener) {
        this.initResultListener = initResultListener;
    }
}
