package com.dc.ddu;

import android.app.Application;
import android.content.Context;

import com.dc.ddu.utils.CrashHandler;
import com.dc.ddu.utils.Foreground;
import com.dc.ddu.utils.Util;

/**
 * Created by Johan on 2016/10/24.
 */
public class DDUApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
    }

}
