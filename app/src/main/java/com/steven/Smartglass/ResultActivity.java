package com.steven.Smartglass;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.steven.Smartglass.FacePP.Facepplusplus;
import com.steven.Smartglass.Upload.Upload;

import java.io.File;


public class ResultActivity extends Activity {

    private Button upload;
    private Button back;
    private Button rcPic;
    private Button rcText;
    private Button rcFace;
    private static Handler facehandler;
    private static Handler uploadhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        String path = getIntent().getStringExtra("picpath");
        ImageView imageView = (ImageView) findViewById(R.id.pic);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
        final TextView tv = (TextView) findViewById(R.id.textView);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        //初始化讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=58f0e555");

        back = (Button) findViewById(R.id.back1);
        back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        ResultActivity.this.finish();
                                    }
                                }
        );

        uploadhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    tv.setText("" + msg.obj);
                }
            }
        };

        facehandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    tv.setText("" + msg.obj);
                }
            }
        };

        upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          File file = new File(Environment.getExternalStorageDirectory(), "temp.jpeg");
                                          if (!file.exists()) {
                                              System.out.println("pic not exist:" + file.getAbsolutePath());
                                              return;
                                          } else
                                              System.out.println("pic dir is:" + file.getAbsolutePath());
                                          String remind = "    正在上传，请稍等...";
                                          tv.setText(remind);
                                          Upload thread2 = new Upload(file, uploadhandler);
                                          thread2.start();
                                      }
                                  }
        );

        rcPic = (Button) findViewById(R.id.rcPic);
        rcPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory(), "temp.jpeg");
                if (!file.exists()) {
                    System.out.println("rcPic pic not exist:" + file.getAbsolutePath());
                    return;
                } else
                    System.out.println("rcPic pic dir is:" + file.getAbsolutePath());
                String url = "https://api-cn.faceplusplus.com/imagepp/beta/detectsceneandobject";
                String remind = "    正在识别，请稍等...";
                tv.setText(remind);
                Facepplusplus thread3 = new Facepplusplus(file, url, facehandler);
                thread3.start();
            }
        });


        rcText = (Button) findViewById(R.id.rcText);
        rcText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory(), "temp.jpeg");
                if (!file.exists()) {
                    System.out.println("rcText pic not exist:" + file.getAbsolutePath());
                    return;
                } else
                    System.out.println("rcText pic dir is:" + file.getAbsolutePath());
                String url = "https://api-cn.faceplusplus.com/imagepp/beta/recognizetext";
                String remind = "    正在识别，请稍等...";
                tv.setText(remind);
                Facepplusplus thread4 = new Facepplusplus(file, url, facehandler);
                thread4.start();
            }
        });

        rcFace = (Button) findViewById(R.id.rcFace);
        rcFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory(), "temp.jpeg");
                if (!file.exists()) {
                    System.out.println("rcFace pic not exist:" + file.getAbsolutePath());
                    return;
                } else
                    System.out.println("rcFace pic dir is:" + file.getAbsolutePath());
                String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";
                String remind = "    正在识别，请稍等...";
                tv.setText(remind);
                Facepplusplus thread5 = new Facepplusplus(file, url, facehandler);
                thread5.start();
            }
        });
    }
}