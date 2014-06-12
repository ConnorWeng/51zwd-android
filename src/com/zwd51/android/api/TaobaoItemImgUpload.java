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
    public static void invoke(TopAndroidClient client, Long userId, String numIid, String id, byte[] imageBytes, TopApiListener listener) {
        TopParameters params = new TopParameters();
        params.setMethod("taobao.item.img.upload");
        params.addParam("num_iid", numIid);
        params.addParam("is_major", "true");
        FileItem image = new FileItem(id + ".jpg", imageBytes);
        params.addAttachment("image", image);
        client.api(params, userId, listener, true);
    }
}
