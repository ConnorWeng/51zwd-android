package com.zwd51.android.model;

import android.util.Log;

/**
 * Created by Connor on 5/31/14.
 */
public class TaobaoItem {
    private static final String TAG = "TaobaoItem";
    private String id;

    public TaobaoItem(String id) {
        this.id = id;
    }

    public void upload() {
        Log.d(TAG, "current taobao item id:" + id);
    }
}
