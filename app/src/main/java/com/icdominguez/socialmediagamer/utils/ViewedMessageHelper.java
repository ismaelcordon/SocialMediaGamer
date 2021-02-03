package com.icdominguez.socialmediagamer.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;

import java.util.List;

public class ViewedMessageHelper {

    public static void updateOnline(boolean status, final Context context) {
        UserProvider userProvider = new UserProvider();
        AuthProvider authProvider = new AuthProvider();
        if(authProvider.getUid() != null) {
            if(isApplicationSentToBackground(context)) {
                userProvider.updateUserOnline(authProvider.getUid(), status);
            } else if(status) {
                userProvider.updateUserOnline(authProvider.getUid(), status);
            }
        }
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if(!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if(!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
