package com.steven.Smartglass.XunFei;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class Xunfei_TTS {

    private Context context;
    private SpeechSynthesizer mTts;
    private String msg;
    private Handler handler;

    public Xunfei_TTS(final Context context, SpeechSynthesizer mTts, String msg, Handler handler) {
        this.context = context;
        this.mTts = mTts;
        this.msg = msg;
        this.handler = handler;

        mTts = SpeechSynthesizer.createSynthesizer(context, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "nannan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "75");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端

        mTts.startSpeaking(msg, mSynListener);
    }

    //合成监听器
    SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            if (error == null) {
                mTts.stopSpeaking();
                mTts.destroy();
                VoiceWakeuper mIvw = VoiceWakeuper.createWakeuper(context, null);
                mIvw.startListening(mWakeuperListener);
            }else {
                System.out.println("----------语音合成会话结束错误："+error);
            }
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };


    //听写监听器
    private WakeuperListener mWakeuperListener = new WakeuperListener() {
        public void onResult(WakeuperResult result) {
            String text = result.getResultString();
            System.out.println("----------------语音唤醒:" + text);
            Toast.makeText(context, "请说...", Toast.LENGTH_SHORT).show();
            Xunfei_Tingxie Tingxiethread = new Xunfei_Tingxie(context, handler);
            Tingxiethread.start();
        }

        public void onError(SpeechError error) {
        }

        public void onBeginOfSpeech() {
        }

        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
                //当使用唤醒+识别功能时获取识别结果
                //arg1:是否最后一个结果，1:是，0:否。
                RecognizerResult reslut = ((RecognizerResult) obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
            }
        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };
}


