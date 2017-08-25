package com.interjoy.skasr.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.interjoy.record.RecordProvider;
import com.interjoy.record.RecordProviderImpl;
import com.interjoy.skasr.impls.HciAsrImpl;
import com.interjoy.skasr.impls.IflytekAsrImpl;
import com.interjoy.skasr.interfaces.AsrProvider;
import com.interjoy.util.LogUtil;

import java.util.Map;

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
public class AsrManager implements AsrProvider {

    public static final int KEY_LING_YUN_TYPE = 0x00000001;// 灵云
    public static final int KEY_XUN_FEI_TYPE = 0x00000010;//讯飞
    public static final String KEY_ASR_SHARE_PRE = "KEY_ASR_SHARE_PRE";
    public static final String KEY_ASR_PLAT_TYPE = "KEY_ASR_PLAT_TYPE";
    private static final String TAG = "AsrManager";

    //asr识别模块
    private AsrProvider asrProvider;
    private RecordProvider recordProvider;
    private AsrResultListener asrResultListener;
    private AsrInitListener asrInitListener;
    private AsrInitListener changeAsrInitListener;
    private Context mContext;

    private volatile static AsrManager asrManager;
    private int startCount = 0;
    public int asrStatus = 0;//(0,没有初始化 1 初始化成功 2 初始化失败)


    public static AsrManager getInstance(Context mContext) {
        if (asrManager == null) {
            synchronized (AsrManager.class) {
                if (asrManager == null) {
                    asrManager = new AsrManager(mContext);
                }
            }
        }
        return asrManager;
    }


    private AsrManager(Context mContext) {
        this.mContext = mContext;
        recordProvider = new RecordProviderImpl();
        recordProvider.setRecordListener(new RecordProvider.RecordListener() {
            @Override
            public void recordDataListener(byte[] data, int size) {
                setPcmData(data, size);
//                LogUtil.d(TAG, "recordDataListener");
            }

            @Override
            public void recordStop() {
                LogUtil.d(TAG, "recordStop");
            }
        });
        changeAsrInitListener = new AsrInitListener() {
            @Override
            public void initSuccess() {
                Log.d(TAG, "initSuccess: ");
                asrStatus = 1;
                if (asrInitListener != null) {
                    asrInitListener.initSuccess();
                }
            }

            @Override
            public void initError(int code, String message) {
                Log.d(TAG, "initError: " + message);
                if (startCount > 2) {
                    startCount = 0;
                    return;
                }
                startCount++;
                resetAsr();
                asrStatus = 2;
                if (asrInitListener != null) {
                    asrInitListener.initError(code, message);
                }

            }
        };
//        init(mContext);
    }


    @Override
    public void init(Context mContext) {
        SharedPreferences mSharedPreferences =
                mContext.getSharedPreferences(KEY_ASR_SHARE_PRE, Activity.MODE_PRIVATE);
        int type = mSharedPreferences.getInt(KEY_ASR_PLAT_TYPE, -1);
        switch (type) {
            case KEY_LING_YUN_TYPE:
                //灵云
                asrProvider = new HciAsrImpl();
                break;
            case KEY_XUN_FEI_TYPE:
                //讯飞
                asrProvider = new IflytekAsrImpl();
                break;
            default:
                asrProvider = new HciAsrImpl();

        }
        asrProvider.setAsrInitListener(changeAsrInitListener);
        asrProvider.setAsrListener(asrResultListener);

        asrProvider.init(mContext);
        recordProvider.start();

    }

    //重启asr
    public void resetAsr() {
        recordProvider.stop();
        asrProvider.destroy();
        init(mContext);
    }

    @Override
    public String getAsrInfo() {
        if (asrProvider == null) {
            return "";
        }
        return asrProvider.getAsrInfo();
    }

    @Override
    public void changeAsr(Context mContext, Map<String, String> params) {
        if (params == null) {
            return;
        }
        int type = Integer.parseInt(params.get(KEY_ASR_PLAT_TYPE));
        recordProvider.stop();
        asrProvider.destroy();
        switch (type) {
            case KEY_LING_YUN_TYPE:
                asrProvider = new HciAsrImpl();
                asrProvider.setAsrListener(asrResultListener);
                asrProvider.setAsrInitListener(changeAsrInitListener);
                asrProvider.changeAsr(mContext, params);
                recordProvider.start();
                break;
            case KEY_XUN_FEI_TYPE:
                asrProvider = new IflytekAsrImpl();
                asrProvider.setAsrListener(asrResultListener);
                asrProvider.setAsrInitListener(changeAsrInitListener);
                asrProvider.changeAsr(mContext, params);
                recordProvider.start();
                break;
        }


    }

    @Override
    public void setAsrInitListener(AsrInitListener asrInitListener) {
        this.asrInitListener = asrInitListener;

    }

    //设置监听
    @Override
    public void setAsrListener(AsrResultListener asrListener) {
        asrResultListener = asrListener;
    }

    @Override
    public void start() {
        if (asrProvider == null) {
            return;
        }
        asrProvider.start();
    }

    @Override
    public void setPcmData(byte[] data, int size) {
        if (asrProvider == null) {
            return;
        }
        asrProvider.setPcmData(data, size);
    }

    @Override
    public void stop() {
        if (asrProvider == null) {
            return;
        }
        asrProvider.stop();

    }

    @Override
    public void destroy() {
        if (asrProvider == null) {
            return;
        }
        recordProvider.stop();
        asrProvider.destroy();
        mContext = null;
        asrManager = null;
        asrProvider = null;
    }

    @Override
    public int getPlatform() {
        if (asrProvider == null) {
            return -1;
        }
        return asrProvider.getPlatform();
    }

    @Override
    public String getAppId() {
        if (null == asrProvider) {
            return "";
        }
        return asrProvider.getAppId();
    }


    public boolean isRecording() {
        recordProvider.destroy();

        return false;
    }

    public void releaseRecord() {
        if (recordProvider != null) {
            recordProvider.stop();
        }
    }

    public void reStartRecord() {
        if (recordProvider != null) {
            recordProvider.start();
        }
    }

    //停止
    public boolean isDestroy() {
        return (asrProvider == null);
    }
}
