package com.interjoy.skrobotvoicedemo.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.List;

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
public class Utils {
    private static final String VERSION = "1.0.1";

    private static final int LOG_NO = 0;
    private static final int LOG_E = 1;
    private static final int LOG_W = 2;
    private static final int LOG_D = 3;
    private static final int LOG_I = 4;

    private static final int LOG_LEVEL = LOG_I;

    public static void logi(String tag, String msg) {
        if (LOG_LEVEL == LOG_I) {
            Log.i(tag, "[" + VERSION + "]" + msg);
        }
    }

    public static void logd(String tag, String msg) {
        if (LOG_LEVEL >= LOG_D) {
            Log.d(tag, "[" + VERSION + "]" + msg);
        }
    }

    public static void logw(String tag, String msg) {
        if (LOG_LEVEL >= LOG_W) {
            Log.w(tag, "[" + VERSION + "]" + msg);
        }
    }

    public static void loge(String tag, String msg) {
        if (LOG_LEVEL >= LOG_E) {
            Log.e(tag, "[" + VERSION + "]" + msg);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     * <p>
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     * <p>
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     *
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
