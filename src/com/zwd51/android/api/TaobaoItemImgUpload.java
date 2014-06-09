package com.zwd51.android.api;

import com.taobao.top.android.TopAndroidClient;
import com.taobao.top.android.TopParameters;
import com.taobao.top.android.api.FileItem;
import com.taobao.top.android.api.TopApiListener;

import java.io.File;

/**
 * Created by Connor on 6/10/14.
 */
public class TaobaoItemImgUpload {
    public static void invoke(TopAndroidClient client, Long userId, String numIid, String filePath, TopApiListener listener) {
        TopParameters params = new TopParameters();
        params.setMethod("taobao.item.img.upload");
        params.addParam("num_iid", numIid);
        File file = new File(filePath);
        if (file.exists()) {
            FileItem image = new FileItem(file);
            params.addAttachment("image", image);
            client.api(params, userId, listener, true);
        }
    }
}
