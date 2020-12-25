package com.example.suspension;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

public class TaskService extends Service {

    private TaskBind mTaskBind;

    public TaskService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mTaskBind = new TaskBind();
        return mTaskBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTaskBind == null) {
            synchronized (TaskService.class) {
                if (mTaskBind == null) {
                    mTaskBind = new TaskBind();
                }
            }
        }
        return START_STICKY;
    }

    public static class TaskBind extends ITaskAidlInterface.Stub {

        @Override
        public void complete() throws RemoteException {
            // 完成数据 进行下一项任务
            System.out.println("成功");
            // 通信.
            EventBus.getDefault().post(new MessageEvent());
        }

        @Override
        public String get() throws RemoteException {
            return "dsfajsfasdjfij";
        }
    }
}
