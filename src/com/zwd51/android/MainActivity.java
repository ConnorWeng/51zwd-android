package com.zwd51.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.zwd51.android.model.TaobaoItem;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private EditText editTextTaobaoItemId;
    private Button buttonUpload;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        bindEventListener();
    }

    private void bindEventListener() {
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "upload button clicked and taobaoItemId is " + editTextTaobaoItemId.getText());
                MainApplication app = (MainApplication) getApplicationContext();
                app.setCurrentTaobaoItem(new TaobaoItem(editTextTaobaoItemId.getText().toString()));
                app.getAndroidClient().authorize(MainActivity.this);
            }
        });
    }

    private void initView() {
        editTextTaobaoItemId = (EditText) findViewById(R.id.editTextTaobaoItemId);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
    }
}
