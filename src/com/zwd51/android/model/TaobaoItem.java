package com.zwd51.android.model;

import android.util.Log;
import com.taobao.top.android.api.ApiError;
import com.taobao.top.android.api.TopApiListener;
import com.zwd51.android.MainApplication;
import com.zwd51.android.api.TaobaoItemGet;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Connor on 5/31/14.
 */
public class TaobaoItem {
    private static final String TAG = "TaobaoItem";
    private MainApplication app;
    private String id;

    public TaobaoItem(MainApplication app, String id) {
        this.app = app;
        this.id = id;
    }

    public void fillFields() {
        Log.d(TAG, "current taobao item id:" + id);
        TaobaoItemGet.invoke(app.getAndroidClient(), app.getUserId(), id, new TopApiListener() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d(TAG, json.toString());
                try {
                    JSONObject itemJSONObject = json.getJSONObject("item_get_response").getJSONObject("item");
                    String title = itemJSONObject.getString("title");
                    Log.d(TAG, "title is " + title);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onError(ApiError error) {
                Log.e(TAG, error.getErrorCode() + error.getSubCode() + error.getMsg() + error.getSubMsg());
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }
}
