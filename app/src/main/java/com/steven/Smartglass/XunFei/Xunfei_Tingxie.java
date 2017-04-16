package com.steven.Smartglass.XunFei;

import android.content.Context;
import android.os.Bundle;
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


public class Xunfei_Tingxie {

    private Context context;
    private TextView tv;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public Xunfei_Tingxie(final Context context, final TextView tv) {
        this.context = context;
        this.tv = tv;

        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(context, null);
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        //开始听写
        mIat.startListening(mRecoListener);

    }

    private void printResult(RecognizerResult results) {
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
        tv.setText(resultBuffer.toString());
    }

    //听写监听器
    final private RecognizerListener mRecoListener = new RecognizerListener() {
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
            Toast.makeText(context, "请开始说话", Toast.LENGTH_SHORT).show();
        }

        //结束录音
        public void onEndOfSpeech() {
            Toast.makeText(context, "说话结束", Toast.LENGTH_SHORT).show();
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

}

