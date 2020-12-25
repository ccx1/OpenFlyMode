package com.example.suspension;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ControllerView extends FrameLayout implements View.OnClickListener {
    private final Context mContext;
    private View mGoHome;

    public ControllerView(@NonNull Context context) {
        this(context, null);
    }

    public ControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_controller, this, true);
        mGoHome = findViewById(R.id.go_home);
        mGoHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        openAct();
    }


    public void openAct() {
        // 强行开启主界面.
        Intent intent = new Intent(App.getInstance(), PIPManager.getInstance().getActClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getInstance().startActivity(intent);
        PIPManager.getInstance().stopFloatWindow();
    }
}
