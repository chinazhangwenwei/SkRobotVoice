package com.interjoy.record;

import android.media.MediaRecorder;

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
public interface RecordProvider {

    int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    int AUDIO_SAMPLE_RATE = 16000;  //44.1KHz,普遍使用的频率
    //成功
    int SUCCESS = 1000;
    //没有sd
    int E_NOSDCARD = 1001;
    //正在录音
    int E_STATE_RECODING = 1002;
    //未知错误
    int E_UNKOWN = 1003;

    int start();

    void stop();

    void destroy();

    void setRecordListener(RecordListener recordListener);

    interface RecordListener {
        void recordDataListener(byte data[], int size);

        void recordStop();
    }
}
