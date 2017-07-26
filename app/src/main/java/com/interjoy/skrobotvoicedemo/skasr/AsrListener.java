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
public interface AsrListener {

    void onError(int type, String errorMsg);

    void onEvent(int type, String param);

    void onResult(int type, String msg);

}
