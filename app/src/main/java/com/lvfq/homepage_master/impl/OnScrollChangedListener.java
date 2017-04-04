package com.lvfq.homepage_master.impl;

import com.lvfq.homepage_master.view.CusScrollView;

/**
 * Created by lvfq
 * Date 2017/3/5 下午1:30.
 */

public interface OnScrollChangedListener {
    void onScrollChanged(CusScrollView scrollView, int x, int y, int oldx, int oldy);
}
