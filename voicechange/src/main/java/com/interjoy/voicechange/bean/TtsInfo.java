package com.interjoy.voicechange.bean;

/**
 * Title:
 * Description:
 * Company: 北京盛开互动科技有限公司
 * Tel: 010-62538800
 * Mail: support@interjoy.com.cn
 *
 * @author zhangwenwei
 * @date 2017/8/21
 */
public class TtsInfo {
    public static String KEY_HCI_TTS = "KEY_HCI_TTS";
    public static String KEY_HCI_DEVELOP_TTS = "KEY_HCI_DEVELOP_TTS";
    public static String KEY_HCI_COLUND_URL_TTS = "KEY_HCI_COLUND_URL_TTS";


    private String speaker;//说话者
    private String platDes;//平台描述
    private int platForm;//平台信息


    private int speakerPosition;//说话者的位置
    private int ttsPosition;//当前tts位置

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getPlatDes() {
        return platDes;
    }

    public void setPlatDes(String platDes) {
        this.platDes = platDes;
    }

    public int getSpeakerPosition() {
        return speakerPosition;
    }

    public void setSpeakerPosition(int speakerPosition) {
        this.speakerPosition = speakerPosition;
    }

    public int getPlatForm() {
        return platForm;
    }

    public void setPlatForm(int platForm) {
        this.platForm = platForm;
    }

    public int getTtsPosition() {
        return ttsPosition;
    }

    public void setTtsPosition(int ttsPosition) {
        this.ttsPosition = ttsPosition;
    }
}
