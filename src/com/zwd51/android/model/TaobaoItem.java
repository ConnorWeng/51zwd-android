package com.zwd51.android.model;

import android.util.Log;
import com.taobao.top.android.api.ApiError;
import com.taobao.top.android.api.TopApiListener;
import com.zwd51.android.MainApplication;
import com.zwd51.android.api.TaobaoItemGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Connor on 5/31/14.
 */
public class TaobaoItem {
    private static final String TAG = "TaobaoItem";
    private MainApplication app;
    private String id;
    private String title;
    private String price;
    private String desc;
    private String propertyAlias;
    private Long cid;
    private String props;
    private String picUrl;
    private String skuProperties = "";
    private String skuQuantites = "";
    private String skuPrices = "";
    private String skuOuterIds = "";


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
                    title = itemJSONObject.getString("title");
                    price = itemJSONObject.getString("price");
                    desc = itemJSONObject.getString("desc");
                    propertyAlias = itemJSONObject.getString("property_alias");
                    cid = itemJSONObject.getLong("cid");
                    props = makeProps(itemJSONObject.getString("props_name"));
                    picUrl = itemJSONObject.getString("pic_url");
                    makeSkus(itemJSONObject.getJSONObject("skus").getJSONArray("sku"));
                    // TODO item_imgs
                    Log.d(TAG, itemJSONObject.getJSONObject("item_imgs").getJSONArray("item_img").toString());
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

    private void makeSkus(JSONArray skus) {
        try {
            for (int i = 0; i < skus.length(); i++) {
                JSONObject sku = skus.getJSONObject(i);
                skuProperties += sku.getString("properties") + ",";
                skuPrices += sku.getString("price") + ",";
                skuQuantites += sku.getString("quantity") + ",";
                skuOuterIds += ",";
            }
            if (skuProperties.length() > 0) {
                skuProperties = skuProperties.substring(0, skuProperties.length() - 1);
                skuPrices = skuPrices.substring(0, skuPrices.length() - 1);
                skuQuantites = skuQuantites.substring(0, skuQuantites.length() - 1);
                skuOuterIds = skuOuterIds.substring(0, skuOuterIds.length() - 1);
            }
            Log.d(TAG, skuProperties);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private String makeProps(String propsName) {
        String[] parts = propsName.split(";");
        StringBuffer sb = new StringBuffer();
        for (String part : parts) {
            String[] sparts = part.split(":");
            sb.append(sparts[0] + ":" + sparts[1] + ";");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
