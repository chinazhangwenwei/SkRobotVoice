package com.interjoy.skrobotvoicedemo.sktts;

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
public class TtsConstant {

    public static final int EVENT_SYNTHESIZE_START = 1110;//合成开始
    public static final int EVENT_SYNTHESIZE_PROCESS = 1111;//正在合成
    public static final int EVENT_SYNTHESIZE_END = 1112;//合成结束
    public static final int EVENT_SPEECH_START = 1113;//语音开始
    public static final int EVENT_SPEECH_PROCESS = 1114;//正在语音
    public static final int EVENT_SPEECH_END = 1115;//语音结束
    public static final int EVENT_SPEECH_PAUSE = 1116;//语音暂停
    public static final int EVENT_SPEECH_RESUME = 1117;//语音继续

    public static final int EVENT_INIT_SUCCEED = 1140;//初始化成功
    public static final int EVENT_CHECK_AUTH_SUCCEED = 1141;//授权检测成功

    /**
     * Initializer error
     */
    public static final int ERROR_INIT = 1200;//初始化失败
    public static final int ERROR_INTERNAL_STATE = 1201;//内部状态错误
    public static final int ERROR_SPEECH = 1202;//语音出错

    public static final String BAIDU_OFFLINE_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";//离线女语音模型
    public static final String BAIDU_OFFLINE_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";//离线男语音模型
    public static final String BAIDU_OFFLINE_TEXT_MODEL_NAME = "bd_etts_text.dat";//离线文本模型


//    TtsBean.SPEAKER_BAIDU_FEMALE_NORMAL,TtsBean.SPEAKER_BAIDU_MALE_NORMAL,TtsBean.SPEAKER_BAIDU_MALE_SPECIAL,
//    TtsBean.SPEAKER_BAIDU_MALE_EMOTIONAL, TtsBean.SPEAKER_BAIDU_CHILD_EMOTIONAL,TtsBean.SPEAKER_HCICLOUD_FEMALE_WANGJING, TtsBean.SPEAKER_HCICLOUD_FEMALE_XIAOKUN,
//    TtsBean.SPEAKER_HCICLOUD_FEMAL_XIXI, TtsBean.SPEAKER_HCICLOUD_MALE_HAOBO

}

