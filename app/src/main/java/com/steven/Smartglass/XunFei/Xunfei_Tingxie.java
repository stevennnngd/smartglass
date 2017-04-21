package com.steven.Smartglass.XunFei;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import android.os.Handler;

import static com.steven.Smartglass.ResultActivity.TingxieMSGwhat;


public class Xunfei_Tingxie extends Thread {

    private Context context;
    private Handler handler;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public Xunfei_Tingxie(final Context context, Handler handler) {
        this.context = context;
        this.handler = handler;

        final SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(context, null);
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        mIat.setParameter(SpeechConstant.VAD_EOS, "2000");
        mIat.startListening(mRecoListener);
    }

    public void printResult(RecognizerResult results) {

        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(context, null);
        String text = TingxieJsonDeco.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        if (sn.equals("1")) {
            handler.obtainMessage(TingxieMSGwhat, resultBuffer.toString()).sendToTarget();
            /*Message msg = handler.obtainMessage();
            msg.obj = resultBuffer.toString();
            handler.sendMessage(msg);*/
        }else {
            mIat.stopListening();
            mIat.destroy();
        }

    }


    //听写监听器
    final public RecognizerListener mRecoListener = new RecognizerListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        @Override
        public void onError(SpeechError speechError) {
            Toast.makeText(context, speechError.getPlainDescription(true), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {
           // Toast.makeText(context, "请开始说话", Toast.LENGTH_SHORT).show();
        }

        //结束录音
        public void onEndOfSpeech() {

            //Toast.makeText(context, "说话结束", Toast.LENGTH_SHORT).show();
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

}

