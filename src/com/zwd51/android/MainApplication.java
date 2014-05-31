package com.zwd51.android;

import android.app.Application;
import com.taobao.top.android.TopAndroidClient;

/**
 * Created by Connor on 5/31/14.
 */
public class MainApplication extends Application {
    private static final String appKey = "21794410";
    private static final String appSecret = "4e6b2aeaf906196804bea8e2584ee985";
    private static final String redirectURI = "com.zwd51://";
    private TopAndroidClient androidClient;

    @Override
    public void onCreate() {
        super.onCreate();
        registerAndroidClient();
    }

    private void registerAndroidClient() {
        TopAndroidClient.registerAndroidClient(getApplicationContext(), appKey, appSecret, redirectURI);
        androidClient = TopAndroidClient.getAndroidClientByAppKey(appKey);
    }

    public TopAndroidClient getAndroidClient() {
        return androidClient;
    }

    public void setAndroidClient(TopAndroidClient androidClient) {
        this.androidClient = androidClient;
    }
}