package com.interjoy.skrobotvoicedemo;

import android.app.Application;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/7/27
 */
public class MyApplication extends Application {
    private static MyApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
