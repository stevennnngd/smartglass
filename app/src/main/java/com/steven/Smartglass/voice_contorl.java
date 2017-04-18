package com.steven.Smartglass;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class voice_contorl {

    String TTSmsg;

    public voice_contorl(String TTSmsg) {
        this.TTSmsg = TTSmsg;

        if (TTSmsg.equals("图像识别")) {
            new ResultActivity().faceplusplus("https://api-cn.faceplusplus.com/imagepp/beta/detectsceneandobject");
        } else if (TTSmsg.equals("人脸识别")) {
            new ResultActivity().faceplusplus("https://api-cn.faceplusplus.com/facepp/v3/detect");
        } else if (TTSmsg.equals("文字识别")) {
            new ResultActivity().faceplusplus("https://api-cn.faceplusplus.com/imagepp/beta/recognizetext");
        } else if (TTSmsg.equals("上传")) {
            new ResultActivity().upload();
        } else {
            return;
        }
    }
}
