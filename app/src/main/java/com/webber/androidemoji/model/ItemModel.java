package com.webber.androidemoji.model;

import com.chad.library.adapter.base.entity.SectionEntity;

public class ItemModel extends SectionEntity<ItemBean> {


    public ItemModel(ItemBean itemBean) {
        super(itemBean);
    }

    public ItemModel(boolean isHeader, String header) {
        super(isHeader, header);
    }
}
