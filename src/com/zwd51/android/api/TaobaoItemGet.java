package com.zwd51.android.api;

import com.taobao.top.android.TopAndroidClient;
import com.taobao.top.android.TopParameters;
import com.taobao.top.android.api.TopApiListener;

import java.util.Arrays;

/**
 * Created by Connor on 5/31/14.
 */
public class TaobaoItemGet {
    public static void invoke(TopAndroidClient client, Long userId, String id, TopApiListener listener) {
        TopParameters params = new TopParameters();
        params.setMethod("taobao.item.get");
        params.setFields(Arrays.asList(new String[]{
                "title", "desc", "pic_url",
                "sku", "item_weight", "property_alias",
                "price", "item_img.url", "cid",
                "nick", "props_name", "prop_img",
                "num"}));
        params.addParam("num_iid", id);
        client.api(params, userId, listener, true);
    }
}
