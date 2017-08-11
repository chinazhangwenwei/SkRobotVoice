package com.interjoy.skrobotvoicedemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import com.interjoy.skrobotvoicedemo.util.LogUtil;

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
public class NetWorkReceiver extends BroadcastReceiver {
    /**
     * 当前处于的网络
     * 0 ：null
     * 1 ：2G/3G
     * 2 ：wifi
     */
    public static int networkStatus;
    public static final String NETWORKSTATE = "com.text.android.network.state"; // An action name
    public static final String NETWORK_KEY = "networkStatus";

    @Override
    public void onReceive(Context context, Intent intent) {
        // The action of this intent or null if none is specified.
        // action是行动的意思，也许是我水平问题无法理解为什么叫行动，我一直理解为标识（现在理解为意图）
        String action = intent.getAction(); //当前接受到的广播的标识(行动/意图)

        // 当当前接受到的广播的标识(意图)为网络状态的标识时做相应判断
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            // 获取网络连接管理器
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            // 获取当前网络状态信息
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {

                //当NetworkInfo不为空且是可用的情况下，获取当前网络的Type状态
                //根据NetworkInfo.getTypeName()判断当前网络
                String name = info.getTypeName();

                //更改NetworkStateService的静态变量，之后只要在Activity中进行判断就好了
                if (name.equals("WIFI")) {
                    networkStatus = 2;
                } else {
                    networkStatus = 1;
                }

            } else {
                // NetworkInfo为空或者是不可用的情况下
                networkStatus = 0;
            }
            Intent netIntent = new Intent(NETWORKSTATE);
            netIntent.putExtra(NETWORK_KEY, networkStatus);
            LogUtil.d("网络变化" + networkStatus);
            LocalBroadcastManager.getInstance(context).sendBroadcast(netIntent);
        }
    }


}
