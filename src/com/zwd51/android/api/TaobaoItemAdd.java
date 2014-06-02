package com.zwd51.android.api;

import com.taobao.top.android.TopAndroidClient;
import com.taobao.top.android.TopParameters;
import com.taobao.top.android.api.FileItem;
import com.taobao.top.android.api.TopApiListener;

import java.io.File;
import java.util.Map;

/**
 * Created by Connor on 5/31/14.
 */
public class TaobaoItemAdd {
    public static void invoke(TopAndroidClient client, Long userId, Map fields, String outputPath, TopApiListener listener) {
        TopParameters params = new TopParameters();
        params.setMethod("taobao.item.add");
        params.setParams(fields);
        File file = new File(outputPath);
        FileItem image = new FileItem(file);
        params.addAttachment("image", image);
        client.api(params, userId, listener, true);
    }
}
