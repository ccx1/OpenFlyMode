package com.example.suspension;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {


    private PIPManager mPIPManager;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private View mDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        mPIPManager = PIPManager.getInstance();
        mPIPManager.setActClass(this.getClass());
        mDD = findViewById(R.id.dd);
        mDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.doStartApplicationWithPackageName(MainActivity.this, "com.alibaba.android.rimet");
            }
        });

//        startService(new Intent(this,TaskService.class));
    }


    public void start(View view) {
        // 去home
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        mPIPManager.startFloatWindow();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        // 收到消息
        // 打开下一个.
        if (TestService.getService() != null) {
            TestService.getService().openFlyMode();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TestService.getService().closeFlyMode();
                    PIPManager.getInstance().openAct();
                    PIPManager.getInstance().openAct();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start(null);
                            mDD.performClick();
                        }
                    },1000);
                }
            }, 1500);
        }
    }


    public void close(View view) {
        mPIPManager.stopFloatWindow();
    }

    public void onAccessibleStart(View view) {
        if (!TestService.isStart()) {
            try {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
        } else {
            startService(new Intent(this, TestService.class));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}