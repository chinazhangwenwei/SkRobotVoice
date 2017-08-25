package com.interjoy.sktts.impls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.SynthesizerTool;
import com.baidu.tts.client.TtsMode;
import com.interjoy.sktts.interfaces.TtsProvider;
import com.interjoy.sktts.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static com.interjoy.sktts.manager.TtsManager.TTS_BAI_DU_TTS;

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
public class BaiduTtsImpl implements TtsProvider, SpeechSynthesizerListener {
    private static final String PLAT_DES = "百度";//平台名称
    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "BaiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
//    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
//    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    /**
     * 0(普通女声)
     * 1(普通男声)
     * 2(特别男声)
     * 3(情感男声<度逍遥>)
     * 4(情感儿童声<度丫丫>)
     */
    private String speaker = "0";
    public static String apiKey = "VShBu32tOmOmcwPK4jTd05oP";
    public static String appId = "9924961";
    public static String secretKey = "95d9e8eab593bdf54e9e5efd92d58937";
    private String tempApiKey = null;
    private String tempAppId = null;
    private String tempSecretKey = null;

    //修改成功后保存平台信息的key值
    public static String KEY_BAI_DU_API = "KEY_BAI_DU_API";
    public static String KEY_BAI_DU_ID = "KEY_BAI_DU_ID";
    public static String KEY_BAI_DU_SECRET = "KEY_BAI_DU_SECRET";

    //修改平台时候的临时key值
    public static String KEY_TEMP_BAI_DU_API = "KEY_TEMP_BAI_DU_API";
    public static String KEY_TEMP_BAI_DU_ID = "KEY_TEMP_BAI_DU_ID";
    public static String KEY_TEMP_BAI_DU_SECRET = "KEY_TEMP_BAI_DU_SECRET";

//    App ID: 10007892
//
//    API Key: DDSTkGS6yIZL4GvXlbETquHj
//
//    Secret Key: 4526eee4fb680d973820ef8651fa7912

    public static String TTS_SP_B_SPEAKER_KEY = "TTS_SP_SPEAKER_BAIDU";

    private SpeakerResultListener speakResult;
    private InitResultListener initResultListener;
    private static final String TAG = "BaiduTtsImpl";
    private boolean isSpeaking = false;


    @Override
    public void init(Context mContext) {
        initialEnv(mContext);
        initialTts(mContext);

    }

    private void initialEnv(Context mContext) {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + File.separator + SAMPLE_DIR_NAME;

        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(mContext, false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + File.separator + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(mContext, false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + File.separator + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(mContext, false, TEXT_MODEL_NAME, mSampleDirPath + File.separator + TEXT_MODEL_NAME);
    }

    //创建文件夹
    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(Context mContext, boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = mContext.getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initialTts(Context mContext) {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences(TTS_SP_KEY,
                Activity.MODE_PRIVATE);
        speaker = sharedPreferences.getString(TTS_SP_B_SPEAKER_KEY, speaker);
        tempAppId = sharedPreferences.getString(KEY_TEMP_BAI_DU_ID, "");
        tempSecretKey = sharedPreferences.getString(KEY_TEMP_BAI_DU_SECRET, "");
        tempApiKey = sharedPreferences.getString(KEY_TEMP_BAI_DU_API, "");
        if (TextUtils.isEmpty(tempAppId) || TextUtils.isEmpty(tempApiKey)
                || TextUtils.isEmpty(tempSecretKey)) {
            //切换平台信息不完整，走正确初始化流程
            LogUtil.d(TAG, "切换平台信息不完整，走正确初始化流程");
            tempAppId = null;
            tempSecretKey = null;
            tempApiKey = null;
            appId = sharedPreferences.getString(KEY_BAI_DU_ID, appId);
            apiKey = sharedPreferences.getString(KEY_BAI_DU_API, apiKey);
            secretKey = sharedPreferences.getString(KEY_BAI_DU_SECRET, secretKey);
        } else {
            //切换平台信息完整，走切换逻辑
            LogUtil.d(TAG, "切换平台信息完整，走切换逻辑");
            appId = tempAppId;
            apiKey = tempApiKey;
            secretKey = tempSecretKey;
            sharedPreferences.edit().putString(KEY_TEMP_BAI_DU_ID, "").apply();
            sharedPreferences.edit().putString(KEY_TEMP_BAI_DU_SECRET, "").apply();
            sharedPreferences.edit().putString(KEY_TEMP_BAI_DU_API, "").apply();
        }
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(mContext);
        mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了正式离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        // 如果合成结果出现临时授权文件将要到期的提示，说明使用了临时授权文件，请删除临时授权即可。
//         mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
//                + LICENSE_FILE_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        mSpeechSynthesizer.setAppId(appId/*这里只是为了让Demo运行使用的APPID,请替换成自己的id。*/);
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        mSpeechSynthesizer.setApiKey(apiKey,
                secretKey/*这里只是为了让Demo正常运行使用APIKey,请替换成自己的APIKey*/);
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, speaker);
        // 设置Mix模式的合成策略
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 授权检测接口(只是通过AuthInfo进行检验授权是否成功。)
        // AuthInfo接口用于测试开发者是否成功申请了在线或者离线授权，如果测试授权成功了，可以删除AuthInfo部分的代码（该接口首次验证时比较耗时），不会影响正常使用（合成使用时SDK内部会自动验证授权）
        AuthInfo authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);


        if (authInfo.isSuccess()) {
            LogUtil.d(TAG, "initSuccess" + appId);
            if (!TextUtils.isEmpty(tempAppId)) {
                sharedPreferences.edit().putString(KEY_BAI_DU_API, tempApiKey).apply();
                sharedPreferences.edit().putString(KEY_BAI_DU_ID, tempAppId).apply();
                sharedPreferences.edit().putString(KEY_BAI_DU_SECRET, tempSecretKey).apply();
                tempApiKey = null;
                tempAppId = null;
                tempSecretKey = null;
            }
            if (initResultListener != null) {
                initResultListener.initSuccess();
            }
        } else {
            if (initResultListener != null) {
                String errorMsg = authInfo.getTtsError().getDetailMessage();
                initResultListener.initError(errorMsg);
            }

        }

        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
//        // 加载离线英文资源（提供离线英文合成功能）
//        int result =
//                mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
//                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        printEngineInfo();
    }

    private void printEngineInfo() {
        LogUtil.d(TAG, "EngineVersioin=" + SynthesizerTool.getEngineVersion());
        LogUtil.d(TAG, "EngineInfo=" + SynthesizerTool.getEngineInfo());
        String textModelInfo = SynthesizerTool.getModelInfo(mSampleDirPath + "/" + TEXT_MODEL_NAME);
        LogUtil.d(TAG, "textModelInfo=" + textModelInfo);
        String speechModelInfo = SynthesizerTool.getModelInfo(mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        LogUtil.d(TAG, "speechModelInfo=" + speechModelInfo);
    }

    @Override
    public void speak(String msg, String speed, String volume) {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, speed);
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, volume);
            mSpeechSynthesizer.speak(msg);
        }
    }

    @Override
    public boolean isSpeaking() {
        return isSpeaking;
    }

    @Override
    public String getPlatDes() {
        return PLAT_DES;
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
        if (null != mSpeechSynthesizer) {
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, pitch);
        }
    }

    @Override
    public boolean changeSpeaker(Context mContext, String speaker) {
        this.speaker = speaker;
        if (null != mSpeechSynthesizer) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(TTS_SP_KEY,
                    Activity.MODE_PRIVATE);
            sharedPreferences.edit().putString(TTS_SP_B_SPEAKER_KEY, speaker).apply();
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, speaker);
            return true;
        }
        return false;
    }


    @Override
    public void changePlatform(Context mContext, HashMap<String, String> params) {
        tempAppId = params.get(KEY_BAI_DU_ID);
        tempApiKey = params.get(KEY_BAI_DU_API);
        tempSecretKey = params.get(KEY_BAI_DU_SECRET);
        if (TextUtils.isEmpty(tempAppId) || TextUtils.isEmpty(tempApiKey)
                || TextUtils.isEmpty(tempSecretKey)) {
            return;
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TTS_SP_KEY,
                Activity.MODE_PRIVATE);
        sharedPreferences.edit().putString(KEY_TEMP_BAI_DU_API, tempApiKey).commit();
        sharedPreferences.edit().putString(KEY_TEMP_BAI_DU_ID, tempAppId).commit();
        sharedPreferences.edit().putString(KEY_TEMP_BAI_DU_SECRET, tempSecretKey).commit();

//
//        if (TextUtils.isEmpty(tempAppId) || TextUtils.isEmpty(tempApiKey)
//                || TextUtils.isEmpty(tempSecretKey)) {
//            tempAppId = null;
//            tempApiKey = null;
//            tempSecretKey = null;
//            init(mContext);
//            return;
//        }
//        appId = tempAppId;
//        apiKey = tempApiKey;
//        secretKey = tempSecretKey;
//        init(mContext);

    }

    @Override
    public String getTtsInfo() {
        String info = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_BAI_DU_ID, appId);
            jsonObject.put(KEY_BAI_DU_API, apiKey);
            jsonObject.put(KEY_BAI_DU_SECRET, secretKey);
            info = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            info = "";
        }
        return info;

    }

    @Override
    public String getCurrentSpeaker() {
        return speaker;
    }

    @Override
    public int getPlatform() {
        return TTS_BAI_DU_TTS;
    }

    @Override
    public void onPause() {
        if (null != mSpeechSynthesizer) {
            mSpeechSynthesizer.pause();
        }

    }

    @Override
    public void onStop() {
        if (null != mSpeechSynthesizer) {
            mSpeechSynthesizer.stop();
        }
    }

    @Override
    public void onResume() {
        if (null != mSpeechSynthesizer) {
            mSpeechSynthesizer.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (null != mSpeechSynthesizer) {
            mSpeechSynthesizer.release();
        }
    }


    @Override
    public void changeParams(HashMap<String, String> params) {

    }

    @Override
    public void onSynthesizeStart(String s) {

    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

    }

    @Override
    public void onSynthesizeFinish(String s) {

    }

    @Override
    public void onSpeechStart(String s) {
        isSpeaking = true;
        if (speakResult != null) {
            speakResult.speakStart();
        }

    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {

    }

    @Override
    public void onSpeechFinish(String s) {
        isSpeaking = false;
        if (speakResult != null) {
            speakResult.speakSuccess();
        }
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        isSpeaking = false;
        if (speakResult != null) {
            speakResult.speakError(speechError.code, s);
        }
    }
}
