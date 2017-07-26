package com.interjoy.skrobotvoicedemo.skasr.result;

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
public interface ResultsListener {

    void onResult(String result);

    void onError(String error);
}
