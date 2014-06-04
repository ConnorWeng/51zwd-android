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
        taobaoItems.add(makeTaobaoItem("37958104675", "8160 2014女装夏季欧根纱蕾丝蓬蓬无袖打底连衣裙", "http://img01.taobaocdn.com/bao/uploaded/i3/T1w28AFOFaXXXXXXXX_!!0-item_pic.jpg_180x180.jpg", "40.00"));
        taobaoItems.add(makeTaobaoItem("39227804357", "2014新款品韩版潮露肩女t恤短袖短裤休闲两件套装名媛小香风6621#", "http://img01.taobaocdn.com/bao/uploaded/i4/T1vdvUFM8cXXXXXXXX_!!0-item_pic.jpg_200x200.jpg", "38.00"));
        taobaoItems.add(makeTaobaoItem("38675410825", "2014韩国春班服情侣装夏装 沙滩条纹短袖t恤衫大码女裙连衣裙6801", "http://img01.taobaocdn.com/bao/uploaded/i4/T1.Z98FQ8aXXXXXXXX_!!0-item_pic.jpg_200x200.jpg", "16.00"));
        taobaoItems.add(makeTaobaoItem("39302044607", "2014夏新款链条斜肩弹力收腰连衣裙", "http://img03.taobaocdn.com/bao/uploaded/i3/2000128480/T2iOZrXilbXXXXXXXX-2000128480.jpg_400x400.jpg", "52.00"));
        taobaoItems.add(makeTaobaoItem("39321547841", "夏装 韩版镂空蕾丝中长款披肩开衫针织衫防晒薄空调衫", "http://img02.taobaocdn.com/bao/uploaded/i2/T1FT2dFMXdXXXXXXXX_!!0-item_pic.jpg_400x400.jpg", "49.59"));
        taobaoItems.add(makeTaobaoItem("39193446739", "2014夏装新款女装清新两件套韩版连衣裙 修身雪纺仙女公主裙套装", "http://img04.taobaocdn.com/bao/uploaded/i4/2000128480/TB2yVRGXpXXXXX4XFXXXXXXXXXX_!!2000128480.jpg_400x400.jpg", "65.00"));
        return taobaoItems;
    }

    private TaobaoItem makeTaobaoItem(String id, String title, String picUrl, String price) {
        TaobaoItem item = new TaobaoItem();
        item.setId(id);
        HashMap<String, String> fields = new HashMap<String, String>();
        fields.put("title", title);
        fields.put("price", price);
        item.setFields(fields);
        item.setPicUrl(picUrl);
        return item;
    }
}
