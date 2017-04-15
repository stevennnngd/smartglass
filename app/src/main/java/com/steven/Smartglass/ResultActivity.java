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
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.steven.Smartglass.FacePP.Facepplusplus;
import com.steven.Smartglass.Upload.Upload;

import java.io.File;


public class ResultActivity extends Activity {

    private Button upload;
    private Button back;
    private Button rcPic;
    private Button rcText;
    private Button rcFace;
    private Button voice;
    private static Handler facehandler;
    private static Handler uploadhandler;
    private String voicetext;

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
        final SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "nannan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端

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
                    tv.setText("" + msg.obj);
                    voicetext = msg.obj.toString();
                    System.out.println(voicetext);
                    try {
                        mTts.startSpeaking(voicetext, mSynListener);
                    }catch (Exception e){
                        System.out.println("声音出错");
                    }
                }
            }
        };

        facehandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    tv.setText("" + msg.obj);
                    voicetext = msg.obj.toString();
                    System.out.println(voicetext);
                    try {
                        mTts.startSpeaking(voicetext, mSynListener);
                    }catch (Exception e){
                        System.out.println("声音出错");
                    }
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

        voice = (Button) findViewById(R.id.voice);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTts.startSpeaking("hello world", mSynListener);
                }catch (Exception e){
                    System.out.println("声音出错");
                }
            }
        });
    }


    //合成监听器
    final private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
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

}