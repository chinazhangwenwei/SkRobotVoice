package com.interjoy.record;

import android.media.AudioFormat;
import android.media.AudioRecord;

import com.interjoy.util.LogUtil;


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

public class RecordProviderImpl implements RecordProvider {
    // 缓冲区字节大小  
    private int bufferSizeInBytes = 0;
    private AudioRecord audioRecord;
    private boolean isRecord = false;// 设置正在录制的状态



    private RecordListener recordListener;

    @Override
    public int start() {
        //判断是否有外部存储设备sdcard
        if (isRecord) {
            return E_STATE_RECODING;
        } else {
            if (audioRecord == null)
                createAudioRecord();
            audioRecord.startRecording();
            // 让录制状态为true
            isRecord = true;
            // 开启音频文件写入线程
            new Thread(new AudioRecordThread()).start();
            return SUCCESS;
        }
    }

    @Override
    public void stop() {
        destroy();
    }

    @Override
    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    @Override
    public void destroy() {
        if (audioRecord != null) {
            isRecord = false;//停止文件写入
            audioRecord.stop();
            audioRecord.release();//释放资源
            audioRecord = null;
        }
    }


    private void createAudioRecord() {

        // 获得缓冲区字节大小  
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        LogUtil.d(bufferSizeInBytes + "多大");
        audioRecord = new AudioRecord(AUDIO_INPUT,
                AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSizeInBytes);

    }


    class AudioRecordThread implements Runnable {
        @Override
        public void run() {
            int readSize = 0;
            byte[] audioData = new byte[bufferSizeInBytes];
            while (isRecord) {
                readSize = audioRecord.read(audioData, 0, bufferSizeInBytes);
                if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {
                    if (recordListener != null) {
                        recordListener.recordDataListener(audioData, readSize);
                    }
                }
            }
            recordListener.recordStop();
        }
    }


}