package com.zwd51.android.model;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.zwd51.android.MainApplication;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void uploadViaWeb() throws IOException {
        UploadViaWebTask task = new UploadViaWebTask();
        task.execute();
    }

    private class UploadViaWebTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... parameters) {
            HttpClient client = new DefaultHttpClient();
            String postUrl = "http://yjsc.51zwd.com/taobao-upload-multi-store/index.php?g=Taobao&m=Upload&a=uploadItemFromAndroid";
            HttpPost post = new HttpPost(postUrl);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("taobaoItemId", id));
            params.add(new BasicNameValuePair("access_token", app.getAccessToken()));
            BufferedReader br = null;
            try {
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse response = client.execute(post);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    InputStream is = resEntity.getContent();
                    br = new BufferedReader(new InputStreamReader(is));
                    String line = br.readLine();
                    Log.d(TAG, "response line:" + line);
                    return line;
                }
            } catch (IOException e) {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e1) {
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                String displayText = "上传宝贝失败!";
                if (result.equals("\"true\"")) {
                    displayText = "上传宝贝成功!";
                }
                Toast.makeText(app.getApplicationContext(), displayText, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(app.getApplicationContext(), "上传出错!请稍候再试!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
