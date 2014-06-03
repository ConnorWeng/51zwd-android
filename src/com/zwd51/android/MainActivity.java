package com.zwd51.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.taobao.top.android.TopAndroidClient;
import com.taobao.top.android.auth.*;
import com.zwd51.android.model.TaobaoItem;

public class MainActivity extends AuthActivity {
    private static final String TAG = "MainActivity";
    private EditText editTextTaobaoItemId;
    private Button buttonUpload;
    private MainApplication app;

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
        });
    }

    private void initView() {
        editTextTaobaoItemId = (EditText) findViewById(R.id.editTextTaobaoItemId);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
    }
}
