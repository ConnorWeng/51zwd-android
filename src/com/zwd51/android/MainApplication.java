package com.zwd51.android;

import android.app.Application;
import com.zwd51.android.model.TaobaoItem;

/**
 * Created by Connor on 5/31/14.
 */
public class MainApplication extends Application {
    private String accessToken;
    private TaobaoItem currentTaobaoItem = new TaobaoItem(null, "null item");

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public TaobaoItem getCurrentTaobaoItem() {
        return currentTaobaoItem;
    }

    public void setCurrentTaobaoItem(TaobaoItem currentTaobaoItem) {
        this.currentTaobaoItem = currentTaobaoItem;
    }
}
