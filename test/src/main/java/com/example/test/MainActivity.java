package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

import com.example.suspension.ITaskAidlInterface;

public class MainActivity extends AppCompatActivity {

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("连接成功");
            mITaskAidlInterface = ITaskAidlInterface.Stub.asInterface(service);

            try {
                System.out.println( mITaskAidlInterface.get());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("连接超时");
        }
    };
    private ITaskAidlInterface mITaskAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View view) {
        Intent intent = new Intent("com.example.suspension.TaskService");
        intent.setPackage("com.example.suspension");
        bindService(intent, conn, BIND_AUTO_CREATE);


        
    }

    public void open(View view) {
        if (mITaskAidlInterface == null) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            mITaskAidlInterface.complete();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
