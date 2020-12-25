package com.example.suspension;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.util.List;

public class TestService extends AccessibilityService {

    private static TestService sService;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private FlyStatus mFlyStatus = FlyStatus.NONE;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        sService = this;
        System.out.println("启动");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        //拿到根节点
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
        if (rootInfo == null) {
            return;
        }
        //开始找目标节点，这里拎出来细讲，直接往下看正文
        if (rootInfo.getChildCount() != 0) {
            if (TextUtils.isEmpty(rootInfo.getClassName())) {
                return;
            }
            checkFlyMode(rootInfo);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void checkFlyMode(AccessibilityNodeInfo rootInfo) {
        if (mFlyStatus != FlyStatus.NONE) {
            openFlyMode(rootInfo, mFlyStatus == FlyStatus.OPEN ? 0 : 1);
        }
    }

    /**
     * 0 为 关闭状态, 想要打开,
     * 1 为 开启状态, 想要关闭
     *
     * @param rootInfo
     * @param mode
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openFlyMode(AccessibilityNodeInfo rootInfo, int mode) {
        System.out.println("" + mode + "" + mFlyStatus);
        if ((Settings.Global.getInt(App.getInstance().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == mode)) {
            // 下拉.
            slide(500, 500, 0, 1500, 200L, 1000L);
            // 点击飞行模式.
            clickTargetTextView(rootInfo, "com.android.systemui:id/toggle", "飞行");
        } else {
            mFlyStatus = FlyStatus.NONE;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void clickTargetTextView(AccessibilityNodeInfo rootInfo, String viewId, String targetDescText) {
        List<AccessibilityNodeInfo> list = rootInfo.findAccessibilityNodeInfosByViewId(viewId);
        if (list.size() == 1) {
            performClick(getClickable(list.get(0)));
            return;
        }
        for (AccessibilityNodeInfo info : list) {
            // 查找指定的控件
            CharSequence contentDescription = info.getText();
            // 包含指定字段的info
            if (contentDescription.toString().contains(targetDescText)) {
                // 如果是飞行模式. 则点击, 先查找可用的. 可以点击的.
                performClick(getClickable(info));
                break;
            }
        }
    }

    //有些节点不可点击 点击交给父级甚至父级的父级...来做的。
    private AccessibilityNodeInfo getClickable(AccessibilityNodeInfo info) {
        if (info.isClickable()) {
            return info;//如果可以点击就返回
        } else {//不可点击就检查父级 一直递归
            return getClickable(info.getParent());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    // 查找需要的id
    private AccessibilityNodeInfo findByID(AccessibilityNodeInfo rootInfo, String text) {
        AccessibilityNodeInfo result = null;
        for (int i = 0; i < rootInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = rootInfo.getChild(i);
            if (child.findAccessibilityNodeInfosByViewId(text).size() > 0) {
                for (AccessibilityNodeInfo info : child.findAccessibilityNodeInfosByViewId(text)) {
                    return getClickable(info);
                }
            }
            result = findByID(child, text);//递归一直找一层层的全部遍历
        }
        return result;
    }

    private void performClick(AccessibilityNodeInfo info) {
        if (info != null) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void slide(int startX, int endX, int startY, int endY) {//仿滑动
        slide(startX, endX, startY, endY, 100L, 500L);
    }

    private void slide(int startX, int endX, int startY, int endY, long startTime, long continuedTime) {//仿滑动
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(startX, startY);//滑动起点
            path.lineTo(endX, endY);//滑动终点
            GestureDescription.Builder builder = new GestureDescription.Builder();
            //100L 第一个是开始的时间，第二个是持续时间
            GestureDescription description = builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, continuedTime)).build();
            dispatchGesture(description, new MyCallBack(), null);
        }
    }


    @Override
    public void onInterrupt() {
        System.out.println("中断");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("停止");
        sService = null;
    }


    /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return sService != null;
    }

    public void openFlyMode() {
        mFlyStatus = FlyStatus.OPEN;
        // 因为只是单纯的设置, 不会触发视图, 所以要强制触发一下
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            onAccessibilityEvent(null);
        }
    }

    public void closeFlyMode() {
        mFlyStatus = FlyStatus.CLOSE;
        // 因为只是单纯的设置, 不会触发视图, 所以要强制触发一下
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            onAccessibilityEvent(null);
        }
    }

    public static TestService getService() {
        return sService;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static class MyCallBack extends GestureResultCallback {
        public MyCallBack() {
            super();
        }

        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            super.onCompleted(gestureDescription);

        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            super.onCancelled(gestureDescription);

        }
    }
}
