package com.interjoy.skasr.impls;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.interjoy.skasr.interfaces.AsrProvider;
import com.interjoy.skasr.manager.AsrManager;
import com.interjoy.util.LogUtil;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.asr.HciCloudAsr;
import com.sinovoice.hcicloudsdk.common.AuthExpireTime;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.Session;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrGrammarId;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.interjoy.skasr.manager.AsrManager.KEY_LING_YUN_TYPE;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/8/3
 */
public class HciAsrImpl implements AsrProvider {

    public String valueHci = "905d5423"; //key值
    public String valueHciDevelop = "4f6eb9f03584f3c5cf82798f7c5ea70f";//平台生成校验值
    public String valueHciColundUrl = "test.api.hcicloud.com:8888";//平台生成的url

    //
//    public String valueHci = "e05d542b"; //key值
//    public String valueHciDevelop = "b99be26181529d657a88af657f47a093";//平台生成校验值
//    public String valueHciColundUrl = "test.api.hcicloud.com:8888";//平台生成的url
    private String VALUE_HCI_CAP = "asr.cloud.freetalk";//平台生成的，选择的集成能力


    private String tempValueHci = null;
    private String tempValueHciDevelop = null;
    private String tempValueHciColundUrl = null;

    public static String KEY_HCI = "KEY_HCI";
    public static String KEY_HCI_DEVELOP = "KEY_HCI_DEVELOP";
    public static String KEY_HCI_COLUND_URL = "KEY_HCI_COLUND_URL";
    private static final String TAG = "HciAsrImpl";

    private AsrConfig asrConfig;//初始化配置

    private Session nSessionId;//一次识别的会话
    private AsrRecogResult asrResult;//识别结果
    private int errCode;//识别错误码
    private AsrResultListener asrListener;//识别结果回调
    private AsrInitListener asrInitListener;

    @Override
    public void init(Context mContext) {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences(AsrManager.KEY_ASR_SHARE_PRE,
                Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(tempValueHci)) {
            valueHci = sharedPreferences.getString(KEY_HCI, valueHci);
            valueHciDevelop = sharedPreferences.getString(KEY_HCI_DEVELOP, valueHciDevelop);
            valueHciColundUrl = sharedPreferences.getString(KEY_HCI_COLUND_URL, valueHciColundUrl);
        }

        InitParam initParam = getInitParam(mContext);
        String strConfig = initParam.getStringConfig();
        LogUtil.d(TAG, "\nhciInit config:" + strConfig);
        // 初始化

        int errCode = HciCloudSys.hciInit(strConfig, mContext);
        if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
            LogUtil.d(TAG, "\nhciInit error: " + HciCloudSys.hciGetErrorInfo(errCode));
            if (asrInitListener != null) {
                asrInitListener.initError(errCode, HciCloudSys.hciGetErrorInfo(errCode));
            }
            return;
        } else {
//            mLogView.append("\nhciInit success");

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
        initAsr(mContext);

    }


    private void initAsr(Context mContext) {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences(AsrManager.KEY_ASR_SHARE_PRE, Context.MODE_PRIVATE);
        //构造Asr初始化的帮助类的实例
        AsrInitParam asrInitParam = new AsrInitParam();
        // 获取App应用中的lib的路径,放置能力所需资源文件。如果使用/data/data/packagename/lib目录,需要添加android_so的标记
        String dataPath = mContext.getFilesDir().getAbsolutePath()
                .replace("files", "lib");
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, AsrInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, VALUE_HCI_CAP);
//        LogUtil.d(TAG,"HciAsrInit config :" + asrInitParam.getStringConfig());
        int errCode = HciCloudAsr.hciAsrInit(asrInitParam.getStringConfig());
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            LogUtil.d(TAG, "startAsr: " + HciCloudSys.hciGetErrorInfo(errCode) + errCode);

            if (asrInitListener != null) {
                if (errCode == 201) {
                    asrInitListener.initSuccess();
                } else {
                    asrInitListener.initError(errCode, HciCloudSys.hciGetErrorInfo(errCode));
                }
            }
//            return;
        } else {
            if (!TextUtils.isEmpty(tempValueHci)) {
                sharedPreferences.edit().putString(HciAsrImpl.KEY_HCI, tempValueHci).apply();
                sharedPreferences.edit().putString(HciAsrImpl.KEY_HCI_COLUND_URL, tempValueHciColundUrl).apply();
                sharedPreferences.edit().putString(HciAsrImpl.KEY_HCI_DEVELOP, tempValueHciDevelop).apply();
//
                tempValueHciDevelop = null;
                tempValueHci = null;
                tempValueHciColundUrl = null;
            }
            if (asrInitListener != null) {
                asrInitListener.initSuccess();
            }
            sharedPreferences.edit().putInt(AsrManager.KEY_ASR_PLAT_TYPE,
                    AsrManager.KEY_LING_YUN_TYPE).apply();
            LogUtil.d(TAG, "HciAsrInit Success");
        }

        AsrGrammarId grammarId = new AsrGrammarId();

        asrConfig = new AsrConfig();
        if (grammarId.isValid()) {
            asrConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_TYPE, AsrConfig.GrammarConfig.VALUE_OF_PARAM_GRAMMAR_TYPE_ID);
            asrConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_ID, grammarId.toString());
        }
        //使用公有云能力asr.cloud.freetalk.music或asr.cloud.freetalk.poi时需要传相对应的domain参数
        if (VALUE_HCI_CAP.indexOf("asr.cloud.freetalk.music") != -1) {
            asrConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_DOMAIN, "music");
        }
        if (VALUE_HCI_CAP.indexOf("asr.cloud.freetalk.poi") != -1) {
            asrConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_DOMAIN, "poi");
        }

        //私有云版本需根据需求配置property参数，详细见开发手册
        //asrConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_PROPERTY, "chinese_8k_common ");

        //若对录音文件识别时，vadtail设置为5000ms，可以减小语音分段影响，获得高的识别正确率率；
        //若对麦克风录音在线识别时，vadtail设置为500ms（默认值），可以减小识别响应时间，尽快返回识别结果；
        asrConfig.addParam(AsrConfig.VadConfig.PARAM_KEY_VAD_TAIL, "500");
        start();
    }

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
            LogUtil.d(TAG, "checkAuth failed: " + initResult);
            return initResult;
        }
    }

    /**
     * 加载初始化信息
     *
     * @param context 上下文语境
     * @return 系统初始化参数
     */
    private InitParam getInitParam(Context context) {
        String authDirPath = context.getFilesDir().getAbsolutePath();

        // 前置条件：无
        InitParam initparam = new InitParam();
        // 授权文件所在路径，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTH_PATH, authDirPath);
        // 灵云云服务的接口地址，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_CLOUD_URL,
                valueHciColundUrl);
        // 开发者Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_DEVELOPER_KEY,
                valueHciDevelop);
        // 应用Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_APP_KEY, valueHci);

        return initparam;
    }


    private void recognize(byte data[]) {
        errCode = HciCloudAsr.hciAsrRecog(nSessionId, data, null, null, asrResult);

        if (errCode != HciErrorCode.HCI_ERR_ASR_REALTIME_WAITING) {
            // 识别成功
            if (errCode == HciErrorCode.HCI_ERR_ASR_REALTIME_END) {
                // 若未检测到端点，但数据已经传入完毕，则需要告诉引擎数据输入完毕
                errCode = HciCloudAsr.hciAsrRecog(nSessionId, null, null, null, asrResult);

                if (HciErrorCode.HCI_ERR_NONE == errCode) {
                    LogUtil.d(TAG, "HciCloudAsr hciAsrRecog Success");
                    // 输出识别结果
                    printAsrResult(asrResult);
                    stop();
                    start();
                } else {
                    if (asrListener != null) {
                        LogUtil.d(TAG, "灵云识别错误码：" + errCode);
                        asrListener.error("灵云识别错误码：" + errCode);
                    }
                    stop();
                    start();
                }
            }
        }
    }


    /**
     * 输出ASR识别结果
     *
     * @param recogResult 识别结果
     */
    private void printAsrResult(AsrRecogResult recogResult) {
        if (recogResult.getRecogItemList().size() < 1) {
            LogUtil.d(TAG, "recognize result is null");
        }
        for (int i = 0; i < recogResult.getRecogItemList().size(); i++) {
            if (recogResult.getRecogItemList().get(i).getRecogResult() != null) {
                String content = recogResult.getRecogItemList().get(i)
                        .getRecogResult();
                LogUtil.d(TAG, content);
                if (asrListener != null) {
                    asrListener.success(content);

                }
                stop();
            } else {
                LogUtil.d(TAG, "result index:" + String.valueOf(i) + " result: null");

            }
        }
    }


    @Override
    public void changeAsr(Context mContext, Map<String, String> params) {
        tempValueHci = params.get(KEY_HCI);
        tempValueHciColundUrl = params.get(KEY_HCI_COLUND_URL);
        tempValueHciDevelop = params.get(KEY_HCI_DEVELOP);

        valueHciColundUrl = tempValueHciColundUrl;
        valueHciDevelop = tempValueHciDevelop;
        valueHci = tempValueHci;
        init(mContext);
//        start();
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
    public String getAsrInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_HCI, valueHci);
            jsonObject.put(KEY_HCI_DEVELOP, valueHciDevelop);
            jsonObject.put(KEY_HCI_COLUND_URL, valueHciColundUrl);
            jsonObject.put("platDes", "捷克华声");
            jsonObject.put("platForm", getPlatform());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void start() {
        if (asrConfig == null) {
            return;
        }
        AsrConfig config = new AsrConfig();
        config.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, VALUE_HCI_CAP);
        config.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, AsrConfig.VALUE_OF_YES);
        config.addParam(AsrConfig.AudioConfig.PARAM_KEY_ENCODE, AsrConfig.AudioConfig.VALUE_OF_PARAM_ENCODE_SPEEX);
        String sSessionConfig = config.getStringConfig();
        sSessionConfig += "," + asrConfig.getStringConfig();
        LogUtil.d(TAG, "hciAsrSessionStart config: " + sSessionConfig);
        nSessionId = new Session();
        errCode = HciCloudAsr.hciAsrSessionStart(sSessionConfig, nSessionId);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
            LogUtil.d(TAG, "hciAsrSessionStart error:" + HciCloudSys.hciGetErrorInfo(errCode));
            return;
        }
        LogUtil.d(TAG, "hciAsrSessionStart Success");
        asrResult = new AsrRecogResult();


    }

    @Override
    public void setPcmData(byte[] data, int size) {
        lock.lock();
        try {
            if (nSessionId != null) {
                recognize(data);
            }
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void stop() {
        if (nSessionId != null) {
            HciCloudAsr.hciAsrSessionStop(nSessionId);
            nSessionId = null;
        }
    }

    private Lock lock = new ReentrantLock();

    @Override
    public void destroy() {
        lock.lock();
        try {
            stop();
            HciCloudAsr.hciAsrRelease();
            HciCloudSys.hciRelease();
        } finally {
            lock.unlock();
        }


    }

    @Override
    public int getPlatform() {
        return KEY_LING_YUN_TYPE;
    }

    @Override
    public String getAppId() {
        return valueHci;
    }
}
