package com.interjoy.skrobotvoicedemo.util;

import android.util.Log;

import com.interjoy.skrobotvoicedemo.BuildConfig;

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
public class LogUtil {
    private static final boolean IS_DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "DEBUG";

    public static void d(String tag, String content) {
        if (IS_DEBUG) {
            Log.d(tag, content);
        }
    }

    public static void d(String content) {
        d(TAG, content);
    }



}
