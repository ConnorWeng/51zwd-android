package com.zwd51.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.taobao.top.android.TopAndroidClient;
import com.taobao.top.android.auth.*;
import com.zwd51.android.data.TaobaoItemProvider;
import com.zwd51.android.model.TaobaoItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AuthActivity {
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
        app = (MainApplication) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        bindEventListener();
    }

    @Override
    protected TopAndroidClient getTopAndroidClient() {
        return app.getAndroidClient();
    }

    @Override
    protected AuthorizeListener getAuthorizeListener() {
        return new AuthorizeListener() {
            @Override
            public void onComplete(AccessToken accessToken) {
                Log.d(TAG, "callbacked");
                String userId = accessToken.getAdditionalInformation().get(AccessToken.KEY_SUB_TAOBAO_USER_ID);
                if (userId == null) {
                    userId = accessToken.getAdditionalInformation().get(AccessToken.KEY_TAOBAO_USER_ID);
                }
                app.setUserId(Long.parseLong(userId));
                String nick = accessToken.getAdditionalInformation().get(AccessToken.KEY_SUB_TAOBAO_USER_NICK);
                if (nick == null) {
                    nick = accessToken.getAdditionalInformation().get(AccessToken.KEY_TAOBAO_USER_NICK);
                }
                app.setNick(nick);
                Log.d(TAG, "userId:" + userId);
                Log.d(TAG, "nick:" + nick);
            }

            @Override
            public void onError(AuthError e) {
                Log.e(TAG, e.getErrorDescription());
            }

            @Override
            public void onAuthException(AuthException e) {
                Log.e(TAG, e.getMessage());
            }
        };
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
                                if (app.getNick() == null) {
                                    app.getAndroidClient().authorize(MainActivity.this);
                                    MainActivity.this.finish();
                                } else {
                                    app.getCurrentTaobaoItem().setAuthBackActivity(MainActivity.this);
                                    app.getCurrentTaobaoItem().fillFieldsAndUpload();
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
