package com.lvfq.homepage_master.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lvfq.homepage_master.R;
import com.lvfq.homepage_master.adapter.ListViewBaseAdapter;
import com.lvfq.homepage_master.impl.IAreaCallBack;
import com.lvfq.homepage_master.impl.IFilterCallBack;
import com.lvfq.homepage_master.util.V;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lvfq.homepage_master.R.id.tv_tip1;
import static com.lvfq.homepage_master.R.id.tv_tip2;
import static com.lvfq.homepage_master.R.id.tv_tip3;

/**
 * Created by lvfq
 * Date 2017/1/21 上午11:21.
 */

public class FilterView extends LinearLayout implements View.OnClickListener {
    private Context context;

    private TextView[] tips = new TextView[3];

    private View fl_content;
    private View ll_content;

    private ListView lv_filter_single;
    private ListView lv_filter_city;
    private ListView lv_filter_area;

    private ListViewBaseAdapter<String> lvSingleAdapter;
    private ListViewBaseAdapter<String> lvAdapterCity;
    private ListViewBaseAdapter<String> lvAdapterArea;

    private String[] c = new String[]{"北京", "上海", "杭州", "深圳"};

    private String[][] a = new String[][]{
            {"东城区", "西城区", "崇文区", "宣武区", "海淀区", "丰台区", "朝阳区"},
            {"黄浦区", "徐汇区", "长宁区", "杨浦区", "虹口区", "普陀区", "静安区"},
            {"西湖区", "上城区", "下城区", "拱墅区", "江干区", "滨江区", "萧山区", "余杭区", "富阳区"},
            {"罗湖区", "福田区", "南山区", "盐田区", "宝安区", "龙岗区", "光明新区"}};

    private List<String> expList = new ArrayList<>();   // 经验列表
    private List<String> positionList = new ArrayList<>();  // 岗位列表
    private List<String> citys = new ArrayList<>(); // 城市列表
    private List<List<String>> areas = new ArrayList<>();   // 区域列表

    private List<String> singleList = new ArrayList<>();    // 存放当前ListView 所填充数据

    private int curCity = 0;
    private int curIndex = -1;

    private IAreaCallBack areaCallBack;
    private IFilterCallBack filterCallBack;


    public FilterView(Context context) {
        this(context, null);
    }

    public FilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        this.context = context;
        initData();

        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_filter, null);
        addView(view);

        initView(view);

        initAdapter();

    }

    /**
     * 实例化组件
     *
     * @param view
     */
    private void initView(View view) {
        tips[0] = V.find(view, tv_tip1);
        tips[1] = V.find(view, tv_tip2);
        tips[2] = V.find(view, tv_tip3);

        fl_content = V.find(view, R.id.fl_content);
        ll_content = V.find(view, R.id.ll_content);

        lv_filter_single = V.find(view, R.id.lv_filter_single);
        lv_filter_city = V.find(view, R.id.lv_filter_city);
        lv_filter_area = V.find(view, R.id.lv_filter_area);

        view.findViewById(R.id.fl_tip1).setOnClickListener(this);
        view.findViewById(R.id.fl_tip2).setOnClickListener(this);
        view.findViewById(R.id.fl_tip3).setOnClickListener(this);
        fl_content.setOnClickListener(this);
    }

    /**
     * 初始化 Adapter
     */
    private void initAdapter() {
        lvSingleAdapter = new ListViewBaseAdapter<String>(context, R.layout.item_filter_single, singleList) {
            @Override
            public void convert(int position, final String item) {
                TextView textView = f(R.id.tv_item_single);
                textView.setText(item);
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 2017/3/31 列表点击事件
                        if (filterCallBack != null) {
                            filterCallBack.callBack(curIndex, item);
                        }
                        changeFilterStatus(curIndex);
                    }
                });
            }
        };

        lvAdapterCity = new ListViewBaseAdapter<String>(context, R.layout.item_filter_list1, citys) {
            @Override
            public void convert(final int position, String item) {
                TextView textView = f(R.id.tv_item_city);
                textView.setText(item);
                if (curCity == position) {
                    textView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                } else {
                    textView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
                }
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        singleList.clear();
                        List<String> list = areas.get(position);
                        if (list != null && list.size() > 0) {
                            singleList.addAll(list);
                        }
                        curCity = position;
                        notifyDataSetChanged();
                        lvAdapterArea.notifyDataSetChanged();
                    }
                });
            }
        };

        lvAdapterArea = new ListViewBaseAdapter<String>(context, R.layout.item_filter_list2, singleList) {
            @Override
            public void convert(int position, final String item) {
                TextView textView = f(R.id.tv_item_area);
                textView.setText(item);
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 确定回调
                        if (areaCallBack != null) {
                            areaCallBack.callBack(citys.get(curCity), item);
                        }
                        changeFilterStatus(curIndex);
                    }
                });
            }
        };

        lv_filter_single.setAdapter(lvSingleAdapter);
        lv_filter_city.setAdapter(lvAdapterCity);
        lv_filter_area.setAdapter(lvAdapterArea);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_tip1:
                singleList.clear();
                curCity = 0;
                singleList.addAll(areas.get(curCity));  // 这里因为是模拟数据，所以我直接用的是 singleList ,实际项目中可根据数据格式进行调整。
                changeFilterStatus(0);
                break;
            case R.id.fl_tip2:
                singleList.clear();
                singleList.addAll(positionList);
                changeFilterStatus(1);
                break;
            case R.id.fl_tip3:
                singleList.clear();
                singleList.addAll(expList);
                changeFilterStatus(2);
                break;
            case R.id.fl_content:
                changeFilterStatus(curIndex);
                break;
        }
    }

    /**
     * 改变筛选条件状态
     *
     * @param index
     */
    private void changeFilterStatus(int index) {
        if (curIndex == index) {
            fl_content.setVisibility(GONE);
            tips[index].setSelected(false);
            curIndex = -1;
        } else {
            if (fl_content.getVisibility() != VISIBLE) {
                fl_content.setVisibility(VISIBLE);
            }
            if (index == 0) {
                ll_content.setVisibility(VISIBLE);
                lv_filter_single.setVisibility(GONE);

                if (lvAdapterCity != null) {
                    lvAdapterCity.notifyDataSetChanged();
                }
                if (lvAdapterArea != null) {
                    lvAdapterArea.notifyDataSetChanged();
                }
            } else {
                ll_content.setVisibility(GONE);
                lv_filter_single.setVisibility(VISIBLE);

                if (lvSingleAdapter != null) {
                    lvSingleAdapter.notifyDataSetChanged();
                }
            }
            tips[index].setSelected(true);
            if (curIndex != -1) {
                tips[curIndex].setSelected(false);
            }
            curIndex = index;
        }
    }


    /**
     * 初始化模拟数据
     */
    private void initData() {
        initArea();
        initPosition();
        initExpList();
    }

    /**
     * 模拟经验要求列表
     */
    private void initExpList() {
        for (int i = 0; i < 5; i++) {
            expList.add((i + 1) + "年以上经验");
        }
    }

    /**
     * 模拟区域数据
     */
    private void initArea() {
        citys = Arrays.asList(c);
        for (int i = 0; i < a.length; i++) {
            areas.add(Arrays.asList(a[i]));
        }
    }

    /**
     * 模拟岗位数据
     */
    private void initPosition() {
        positionList.add("运营总监");
        positionList.add("产品经理");
        positionList.add("技术主管");
        positionList.add("财务助理");
    }

    /**
     * 设置区域点击回调
     *
     * @param areaCallBack
     */
    public void setAreaCallBack(IAreaCallBack areaCallBack) {
        this.areaCallBack = areaCallBack;
    }

    /**
     * 单选项点击回调
     *
     * @param filterCallBack
     */
    public void setFilterCallBack(IFilterCallBack filterCallBack) {
        this.filterCallBack = filterCallBack;
    }

}
