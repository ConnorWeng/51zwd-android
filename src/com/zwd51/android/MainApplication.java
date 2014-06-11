package com.zwd51.android;

import android.app.Application;
import com.taobao.top.android.TopAndroidClient;
import com.zwd51.android.model.TaobaoItem;

/**
 * Created by Connor on 5/31/14.
 */
public class MainApplication extends Application {
    //* production
    private static final String appKey = "21779926";
    private static final String appSecret = "e46e5711fb61913f05e67601c6b71f51";
    //*/
    /* test
    private static final String appKey = "21795154";
    private static final String appSecret = "ffce9b10e71970dacdd3cca2c7b2e0e3";
    //*/
    /* 51mobile
    private static final String appKey = "21800188";
    private static final String appSecret = "cd3b76c6d1ce45c99d140db522704894";
    //*/
    private static final String redirectURI = "com.zwd51://";
    private TopAndroidClient androidClient;
    private Long userId;
    private String nick;
    private TaobaoItem currentTaobaoItem = new TaobaoItem(null, "null item");

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

    public TaobaoItem getCurrentTaobaoItem() {
        return currentTaobaoItem;
    }

    public void setCurrentTaobaoItem(TaobaoItem currentTaobaoItem) {
        this.currentTaobaoItem = currentTaobaoItem;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
