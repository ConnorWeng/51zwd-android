package com.zwd51.android.data;

import com.zwd51.android.model.TaobaoItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Connor on 6/4/14.
 */
public class TaobaoItemProvider {
    public ArrayList<TaobaoItem> getData() {
        ArrayList<TaobaoItem> taobaoItems = new ArrayList<TaobaoItem>();
        taobaoItems.add(makeTaobaoItem("8160 2014女装夏季欧根纱蕾丝蓬蓬无袖打底连衣裙", "http://img01.taobaocdn.com/bao/uploaded/i3/T1w28AFOFaXXXXXXXX_!!0-item_pic.jpg_180x180.jpg", "40.00"));
        taobaoItems.add(makeTaobaoItem("2014新款品韩版潮露肩女t恤短袖短裤休闲两件套装名媛小香风6621#", "http://img01.taobaocdn.com/bao/uploaded/i4/T1vdvUFM8cXXXXXXXX_!!0-item_pic.jpg_200x200.jpg", "38.00"));
        taobaoItems.add(makeTaobaoItem("2014韩国春班服情侣装夏装 沙滩条纹短袖t恤衫大码女裙连衣裙6801", "http://img01.taobaocdn.com/bao/uploaded/i4/T1.Z98FQ8aXXXXXXXX_!!0-item_pic.jpg_200x200.jpg", "16.00"));
        return taobaoItems;
    }

    private TaobaoItem makeTaobaoItem(String title, String picUrl, String price) {
        TaobaoItem item = new TaobaoItem();
        HashMap<String, String> fields = new HashMap<String, String>();
        fields.put("title", title);
        fields.put("price", price);
        item.setFields(fields);
        item.setPicUrl(picUrl);
        return item;
    }
}
