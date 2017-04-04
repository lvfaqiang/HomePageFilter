package com.lvfq.homepage_master.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.lvfq.homepage_master.impl.OnScrollChangedListener;

/**
 * Created by lvfq
 * Date 2017/3/5 下午1:28.
 */

public class CusScrollView extends ScrollView {

    private OnScrollChangedListener listener;

    public void setOnScrollChangedListener(OnScrollChangedListener listener){
        this.listener = listener;
    }

    public CusScrollView(Context context) {
        super(context);
    }

    public CusScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null) {
            listener.onScrollChanged(this ,l , t , oldl , oldt);
        }
    }
}
