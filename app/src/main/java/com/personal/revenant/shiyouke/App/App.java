package com.personal.revenant.shiyouke.App;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

/**
 * Created by Administrator on 2018/8/23.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.getInstance().init(this);
//        if (BuildConfig.DEBUG) {
//            UMConfigure.setLogEnabled(true);
//        }
//        UMConfigure.init(this, "5b7d6a47a40fa3323500000e", "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
//                "");
//        PlatformConfig.setWeixin(Constant.APP_ID, Constant.SECRET);
    }
}
