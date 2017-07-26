package com.interjoy.skrobotvoicedemo.skasr;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/7/26
 */
public class AsrConstant {

    //识别结果
    public static final int RESULT_NET = 1001;//网络识别
    public static final int RESULT_LOCAL = 1002;//本地识别
    public static final int RESULT_PROCESS = 1002;//中间识别结果

    //事件
    public static final int EVENT_RECORDING_START = 1101;//录音设备打开
    public static final int EVENT_RECORDING_STOP = 1102;//说话结束
    public static final int EVENT_VAD_TIMEOUT = 1103;//端点超时
    public static final int EVENT_SPEECH_DETECTED = 1104;//检测到说话
    public static final int EVENT_SPEECH_END = 1105;//检测到说话结束
    public static final int EVENT_RECOGNIZE_START = 1106;//识别开始
    public static final int EVENT_RECOGNIZE_END = 1107;//识别结束
    public static final int EVENT_RECOGNIZE_PROCESS = 1108;//识别开始
    public static final int EVENT_USERDATA_UPLOADED = 1109;//用户信息更新
    public static final int EVENT_VOCAB_INSERTED = 1112;
    public static final int EVENT_NO_VOICE_INPUT = 1113;//无声音输入
    public static final int EVENT_ERROR = 1114;//错误
    public static final int EVENT_ENGINE_SWITCH = 1115;//切换引擎
    public static final int EVENT_NET_END = 1119;//网路识别结束
    public static final int EVENT_VOLUMECHANGE = 1122;//音量改变
    public static final int EVENT_COMPILE_DONE = 1123;
    public static final int EVENT_ENGINE_INIT_DONE = 1129;
    public static final int EVENT_LOADGRAMMAR_DONE = 1130;

    public static final int EVENT_INIT_SUCCEED = 1140;//初始化成功
    public static final int EVENT_CHECK_AUTH_SUCCEED = 1141;//授权检测成功


    /**
     * Initializer error
     */
    public static final int ERROR_INIT = 1200;//初始化失败

    /**
     * Error is null
     */
    public static final int ERROR_NULL = 1201;//错误为空

    /**
     * State error
     */
    public static final int ERROR_STATE_WRONG = 1202;//状态错误

    /**
     * Network operation timed out.
     */
    public static final int ERROR_NETWORK_TIMEOUT = 1203;

    /**
     * Other network related errors.
     */
    public static final int ERROR_NETWORK = 1204;

    /**
     * Audio recording error.
     */
    public static final int ERROR_AUDIO = 1205;

    /**
     * Server sends error status.
     */
    public static final int ERROR_SERVER = 1206;

    /**
     * Other client side errors.
     */
    public static final int ERROR_CLIENT = 1207;

    /**
     * No speech input
     */
    public static final int ERROR_SPEECH_TIMEOUT = 1208;

    /**
     * No recognition result matched.
     */
    public static final int ERROR_NO_MATCH = 1209;

    /**
     * RecognitionService busy.
     */
    public static final int ERROR_RECOGNIZER_BUSY = 1210;

    /**
     * Insufficient permissions
     */
    public static final int ERROR_INSUFFICIENT_PERMISSIONS = 1211;
}
