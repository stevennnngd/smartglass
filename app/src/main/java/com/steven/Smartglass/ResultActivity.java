package com.steven.Smartglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.steven.Smartglass.FacePP.Faceplusplus;
import com.steven.Smartglass.Upload.Upload;
import com.steven.Smartglass.XunFei.Xunfei_Tingxie;
import com.steven.Smartglass.XunFei.Xunfei_TTS;


import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class ResultActivity extends Activity {

    private Button upload;
    private Button back;
    private Button rcPic;
    private Button rcText;
    private Button rcFace;
    private Button voice;
    private TextView tv;
    private static Handler facehandler;
    private static Handler uploadhandler;
    private static Handler voicehandler;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        String path = getIntent().getStringExtra("picpath");
        ImageView imageView = (ImageView) findViewById(R.id.pic);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
        tv = (TextView) findViewById(R.id.textView);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        //初始化讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58f0e555");
        final SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);

        back = (Button) findViewById(R.id.back1);
        back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        ResultActivity.this.finish();
                                        mTts.stopSpeaking();
                                        mTts.destroy();
                                    }
                                }
        );

        uploadhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    String TTSmsg = msg.obj.toString();
                    tv.setText("" + TTSmsg);
                    try {
                        new Xunfei_TTS(context, mTts, TTSmsg);
                    } catch (Exception e) {
                        System.out.println("声音出错");
                    }
                }
            }
        };

        facehandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    String TTSmsg = msg.obj.toString();
                    tv.setText("" + TTSmsg);
                    try {
                        new Xunfei_TTS(context, mTts, TTSmsg);
                    } catch (Exception e) {
                        System.out.println("声音出错");
                    }
                }
            }
        };

        voicehandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    String TTSmsg = msg.obj.toString();
                    tv.setText("" + TTSmsg);
                    if (TTSmsg.equals("打开相机")) {
                        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                        startActivity(intent);
                        ResultActivity.this.finish();
                    } else
                        new voice_contorl(TTSmsg);
                }
            }
        };

        upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          upload();
                                      }
                                  }
        );


        rcPic = (Button) findViewById(R.id.rcPic);
        rcPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceplusplus("https://api-cn.faceplusplus.com/imagepp/beta/detectsceneandobject");
            }
        });


        rcText = (Button) findViewById(R.id.rcText);
        rcText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceplusplus("https://api-cn.faceplusplus.com/imagepp/beta/recognizetext");
            }
        });

        rcFace = (Button) findViewById(R.id.rcFace);
        rcFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceplusplus("https://api-cn.faceplusplus.com/facepp/v3/detect");
            }
        });


        voice = (Button) findViewById(R.id.voice);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Xunfei_Tingxie voicethread = new Xunfei_Tingxie(context, voicehandler);
                voicethread.start();
            }
        });

    }

    public void faceplusplus(String url) {
        File file = new File(Environment.getExternalStorageDirectory(), "temp.jpeg");
        if (!file.exists()) {
            System.out.println("rcPic pic not exist:" + file.getAbsolutePath());
            return;
        } else
            System.out.println("rcPic pic dir is:" + file.getAbsolutePath());
        Faceplusplus rcPicthread = new Faceplusplus(file, url, facehandler);
        rcPicthread.start();
    }


    public void upload() {
        File file = new File(Environment.getExternalStorageDirectory(), "temp.jpeg");
        if (!file.exists()) {
            System.out.println("pic not exist:" + file.getAbsolutePath());
            return;
        } else
            System.out.println("pic dir is:" + file.getAbsolutePath());
        Upload uploadthread = new Upload(file, uploadhandler);
        uploadthread.start();
    }

}




