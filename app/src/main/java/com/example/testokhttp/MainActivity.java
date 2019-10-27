package com.example.testokhttp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    public String str_wjm;

    private ProgressBar mprogressBar;
    private EditText editText;
    private ImageView image;
    private static final String TAG = "MainActivity";

    private Handler handler1 = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("提示");
                    builder1.setMessage("下载完成");
                    builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder1.show();
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mprogressBar = (ProgressBar) findViewById(R.id.progressbar);
        editText = (EditText) findViewById(R.id.edittext);
        image = (ImageView) findViewById(R.id.image);
    }

    public void download123(View view) {
        /**
         * 点击按钮弹出对话框，输入文件名下载文件
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("文件名");
        final View v = getLayoutInflater().inflate(R.layout.dialog,null);
        builder.setView(v);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edit_wj;
                edit_wj = (EditText) v.findViewById(R.id.wenjianming);
                str_wjm = edit_wj.getText().toString();

                //okhttputils下载大文件
                String url = editText.getText().toString();
                String exname = url.substring(url.lastIndexOf(".",url.length()));
                OkHttpUtils.get().url(url).build().execute(new FileCallBack("/storage/emulated/0/Test",str_wjm + exname)
                {

                    @Override
                    public void onBefore(Request request, int id)
                    {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id)
                    {
                        mprogressBar.setProgress((int) (100 * progress));
                        Log.e(TAG, "inProgress :" + (int) (100 * progress));
                        if((int)(100*progress) == 100){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    Message msg = new Message();
                                    msg.what = 1;
                                    handler1.handleMessage(msg);
                                    Looper.loop();
                                }
                            }).start();
                        }

                    }

                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        Log.e(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file, int id)
                    {
                        Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                    }
                });
            }
        });
        builder.show();



    }

    public void getimage(View view) {

        String url = "http://images.csdn.net/20150817/1.jpg";
        OkHttpUtils
                .get()//
                .url(url)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        System.out.println(e.toString());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int i) {
                        image.setImageBitmap(bitmap);
                    }
                });
    }
}
