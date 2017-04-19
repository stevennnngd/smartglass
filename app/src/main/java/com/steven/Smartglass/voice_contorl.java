package com.steven.Smartglass;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.iflytek.cloud.SpeechSynthesizer;
import com.steven.Smartglass.XunFei.Xunfei_TTS;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class voice_contorl {

    String TTSmsg;
    Context context;

    public voice_contorl(String TTSmsg, Context context) {
        this.TTSmsg = TTSmsg;
        this.context = context;

        if (TTSmsg.equals("图像识别")) {
            Toast.makeText(context, "正在识别，请稍等...", Toast.LENGTH_SHORT).show();
            new ResultActivity().faceplusplus("https://api-cn.faceplusplus.com/imagepp/beta/detectsceneandobject");
        } else if (TTSmsg.equals("人脸识别")) {
            Toast.makeText(context, "正在识别，请稍等...", Toast.LENGTH_SHORT).show();
            new ResultActivity().faceplusplus("https://api-cn.faceplusplus.com/facepp/v3/detect");
        } else if (TTSmsg.equals("文字识别")) {
            Toast.makeText(context, "正在识别，请稍等...", Toast.LENGTH_SHORT).show();
            new ResultActivity().faceplusplus("https://api-cn.faceplusplus.com/imagepp/beta/recognizetext");
        } else if (TTSmsg.equals("上传")) {
            Toast.makeText(context, "正在上传，请稍等...", Toast.LENGTH_SHORT).show();
            new ResultActivity().upload();
        } else {
            SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
            new Xunfei_TTS(context, mTts, TTSmsg);
        }
    }
}
