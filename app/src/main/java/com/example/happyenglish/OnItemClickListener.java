package com.example.happyenglish;

import android.view.View;

public interface OnItemClickListener {
    //由于在adapter中直接设置点击事件很多变量不知道怎么传，通过这个接口可以直接在MainActivity中注册点击事件
    public void onItemClick(View view, int postion);
}