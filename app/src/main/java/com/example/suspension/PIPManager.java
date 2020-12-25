package com.example.suspension;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

/**
 * 悬浮播放
 * Created by dueeeke on 2018/3/30.
 */

public class PIPManager {

    @SuppressLint("StaticFieldLeak")
    private static PIPManager instance;
    private final ControllerView mControllerView;
    private FloatView mFloatView;
    private boolean mIsShowing;
    private Class<? extends Activity> mActClass;


    private PIPManager() {
        // 控制器视图
        mControllerView = new ControllerView(App.getInstance());
        // 悬浮视图
        mFloatView = new FloatView(App.getInstance(), 0, 0);
    }

    public static PIPManager getInstance() {
        if (instance == null) {
            synchronized (PIPManager.class) {
                if (instance == null) {
                    instance = new PIPManager();
                }
            }
        }
        return instance;
    }


    public void openAct(){
        mControllerView.openAct();
    }

    public void startFloatWindow() {
        if (mIsShowing) return;
        mFloatView.addView(mControllerView);
        mFloatView.addToWindow();
        mIsShowing = true;
    }

    public void stopFloatWindow() {
        if (!mIsShowing) return;
        mFloatView.removeView(mControllerView);
        mFloatView.removeFromWindow();
        mIsShowing = false;
    }

    public boolean isStartFloatWindow() {
        return mIsShowing;
    }


    public void setActClass(Class<? extends Activity> cls) {
        this.mActClass = cls;
    }

    public Class<? extends Activity> getActClass() {
        return mActClass;
    }


}
