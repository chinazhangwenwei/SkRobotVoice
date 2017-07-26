package com.interjoy.skrobotvoicedemo.constant;

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
public class BaseParams {
    public static final String EXTRA_APPID = "appid";//开放平台创建应用后分配，设置后会覆盖 manifest 中配置的 APP_ID
    public static final String EXTRA_KEY = "key";//开放平台创建应用后分配，设置后会覆盖 manifest 中配置的 API_KEY
    public static final String EXTRA_SECRET = "secret";//开放平台创建应用后分配，设置后会覆盖 manifest 中配置的SECRET_KEY
    public static final String EXTRA_SAMPLE = "sample";//采样率
    public static final String EXTRA_INFILE = "infile";//音频源
    public static final String EXTRA_OUTFILE = "outfile";//保存识别过程中产生的文件
    public static final String EXTRA_GRAMMAR = "grammar";//离线识别语法路径
    public static final String EXTRA_LANGUAGE = "language";//语种
    public static final String EXTRA_VAD = "vad";//语音活动检测

    public static  int SAMPLE_8K = 8000;
    public static  int SAMPLE_16K = 16000;
}
