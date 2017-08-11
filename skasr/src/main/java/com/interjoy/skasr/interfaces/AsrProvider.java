package com.interjoy.skasr.interfaces;

import android.content.Context;

import java.util.Map;

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
public interface AsrProvider {

    //初始化Asr模块
    void init(Context mContext);

    //切换Asr平台
    void changeAsr(Context mContext, Map<String, String> params);

    //设置监听
    void setAsrListener(AsrResultListener asrListener);
    //设置初始化监听

    void setAsrInitListener(AsrInitListener asrInitListener);

    //开启识别
    void start();

    //设置数据
    void setPcmData(byte data[], int size);

    //停止识别
    void stop();

    //销毁资源
    void destroy();

    //获取当前平台
    int getPlatform();

    //appid
    String getAppId();


    interface AsrResultListener {
        //识别成功的回调
        void success(String result);

        //识别失败的回调
        void error(String error);

    }

    interface AsrInitListener {
        //初始化成功
        void initSuccess();

        //初始化失败
        void initError(int code, String message);
    }

}
