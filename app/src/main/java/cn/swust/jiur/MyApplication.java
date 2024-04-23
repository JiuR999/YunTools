package cn.swust.jiur;

import android.app.Application;

import cn.swust.jiur.utils.SharedPreferenceUtil;
import cn.swust.jiur.utils.mTrustManager;

public class MyApplication extends Application {
    private static MyApplication mApp;
    private int sCurrentTheme;

    public static MyApplication getInstance() {
        return mApp;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        initTheme();
        mTrustManager.handleSSLHandshake();
        mApp = this;

    }

    public void initTheme() {
        this.sCurrentTheme = (int) SharedPreferenceUtil.readData(this, SharedPreferenceUtil.Type.INT
                , "theme", "theme");
        setTheme(sCurrentTheme);
    }

    public void setsCurrentTheme(int theme){
        this.sCurrentTheme = theme;
    }
    public int getsCurrentTheme(){
        return this.sCurrentTheme;
    }
}
