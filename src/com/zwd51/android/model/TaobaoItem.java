package com.zwd51.android.model;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.taobao.top.android.api.ApiError;
import com.taobao.top.android.api.TopApiListener;
import com.zwd51.android.MainApplication;
import com.zwd51.android.api.TaobaoItemAdd;
import com.zwd51.android.api.TaobaoItemGet;
import com.zwd51.android.api.TaobaoItemImgUpload;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Connor on 5/31/14.
 */
public class TaobaoItem {
    private static final String TAG = "TaobaoItem";
    private MainApplication app;
    private String id;
    private Map<String, String> fields = new HashMap<String, String>();
    private String picUrl;
    private Activity activity;

    public TaobaoItem() {
    }

    public TaobaoItem(MainApplication app, String id) {
        this.app = app;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public void fillFieldsAndUpload() {
        Log.d(TAG, "current taobao item id:" + id);
        TaobaoItemGet.invoke(app.getAndroidClient(), app.getUserId(), id, new TopApiListener() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d(TAG, json.toString());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(app.getApplicationContext(), "获取宝贝信息成功", Toast.LENGTH_LONG).show();
                    }
                });
                try {
                    JSONObject itemJSONObject = json.getJSONObject("item_get_response").getJSONObject("item");
                    fields.put("title", itemJSONObject.getString("title"));
                    fields.put("price", itemJSONObject.getString("price"));
                    fields.put("desc", itemJSONObject.getString("desc"));
                    fields.put("property_alias", itemJSONObject.getString("property_alias"));
                    fields.put("cid", itemJSONObject.getString("cid"));
                    fields.put("props", makeProps(itemJSONObject.getString("props_name")));
                    fields.put("num", itemJSONObject.getString("num"));
                    fields.put("type", "fixed");
                    fields.put("stuff_status", "new");
                    fields.put("location.state", "广东");
                    fields.put("location.city", "广州");
                    fields.put("approve_status", "onsale");
                    fields.put("freight_payer", "seller");
                    fields.put("valid_thru", "14");
                    fields.put("has_voice", "true");
                    fields.put("has_warranty", "true");
                    fields.put("has_showcase", "false");
                    fields.put("has_discount", "false");
                    makeSkus(itemJSONObject.getJSONObject("skus").getJSONArray("sku"));
                    picUrl = itemJSONObject.getString("pic_url");
                    // TODO item_imgs
                    Log.d(TAG, itemJSONObject.getJSONObject("item_imgs").getJSONArray("item_img").toString());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            upload();
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onError(final ApiError error) {
                Log.e(TAG, error.getErrorCode() + error.getSubCode() + error.getMsg() + error.getSubMsg());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(app.getApplicationContext(), error.getErrorCode() + error.getSubCode() + error.getMsg() + error.getSubMsg(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

    private void upload() {
        Toast.makeText(app.getApplicationContext(), "开始上传宝贝", Toast.LENGTH_LONG).show();
        TaobaoItemAdd.invoke(app.getAndroidClient(), app.getUserId(), fields, new TopApiListener() {
            @Override
            public void onComplete(final JSONObject json) {
                Log.d(TAG, json.toString());
                try {
                    if (json.getJSONObject("item_add_response") != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(app.getApplicationContext(), "上传宝贝成功!", Toast.LENGTH_LONG).show();
                            }
                        });
                        TaobaoItemImgUpload.invoke(app.getAndroidClient(), app.getUserId(),
                                json.getJSONObject("item_add_response").getJSONObject("item").getString("num_iid"),
                                id, getImageBytes(), new TopApiListener() {
                                    @Override
                                    public void onComplete(JSONObject json) {
                                        Log.d(TAG, json.toString());
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(app.getApplicationContext(), "更新宝贝主图成功!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(final ApiError error) {
                                        Log.e(TAG, error.getErrorCode() + error.getSubCode() + error.getMsg() + error.getSubMsg());
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(app.getApplicationContext(), error.getErrorCode() + error.getSubCode() + error.getMsg() + error.getSubMsg(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onException(final Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(app.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                    } else if (json.getJSONObject("error_response") != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(app.getApplicationContext(), json.getJSONObject("error_response").getString("sub_msg"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onError(final ApiError error) {
                Log.e(TAG, error.getErrorCode() + error.getSubCode() + error.getMsg() + error.getSubMsg());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(app.getApplicationContext(), error.getErrorCode() + error.getSubCode() + error.getMsg() + error.getSubMsg(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onException(final Exception e) {
                Log.e(TAG, e.getMessage());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(app.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void makeSkus(JSONArray skus) {
        String skuProperties = "";
        String skuQuantites = "";
        String skuPrices = "";
        String skuOuterIds = "";
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
            fields.put("skuProperties", skuProperties);
            fields.put("skuPrices", skuPrices);
            fields.put("skuQuantites", skuQuantites);
            fields.put("skuOuterIds", skuOuterIds);
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

    public void setAuthBackActivity(Activity activity) {
        this.activity = activity;
    }

    private byte[] getImageBytes() {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = app.getAssets().open(id + ".jpg");
            outputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int ch;
            while ((ch = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, ch);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getImageBytes error:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
