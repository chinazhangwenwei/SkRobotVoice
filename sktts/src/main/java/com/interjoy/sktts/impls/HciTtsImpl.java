package com.interjoy.sktts.impls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.interjoy.sktts.interfaces.TtsProvider;
import com.interjoy.sktts.util.LogUtil;
import com.sinovoice.hcicloudsdk.android.tts.player.TTSPlayer;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.common.AuthExpireTime;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.tts.TtsConfig;
import com.sinovoice.hcicloudsdk.common.tts.TtsInitParam;
import com.sinovoice.hcicloudsdk.player.TTSCommonPlayer;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.interjoy.sktts.manager.TtsManager.TTS_LING_YUN_TYPE;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/8/8
 */
public class HciTtsImpl implements TtsProvider, TTSPlayerListener {
    private static final String PLAT_DES = "捷通华声";//平台名称
    private static final String TAG = "HciTtsImpl";
    public static String TTS_SP_H_SPEAKER_KEY = "TTS_SP_SPEAKER_HCI";//说话人的sp key值

    private TTSCommonPlayer ttsCommonPlayer = null;
    private TtsConfig ttsConfig = null;
    private String valueTtsCap = "tts.cloud.wangjing";//说话人

    public String valueHci = "905d5423"; //key值
    public String valueHciDevelop = "4f6eb9f03584f3c5cf82798f7c5ea70f";//平台生成校验值
    public String valueHciColundUrl = "test.api.hcicloud.com:8888";//平台生成的url


    @Override
    public void init(Context mContext, InitResultListener initResultListener) {
        InitParam initParam = getInitParam(mContext);
        String strConfig = initParam.getStringConfig();
        // 初始化
        int errCode = HciCloudSys.hciInit(strConfig, mContext);
        if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
            LogUtil.d(TAG, "\nhciInit error: " + HciCloudSys.hciGetErrorInfo(errCode));
            if (initResultListener != null) {
                initResultListener.initError(HciCloudSys.hciGetErrorInfo(errCode));
            }
            return;
        } else {
            LogUtil.d(TAG, "hciInit success");
        }

        // 获取授权/更新授权文件 :
        errCode = checkAuthAndUpdateAuth();
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            LogUtil.d(TAG, "\nCheckAuthAndUpdateAuth error: " + HciCloudSys.hciGetErrorInfo(errCode));
            // 由于系统已经初始化成功,在结束前需要调用方法hciRelease()进行系统的反初始化
//            mLogView.append("\nCheckAuthAndUpdateAuth error: " + HciCloudSys.hciGetErrorInfo(errCode));
            HciCloudSys.hciRelease();
        }

//初始化ttsPlayer

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TTS_SP_KEY,
                Activity.MODE_PRIVATE);
        valueTtsCap = sharedPreferences.getString(TTS_SP_H_SPEAKER_KEY, valueTtsCap);

        // 构造Tts初始化的帮助类的实例
        TtsInitParam ttsInitParam = new TtsInitParam();
        // 获取App应用中的lib的路径
        String dataPath = mContext.getFilesDir().getAbsolutePath().replace("files", "lib");
        // 使用lib下的资源文件,需要添加android_so的标记
        ttsInitParam.addParam(TtsInitParam.PARAM_KEY_FILE_FLAG, TtsInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);

        ttsInitParam.addParam(TtsInitParam.PARAM_KEY_DATA_PATH, dataPath);
        // 用户可以根据自己可用的能力进行设置, 另外,此处可以传入多个能力值,并用;隔开
//        ttsInitParam.addParam(TtsInitParam.PARAM_KEY_INIT_CAP_KEYS, valueTtsCap);

        ttsCommonPlayer = new TTSPlayer();
        // 配置TTS初始化参数
//        ttsConfig = new TtsConfig();
        ttsCommonPlayer.init(ttsInitParam.getStringConfig(), this);
        ttsCommonPlayer.setContext(mContext);
        if (ttsCommonPlayer.getPlayerState() == TTSPlayer.PLAYER_STATE_IDLE) {
//
            Log.d(TAG, "initPlayer: TtsPlayer init succeed");
            if (initResultListener != null) {
                initResultListener.initSuccess();
            }
        } else {
            Log.d(TAG, "initPlayer: TtsPlayer init failed" + ttsCommonPlayer.getPlayerState());
            ttsCommonPlayer = null;
            if (initResultListener != null) {
                initResultListener.initError("灵云 tts　init error");
            }


        }
//        initPlayer(mContext);

    }

    /**
     * 加载初始化信息
     *
     * @param mContext 上下文语境
     * @return 系统初始化参数
     */
    private InitParam getInitParam(Context mContext) {
        String authDirPath = mContext.getFilesDir().getAbsolutePath();
        StringBuilder sb = new StringBuilder(authDirPath);
        sb.append(File.separator).append("tts");
        Log.d(TAG, "getInitParam: " + authDirPath);
        // 前置条件：无
        InitParam initparam = new InitParam();

        // 授权文件所在路径，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTH_PATH, authDirPath);

//        // 是否自动访问云授权,详见 获取授权/更新授权文件处注释
//        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTO_CLOUD_AUTH, "no");

        // 灵云云服务的接口地址，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_CLOUD_URL, valueHciColundUrl);

        // 开发者Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_DEVELOPER_KEY, valueHciDevelop);

        // 应用Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_APP_KEY, valueHci);


        return initparam;
    }

    /**
     * 获取授权
     *
     * @return true 成功
     */
    private int checkAuthAndUpdateAuth() {

        // 获取系统授权到期时间
        int initResult;
        AuthExpireTime objExpireTime = new AuthExpireTime();
        initResult = HciCloudSys.hciGetAuthExpireTime(objExpireTime);
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            // 显示授权日期,如用户不需要关注该值,此处代码可忽略
            Date date = new Date(objExpireTime.getExpireTime() * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.CHINA);
            LogUtil.d(TAG, "expire time: " + sdf.format(date));

            if (objExpireTime.getExpireTime() * 1000 > System
                    .currentTimeMillis()) {
                // 已经成功获取了授权,并且距离授权到期有充足的时间(>7天)
                LogUtil.d(TAG, "checkAuth success");
                return initResult;
            }

        }

        // 获取过期时间失败或者已经过期
        initResult = HciCloudSys.hciCheckAuth();
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            LogUtil.d(TAG, "checkAuth success");
            return initResult;
        } else {
            Log.e(TAG, "checkAuth failed: " + initResult);
            return initResult;
        }
    }


    @Override
    public void speak(String msg) {
        if (ttsCommonPlayer == null) {
            return;
        }
        // 配置播放器的属性。包括：音频格式，音库文件，语音风格，语速等等。详情见文档。
        ttsConfig = new TtsConfig();
        // 音频格式
        ttsConfig.addParam(TtsConfig.BasicConfig.PARAM_KEY_AUDIO_FORMAT, "pcm16k16bit");
        // 指定语音合成的能力(云端合成,发言人是XiaoKun)
        ttsConfig.addParam(TtsConfig.SessionConfig.PARAM_KEY_CAP_KEY, valueTtsCap);
        // 设置合成语速
        ttsConfig.addParam(TtsConfig.BasicConfig.PARAM_KEY_SPEED, "5");
        // property为私有云能力必选参数，公有云传此参数无效
        ttsConfig.addParam("property", "cn_xiaokun_common");

        if (ttsCommonPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PLAYING
                || ttsCommonPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PAUSE) {
            ttsCommonPlayer.stop();
        }

        if (ttsCommonPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_IDLE) {
            ttsCommonPlayer.play(msg,
                    ttsConfig.getStringConfig());
        } else {
//            Toast.makeText(HciCloudTTSPlayerExampleActivity.this, "播放器内部状态错误",
//                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "speak: 播放器内部状态错误" + ttsCommonPlayer.getPlayerState());
        }
    }

    @Override
    public int getPlatform() {
        return TTS_LING_YUN_TYPE;
    }

    @Override
    public String getPlatDes() {
        return PLAT_DES;
    }

    @Override
    public String getCurrentSpeaker() {
        String array[] = valueTtsCap.split("\\.");
        return array[array.length - 1];

    }

    @Override
    public void changeVolume(String volume) {

    }

    @Override
    public void changeSpeed(String speed) {

    }

    @Override
    public void changePitch(String pitch) {

    }

    @Override
    public void changeSpeaker(Context mContext, String speaker) {
        valueTtsCap = speaker;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TTS_SP_KEY,
                Activity.MODE_PRIVATE);
        sharedPreferences.edit().putString(TTS_SP_H_SPEAKER_KEY, valueTtsCap).apply();

    }

    @Override
    public void changeParams(HashMap<String, String> params) {

    }

    @Override
    public void changePlatform(Context mContext, HashMap<String, Integer> params) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {
        if (ttsCommonPlayer.canStop()) {
            ttsCommonPlayer.stop();
        }

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        if (null != ttsCommonPlayer) {
            onStop();
            if (ttsCommonPlayer.getPlayerState() != 0) {
                ttsCommonPlayer.release();
            }
            HciCloudSys.hciRelease();
        }

    }

    @Override
    public void onPlayerEventStateChange(TTSCommonPlayer.PlayerEvent playerEvent) {

    }

    @Override
    public void onPlayerEventProgressChange(TTSCommonPlayer.PlayerEvent playerEvent,
                                            int i, int i1) {

    }

    @Override
    public void onPlayerEventPlayerError(TTSCommonPlayer.PlayerEvent playerEvent
            , int i) {

    }
}
