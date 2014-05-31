package com.zwd51.android;

import android.os.Bundle;
import android.util.Log;
import com.taobao.top.android.TopAndroidClient;
import com.taobao.top.android.auth.*;

/**
 * Created by Connor on 5/31/14.
 */
public class AuthBackActivity extends AuthActivity {
    private static final String TAG = "AuthBackActivity";
    private Long userId;
    private String nick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected TopAndroidClient getTopAndroidClient() {
        return ((MainApplication) getApplicationContext()).getAndroidClient();
    }

    @Override
    protected AuthorizeListener getAuthorizeListener() {
        return new AuthorizeListener() {
            @Override
            public void onComplete(AccessToken accessToken) {
                Log.d(TAG, "callbacked");
                String id = accessToken.getAdditionalInformation().get(AccessToken.KEY_SUB_TAOBAO_USER_ID);
                if (id == null) {
                    id = accessToken.getAdditionalInformation().get(AccessToken.KEY_TAOBAO_USER_ID);
                }
                userId = Long.parseLong(id);
                nick = accessToken.getAdditionalInformation().get(AccessToken.KEY_SUB_TAOBAO_USER_NICK);
                if (nick == null) {
                    nick = accessToken.getAdditionalInformation().get(AccessToken.KEY_TAOBAO_USER_NICK);
                }
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
}
