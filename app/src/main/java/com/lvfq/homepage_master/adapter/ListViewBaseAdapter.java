package com.lvfq.homepage_master.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lvfq.homepage_master.util.ViewHolder;

import java.util.List;

/**
 * ListViewBaseAdapter
 *
 * @author lvfq
 * @date 2017/3/20 上午9:46
 * @mainFunction :
 */

public abstract class ListViewBaseAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected Context context;
    private int layoutId;
    protected View itemView;

    public ListViewBaseAdapter(Context context, int layoutId, List<T> list) {
        this.context = context;
        this.layoutId = layoutId;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return list != null && list.size() > 0 ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        }
        itemView = convertView;
        convert(position, getItem(position));
        return convertView;
    }

    public abstract void convert(int position, T item);

    protected <E extends View> E f(int id) {
        return ViewHolder.get(itemView, id);
    }

}
