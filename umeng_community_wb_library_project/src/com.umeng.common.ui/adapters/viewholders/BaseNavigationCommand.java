package com.umeng.common.ui.adapters.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.umeng.comm.core.utils.DeviceUtils;

/**
 * Created by pei on 16/7/26.
 */
public abstract class BaseNavigationCommand implements NavigationCommand {

    private transient Context mActivity;

    public BaseNavigationCommand(Activity activity) {
        this.mActivity = activity;
    }

    protected Context getContext() {
        if (mActivity == null) {
            mActivity = DeviceUtils.getContext();
        }
        return mActivity;
    }

    protected void startActivity(Intent intent) {
        // 处理恢复进程，activity为空的情况
        if (mActivity == null) {
            Context context = DeviceUtils.getContext();
            if (context == null) {
                return;
            }
            mActivity = context;
        }
        if (mActivity instanceof Activity) {
            mActivity.startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        }
    }

}
