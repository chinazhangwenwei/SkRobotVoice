package com.interjoy.voicechange.bean;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/8/23
 */
public class AsrInfo {
    private int platForm;
    private String platDes;
    public static final int KEY_LING_YUN_TYPE = 0x00000001;// 灵云
    public static final int KEY_XUN_FEI_TYPE = 0x00000010;//讯飞

    public static final String KEY_ASR_SHARE_PRE = "KEY_ASR_SHARE_PRE";
    public static final String KEY_ASR_PLAT_TYPE = "KEY_ASR_PLAT_TYPE";


    public static String KEY_HCI = "KEY_HCI";
    public static String KEY_HCI_DEVELOP = "KEY_HCI_DEVELOP";
    public static String KEY_HCI_COLUND_URL = "KEY_HCI_COLUND_URL";
    public static String KEY_XUN_APP_ID = "APP_FLY_ID";


    public int getPlatForm() {
        return platForm;
    }

    public void setPlatForm(int platForm) {
        this.platForm = platForm;
    }

    public String getPlatDes() {
        return platDes;
    }

    public void setPlatDes(String platDes) {
        this.platDes = platDes;
    }
}
