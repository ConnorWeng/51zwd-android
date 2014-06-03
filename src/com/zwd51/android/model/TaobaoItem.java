package com.zwd51.android.model;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.taobao.top.android.api.ApiError;
import com.taobao.top.android.api.TopApiListener;
import com.zwd51.android.MainApplication;
import com.zwd51.android.api.TaobaoItemAdd;
import com.zwd51.android.api.TaobaoItemGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public TaobaoItem(MainApplication app, String id) {
        this.app = app;
        this.id = id;
    }

    public void fillFieldsAndUpload() {
        Log.d(TAG, "current taobao item id:" + id);
        TaobaoItemGet.invoke(app.getAndroidClient(), app.getUserId(), id, new TopApiListener() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d(TAG, json.toString());
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
            public void onError(ApiError error) {
                Log.e(TAG, error.getErrorCode() + error.getSubCode() + error.getMsg() + error.getSubMsg());
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

    private void upload() {
        UploadTask task = new UploadTask();
        File file = new File(app.getFilesDir(), System.currentTimeMillis() + ".jpg");
        task.execute(picUrl, file.getAbsolutePath());
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

    private class UploadTask extends AsyncTask<String, Integer, String> {
        private String outputPath;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                String downloadUrl = params[0];
                URL url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {

                }
                int fileLength = connection.getContentLength();
                inputStream = connection.getInputStream();
                outputPath = params[1];
                outputStream = new FileOutputStream(outputPath);
                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    if (isCancelled()) {
                        inputStream.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0) publishProgress((int) (total * 100 / fileLength));
                    outputStream.write(data, 0, count);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return e.getMessage();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ignored) {
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(app.getApplicationContext(), "Upload item picture:" + result, Toast.LENGTH_LONG);
            } else {
                Log.d(TAG, "download picture successfully:" + outputPath);
                TaobaoItemAdd.invoke(app.getAndroidClient(), app.getUserId(), fields, outputPath, new TopApiListener() {
                    @Override
                    public void onComplete(JSONObject json) {
                        Log.d(TAG, json.toString());
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
    }
}
