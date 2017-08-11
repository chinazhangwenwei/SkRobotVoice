package com.interjoy.skrobotvoicedemo.skasr.hcicloud;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.interjoy.skrobotvoicedemo.constant.HciCloudParams;
import com.interjoy.skrobotvoicedemo.skasr.AsrListener;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HciCloudFuncHelper  {
    private static final String TAG = HciCloudFuncHelper.class.getSimpleName();


    public static void init(Context context, String capKey) {
        InitParam initParam = getInitParam(context);
        String strConfig = initParam.getStringConfig();
        Log.i(TAG, "\nhciInit config:" + strConfig);

        // 初始化
        int errCode = HciCloudSys.hciInit(strConfig, context);
        if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
//            mLogView.append("\nhciInit error: " + HciCloudSys.hciGetErrorInfo(errCode));
            Log.i(TAG, "\nhciInit error: " + HciCloudSys.hciGetErrorInfo(errCode));
            return;
        } else {
//            mLogView.append("\nhciInit success");
            Log.i(TAG, "hciInit success");
        }

        // 获取授权/更新授权文件 :
        errCode = checkAuthAndUpdateAuth();
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            Log.i(TAG, "\nCheckAuthAndUpdateAuth error: " + HciCloudSys.hciGetErrorInfo(errCode));
            // 由于系统已经初始化成功,在结束前需要调用方法hciRelease()进行系统的反初始化
//            mLogView.append("\nCheckAuthAndUpdateAuth error: " + HciCloudSys.hciGetErrorInfo(errCode));
            HciCloudSys.hciRelease();
        }
        startAsr(context, capKey);
    }

    private static int checkAuthAndUpdateAuth() {

        // 获取系统授权到期时间
        int initResult;
        AuthExpireTime objExpireTime = new AuthExpireTime();
        initResult = HciCloudSys.hciGetAuthExpireTime(objExpireTime);
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            // 显示授权日期,如用户不需要关注该值,此处代码可忽略
            Date date = new Date(objExpireTime.getExpireTime() * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.CHINA);
            Log.i(TAG, "expire time: " + sdf.format(date));

            if (objExpireTime.getExpireTime() * 1000 > System
                    .currentTimeMillis()) {
                // 已经成功获取了授权,并且距离授权到期有充足的时间(>7天)
                Log.i(TAG, "checkAuth success");
                return initResult;
            }

        }

        // 获取过期时间失败或者已经过期
        initResult = HciCloudSys.hciCheckAuth();
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            Log.i(TAG, "checkAuth success");
            return initResult;
        } else {
            Log.e(TAG, "checkAuth failed: " + initResult);
            return initResult;
        }
    }

    /**
     * 加载初始化信息
     *
     * @param context 上下文语境
     * @return 系统初始化参数
     */
    private static InitParam getInitParam(Context context) {
        String authDirPath = context.getFilesDir().getAbsolutePath();

        // 前置条件：无
        InitParam initparam = new InitParam();
        // 授权文件所在路径，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTH_PATH, authDirPath);
        // 灵云云服务的接口地址，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_CLOUD_URL,
                HciCloudParams.KEY_HCI_COLUND_URL);
        // 开发者Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_DEVELOPER_KEY,
                HciCloudParams.KEY_HCI_DEVELOP);
        // 应用Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_APP_KEY, HciCloudParams.KEY_HCI
        );

        // 配置日志参数
        String sdcardState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            String sdPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            String packageName = context.getPackageName();

            String logPath = sdPath + File.separator + "sinovoice"
                    + File.separator + packageName + File.separator + "log"
                    + File.separator;

            // 日志文件地址
            File fileDir = new File(logPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            // 日志的路径，可选，如果不传或者为空则不生成日志
            initparam.addParam(InitParam.LogParam.PARAM_KEY_LOG_LEVEL, "3");
            initparam.addParam(InitParam.LogParam.PARAM_KEY_LOG_FILE_PATH, logPath);
        }

        return initparam;
    }

    /*
     * 非实时识别
     */
//    public static void Recog(String capkey, AsrConfig recogConfig, String audioFile) {
//        Log.d(TAG, "......Recog ......");
//
//        // 载入语音数据文件
//        byte[] voiceData = getAssetFileData(audioFile);
//        if (null == voiceData) {
//            Log.d(TAG, "Open input voice file" + audioFile + " error!");
//            return;
//        }
//
//        // 启动 ASR Session
//        int errCode = -1;
//        AsrConfig config = new AsrConfig();
//        config.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
//        config.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, "no");
//        config.addParam(AsrConfig.AudioConfig.PARAM_KEY_ENCODE, AsrConfig.AudioConfig.VALUE_OF_PARAM_ENCODE_SPEEX);
//        String sSessionConfig = config.getStringConfig();
//        sSessionConfig += "," + recogConfig.getStringConfig();
//        Log.i(TAG, "hciAsrSessionStart config: " + sSessionConfig);
//        Session nSessionId = new Session();
//        errCode = HciCloudAsr.hciAsrSessionStart(sSessionConfig, nSessionId);
//        if (HciErrorCode.HCI_ERR_NONE != errCode) {
//            Log.d(TAG, "hciAsrSessionStart error:" + HciCloudSys.hciGetErrorInfo(errCode));
//            return;
//        }
//        Log.d(TAG, "hciAsrSessionStart Success");
//
//        // 识别
//        AsrRecogResult asrResult = new AsrRecogResult();
//        Log.i(TAG, "HciCloudAsr hciAsrRecog config：" + recogConfig + "\n");
//        errCode = HciCloudAsr.hciAsrRecog(nSessionId, voiceData, null, null, asrResult);
//
//        if (HciErrorCode.HCI_ERR_NONE == errCode) {
//            Log.i(TAG, "HciCloudAsr hciAsrRecog Success");
//            // 输出识别结果
//            printAsrResult(asrResult);
//        } else {
//            Log.d(TAG, "hciAsrRecog error:" + HciCloudSys.hciGetErrorInfo(errCode));
//        }
//
//        // 终止session
//        HciCloudAsr.hciAsrSessionStop(nSessionId);
//        Log.d(TAG, "hciAsrSessionStop");
//    }

//    public static void RealtimeRecogInit(Context context,)

    public static AsrConfig recogConfig;


    public static void startAsr(Context context, String capkey) {
        //初始化ASR
        //构造Asr初始化的帮助类的实例
        AsrInitParam asrInitParam = new AsrInitParam();
        // 获取App应用中的lib的路径,放置能力所需资源文件。如果使用/data/data/packagename/lib目录,需要添加android_so的标记
        String dataPath = context.getFilesDir().getAbsolutePath()
                .replace("files", "lib");
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, AsrInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, capkey);
//        Log.d(TAG,"HciAsrInit config :" + asrInitParam.getStringConfig());
        int errCode = HciCloudAsr.hciAsrInit(asrInitParam.getStringConfig());

        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            Log.d(TAG, "startAsr: " + HciCloudSys.hciGetErrorInfo(errCode));
//
            return;
        } else {
//            Log.d(TAG,"HciAsrInit Success");
            Log.d(TAG, "HciAsrInit Success");
        }

        AsrGrammarId grammarId = new AsrGrammarId();
//        if (capkey.equalsIgnoreCase("asr.local.grammar.v4")) {
//
//            AsrConfig loadConfig = new AsrConfig();
//            loadConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_TYPE, AsrConfig.GrammarConfig.VALUE_OF_PARAM_GRAMMAR_TYPE_JSGF);
//            loadConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_IS_FILE, AsrConfig.VALUE_OF_NO);
//            loadConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
//            byte[] grammarData = getAssetFileData("stock_10001.gram");
//            String strGrammarData = null;
//            try {
//                strGrammarData = new String(grammarData, "utf-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            errCode = HciCloudAsr.hciAsrLoadGrammar(loadConfig.getStringConfig(), strGrammarData, grammarId);
//            if (errCode != HciErrorCode.HCI_ERR_NONE) {
//                Log.d(TAG, "hciAsrLoadGrammar error:" + HciCloudSys.hciGetErrorInfo(errCode));
//                HciCloudAsr.hciAsrRelease();
//                return;
//            } else {
//                Log.d(TAG, "hciAsrLoadGrammar Success");
//            }
//        }

        recogConfig = new AsrConfig();
        if (grammarId.isValid()) {
            recogConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_TYPE, AsrConfig.GrammarConfig.VALUE_OF_PARAM_GRAMMAR_TYPE_ID);
            recogConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_ID, grammarId.toString());
        }
        //使用公有云能力asr.cloud.freetalk.music或asr.cloud.freetalk.poi时需要传相对应的domain参数
        if (capkey.indexOf("asr.cloud.freetalk.music") != -1) {
            recogConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_DOMAIN, "music");
        }
        if (capkey.indexOf("asr.cloud.freetalk.poi") != -1) {
            recogConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_DOMAIN, "poi");
        }

        //私有云版本需根据需求配置property参数，详细见开发手册
        //recogConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_PROPERTY, "chinese_8k_common ");

        //若对录音文件识别时，vadtail设置为5000ms，可以减小语音分段影响，获得高的识别正确率率；
        //若对麦克风录音在线识别时，vadtail设置为500ms（默认值），可以减小识别响应时间，尽快返回识别结果；
        recogConfig.addParam(AsrConfig.VadConfig.PARAM_KEY_VAD_TAIL, "500");

        String audioFile = "san_xia_shui_li_16k16.pcm";

        //非实时识别
//        Recog(capkey,recogConfig,audioFile);

        //非实时识别
//        RealtimeRecog(capkey, recogConfig, audioFile);

//        if (grammarId.isValid()) {
//            HciCloudAsr.hciAsrUnloadGrammar(grammarId);
////        	Log.d(TAG,"hciAsrUnloadGrammar");
//        }
//        //反初始化ASR
//        HciCloudAsr.hciAsrRelease();
//        Log.d(TAG,"hciAsrRelease");

//        return;

    }


    private static Session nSessionId;
    private static AsrRecogResult asrResult;
    private static int errCode;
    public static AsrListener asrListner;

    /*
     *实时识别
     */
    public static void RealtimeRecog(String capkey, AsrConfig recogConfig, String audioFile, byte datas[], int size) {
//		Log.d(TAG,"......RealtimeRecog ......");

        // 载入语音数据文件
//        int errCode = -1;
        if (nSessionId == null) {
            errCode = -1;
            if (recogConfig == null) {
                return;
            }
            AsrConfig config = new AsrConfig();
            config.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
            config.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, AsrConfig.VALUE_OF_YES);
            config.addParam(AsrConfig.AudioConfig.PARAM_KEY_ENCODE, AsrConfig.AudioConfig.VALUE_OF_PARAM_ENCODE_SPEEX);
            String sSessionConfig = config.getStringConfig();
            sSessionConfig += "," + recogConfig.getStringConfig();
            Log.i(TAG, "hciAsrSessionStart config: " + sSessionConfig);
            nSessionId = new Session();
            errCode = HciCloudAsr.hciAsrSessionStart(sSessionConfig, nSessionId);
            if (HciErrorCode.HCI_ERR_NONE != errCode) {
                Log.d(TAG, "hciAsrSessionStart error:" + HciCloudSys.hciGetErrorInfo(errCode));
                return;
            }
            Log.d(TAG, "hciAsrSessionStart Success");

            asrResult = new AsrRecogResult();
        }
        errCode = HciCloudAsr.hciAsrRecog(nSessionId, datas, null, null, asrResult);

        if (errCode != HciErrorCode.HCI_ERR_ASR_REALTIME_WAITING) {
            // 识别成功
            if (errCode == HciErrorCode.HCI_ERR_ASR_REALTIME_END) {
                // 若未检测到端点，但数据已经传入完毕，则需要告诉引擎数据输入完毕
                errCode = HciCloudAsr.hciAsrRecog(nSessionId, null, null, null, asrResult);

                if (HciErrorCode.HCI_ERR_NONE == errCode) {
                    Log.i(TAG, "HciCloudAsr hciAsrRecog Success");
                    // 输出识别结果
                    printAsrResult(asrResult);
//                    nSessionId = null;
                } else {
//                    Log.d(TAG, "hciAsrRecog error:" + HciCloudSys.hciGetErrorInfo(errCode));
                    nSessionId = null;
                }
            } else {
//                break;
            }
        }

//        // 若未检测到端点，但数据已经传入完毕，则需要告诉引擎数据输入完毕
//        if (errCode == HciErrorCode.HCI_ERR_ASR_REALTIME_WAITING) {
//            errCode = HciCloudAsr.hciAsrRecog(nSessionId, null, recogConfig.getStringConfig(), null, asrResult);
//
//            if (HciErrorCode.HCI_ERR_NONE == errCode) {
//                Log.i(TAG, "HciCloudAsr hciAsrRecog Success");
//                // 输出识别结果
//                printAsrResult(asrResult);
//            } else {
//                Log.d(TAG,"hciAsrRecog error:"
//                        + HciCloudSys.hciGetErrorInfo(errCode));
//            }
//        }

        // 终止session
//        HciCloudAsr.hciAsrSessionStop(nSessionId);
//        Log.d(TAG,"hciAsrSessionStop");


//        byte[] voiceData = getAssetFileData(audioFile);
//        if (null == voiceData) {
//            Log.d(TAG,"Open input voice file" + audioFile + " error!");
//            return;
//        }
//
//        // 启动 ASR Session
//        int errCode = -1;
//        AsrConfig config = new AsrConfig();
//        config.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
//        config.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, AsrConfig.VALUE_OF_YES);
//        config.addParam(AsrConfig.AudioConfig.PARAM_KEY_ENCODE, AsrConfig.AudioConfig.VALUE_OF_PARAM_ENCODE_SPEEX);
//        String sSessionConfig = config.getStringConfig();
//        sSessionConfig += "," + recogConfig.getStringConfig();
//        Log.i(TAG, "hciAsrSessionStart config: " + sSessionConfig);
//        Session nSessionId = new Session();
//        errCode = HciCloudAsr.hciAsrSessionStart(sSessionConfig, nSessionId);
//        if (HciErrorCode.HCI_ERR_NONE != errCode) {
//            Log.d(TAG,"hciAsrSessionStart error:" + HciCloudSys.hciGetErrorInfo(errCode));
//            return;
//        }
//        Log.d(TAG,"hciAsrSessionStart Success");
//
//        AsrRecogResult asrResult = new AsrRecogResult();
//        int nPerLen = 3200; //0.1s
//        int nLen = 0;
//        while (nLen < voiceData.length) {
//            if (voiceData.length - nLen <= nPerLen) {
//                nPerLen = voiceData.length - nLen;
//            }
//
//            byte[] subVoiceData = new byte[nPerLen];
//            System.arraycopy(voiceData, nLen, subVoiceData, 0, nPerLen);
//            // 调用识别方法,将音频数据不短的传入引擎
//            errCode = HciCloudAsr.hciAsrRecog(nSessionId, datas, null, null, asrResult);
//
//            if (errCode != HciErrorCode.HCI_ERR_ASR_REALTIME_WAITING) {
//                // 识别成功
//                if (errCode == HciErrorCode.HCI_ERR_ASR_REALTIME_END) {
//                    // 若未检测到端点，但数据已经传入完毕，则需要告诉引擎数据输入完毕
//                    errCode = HciCloudAsr.hciAsrRecog(nSessionId, null, null, null, asrResult);
//
//                    if (HciErrorCode.HCI_ERR_NONE == errCode) {
//                        Log.i(TAG, "HciCloudAsr hciAsrRecog Success");
//                        // 输出识别结果
//                        printAsrResult(asrResult);
//                    } else {
//                        Log.d(TAG,"hciAsrRecog error:" + HciCloudSys.hciGetErrorInfo(errCode));
//                    }
//                } else {
//                    break;
//                }
//            }
//            nLen += nPerLen;
//        }

//        // 若未检测到端点，但数据已经传入完毕，则需要告诉引擎数据输入完毕
//        if (errCode == HciErrorCode.HCI_ERR_ASR_REALTIME_WAITING) {
//            errCode = HciCloudAsr.hciAsrRecog(nSessionId, null, recogConfig.getStringConfig(), null, asrResult);
//
//            if (HciErrorCode.HCI_ERR_NONE == errCode) {
//                Log.i(TAG, "HciCloudAsr hciAsrRecog Success");
//                // 输出识别结果
//                printAsrResult(asrResult);
//            } else {
//                Log.d(TAG, "hciAsrRecog error:"
//                        + HciCloudSys.hciGetErrorInfo(errCode));
//            }
//        }
//
//        // 终止session
//        HciCloudAsr.hciAsrSessionStop(nSessionId);
//        Log.d(TAG, "hciAsrSessionStop");
    }

    public static void stopRecodNize() {
        nSessionId = null;
    }

    /**
     * 输出ASR识别结果
     *
     * @param recogResult 识别结果
     */
    private static void printAsrResult(AsrRecogResult recogResult) {
        if (recogResult.getRecogItemList().size() < 1) {
            Log.d(TAG, "recognize result is null");
        }
        for (int i = 0; i < recogResult.getRecogItemList().size(); i++) {
            if (recogResult.getRecogItemList().get(i).getRecogResult() != null) {
                String utf8 = recogResult.getRecogItemList().get(i)
                        .getRecogResult();
                asrListner.onResult(0, utf8);
                HciCloudFuncHelper.stopRecodNize();
                Log.d(TAG, "result index:" + String.valueOf(i) + " result:" + utf8);
            } else {
                Log.d(TAG, "result index:" + String.valueOf(i) + " result: null");
            }
        }
    }

//    public static void Func(Context context, String capkey, TextView view) {
////
////        setTextView(view);
////        setContext(context);
//
//        //初始化ASR
//        //构造Asr初始化的帮助类的实例
//        AsrInitParam asrInitParam = new AsrInitParam();
//        // 获取App应用中的lib的路径,放置能力所需资源文件。如果使用/data/data/packagename/lib目录,需要添加android_so的标记
//        String dataPath = context.getFilesDir().getAbsolutePath()
//                .replace("files", "lib");
//        asrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
//        asrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, AsrInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);
//        asrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, capkey);
////        Log.d(TAG,"HciAsrInit config :" + asrInitParam.getStringConfig());
//        int errCode = HciCloudAsr.hciAsrInit(asrInitParam.getStringConfig());
//        if (errCode != HciErrorCode.HCI_ERR_NONE) {
//            Log.d(TAG, "HciAsrInit error:" + HciCloudSys.hciGetErrorInfo(errCode));
//            return;
//        } else {
//            Log.d(TAG, "HciAsrInit Success");
//        }
//
//        AsrGrammarId grammarId = new AsrGrammarId();
//        if (capkey.equalsIgnoreCase("asr.local.grammar.v4")) {
//
//            AsrConfig loadConfig = new AsrConfig();
//            loadConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_TYPE, AsrConfig.GrammarConfig.VALUE_OF_PARAM_GRAMMAR_TYPE_JSGF);
//            loadConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_IS_FILE, AsrConfig.VALUE_OF_NO);
//            loadConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
//            byte[] grammarData = getAssetFileData("stock_10001.gram");
//            String strGrammarData = null;
//            try {
//                strGrammarData = new String(grammarData, "utf-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            errCode = HciCloudAsr.hciAsrLoadGrammar(loadConfig.getStringConfig(), strGrammarData, grammarId);
//            if (errCode != HciErrorCode.HCI_ERR_NONE) {
//                Log.d(TAG, "hciAsrLoadGrammar error:" + HciCloudSys.hciGetErrorInfo(errCode));
//                HciCloudAsr.hciAsrRelease();
//                return;
//            } else {
//                Log.d(TAG, "hciAsrLoadGrammar Success");
//            }
//        }
//
//        AsrConfig recogConfig = new AsrConfig();
//        if (grammarId.isValid()) {
//            recogConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_TYPE, AsrConfig.GrammarConfig.VALUE_OF_PARAM_GRAMMAR_TYPE_ID);
//            recogConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_ID, grammarId.toString());
//        }
//        //使用公有云能力asr.cloud.freetalk.music或asr.cloud.freetalk.poi时需要传相对应的domain参数
//        if (capkey.indexOf("asr.cloud.freetalk.music") != -1) {
//            recogConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_DOMAIN, "music");
//        }
//        if (capkey.indexOf("asr.cloud.freetalk.poi") != -1) {
//            recogConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_DOMAIN, "poi");
//        }
//
//        //私有云版本需根据需求配置property参数，详细见开发手册
//        //recogConfig.addParam(AsrConfig.ResultConfig.PARAM_KEY_PROPERTY, "chinese_8k_common ");
//
//        //若对录音文件识别时，vadtail设置为5000ms，可以减小语音分段影响，获得高的识别正确率率；
//        //若对麦克风录音在线识别时，vadtail设置为500ms（默认值），可以减小识别响应时间，尽快返回识别结果；
//        //recogConfig.addParam(AsrConfig.VadConfig.PARAM_KEY_VAD_TAIL, "5000");
//
//        String audioFile = "san_xia_shui_li_16k16.pcm";
//
//        //非实时识别
////        Recog(capkey,recogConfig,audioFile);
//
//        //非实时识别
////        RealtimeRecog(capkey, recogConfig, audioFile);
//
//        if (grammarId.isValid()) {
//            HciCloudAsr.hciAsrUnloadGrammar(grammarId);
////        	Log.d(TAG,"hciAsrUnloadGrammar");
//        }
//        //反初始化ASR
//        HciCloudAsr.hciAsrRelease();
////        Log.d(TAG,"hciAsrRelease");
//
//        return;
//    }

}
