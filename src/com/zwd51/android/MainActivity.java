package com.zwd51.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.taobao.top.android.api.WebUtils;
import com.zwd51.android.data.TaobaoItemProvider;
import com.zwd51.android.model.TaobaoItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private EditText editTextTaobaoItemId;
    private Button buttonUpload;
    private MainApplication app;
    private ListView listView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MainApplication) getApplicationContext();
        handleCallback();
        setContentView(R.layout.main);
        initView();
        bindEventListener();
    }

    private void handleCallback() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String fragment = uri.getFragment();
            if (fragment.split("=").length > 1) {
                String accessToken = uri.getFragment().split("=")[1];
                Log.d(TAG, "access_token:" + accessToken);
                app.setAccessToken(accessToken);
            }
        }
    }

    private void bindEventListener() {
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(R.string.upload)
                        .setMessage(R.string.really_upload)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "upload button clicked and taobaoItemId is " + editTextTaobaoItemId.getText());
                                app.setCurrentTaobaoItem(new TaobaoItem(app, editTextTaobaoItemId.getText().toString()));
                                if (app.getAccessToken() == null) {
                                    Toast.makeText(getApplicationContext(), "还未授权，请授权后回来重新上传", Toast.LENGTH_LONG).show();
                                    authorize();
                                    MainActivity.this.finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "准备上传，请稍候", Toast.LENGTH_LONG).show();
                                    try {
                                        app.getCurrentTaobaoItem().uploadViaWeb();
                                    } catch (IOException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });
    }

    private void initView() {
        editTextTaobaoItemId = (EditText) findViewById(R.id.editTextTaobaoItemId);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        listView = (ListView) findViewById(R.id.listView);
        TaobaoItemProvider itemProvider = new TaobaoItemProvider();
        listView.setAdapter(new ItemArrayAdapter(getApplicationContext(), R.layout.list_item, itemProvider.getData()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TaobaoItem item = (TaobaoItem) parent.getItemAtPosition(position);
                editTextTaobaoItemId.setText(item.getId());
                buttonUpload.performClick();
            }
        });
    }

    public void authorize() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("response_type", "code");
        params.put("client_id", "21357243");
        params.put("redirect_uri", "http://121.196.142.10/index.php");
        params.put("state", "android");
        params.put("view", "web");
        String str = "";
        try {
            URL url = WebUtils.buildGetUrl("https://oauth.taobao.com/authorize", params, null);
            str = url.toString();
            Log.d(TAG, "auth uri:" + str);
        } catch (IOException e) {
            throw new RuntimeException(e);// won't happen
        }
        Uri uri = Uri.parse(str);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        try {
            ComponentName name = new ComponentName("com.android.browser",
                    "com.android.browser.BrowserActivity");
            // 判断系统自带浏览器是否安装
            getApplicationContext().getPackageManager().getActivityInfo(name,
                    PackageManager.GET_INTENT_FILTERS);
            it.setComponent(name);
        } catch (Exception e) {
            /*
             * if an activity with the given class name can not be found on the
			 * system
			 */
            Log.e(TAG, e.getMessage(), e);
        }
        this.startActivity(it);
    }

    private class ItemArrayAdapter extends ArrayAdapter<TaobaoItem> {
        public ItemArrayAdapter(Context context, int textViewResourceId, List<TaobaoItem> items) {
            super(context, textViewResourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaobaoItem item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            GetRemotePictureTask getRemotePictureTask = new GetRemotePictureTask();
            getRemotePictureTask.execute(new ImageItem(item.getPicUrl(), imageView));
            TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
            titleTextView.setText(item.getFields().get("title"));
            TextView secondLineTextView = (TextView) convertView.findViewById(R.id.secondLine);
            secondLineTextView.setText(item.getFields().get("price"));
            return convertView;
        }

        private Bitmap getBitmapFromUrl(String url) {
            URL myFileUrl = null;
            Bitmap bitmap = null;
            try {
                myFileUrl = new URL(url);
            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
            }
            try {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }

        private class GetRemotePictureTask extends AsyncTask<ImageItem, Integer, Bitmap> {
            private ImageView imageView;

            @Override
            protected Bitmap doInBackground(ImageItem... params) {
                this.imageView = params[0].imageView;
                return getBitmapFromUrl(params[0].picUrl);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        }

        private class ImageItem {
            public String picUrl;
            public ImageView imageView;

            public ImageItem(String picUrl, ImageView imageView) {
                this.picUrl = picUrl;
                this.imageView = imageView;
            }
        }
    }
}
