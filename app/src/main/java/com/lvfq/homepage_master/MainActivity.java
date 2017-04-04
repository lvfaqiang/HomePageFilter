package com.lvfq.homepage_master;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lvfq.homepage_master.adapter.ListViewBaseAdapter;
import com.lvfq.homepage_master.impl.IAreaCallBack;
import com.lvfq.homepage_master.impl.IFilterCallBack;
import com.lvfq.homepage_master.impl.OnScrollChangedListener;
import com.lvfq.homepage_master.util.DPUtil;
import com.lvfq.homepage_master.util.V;
import com.lvfq.homepage_master.view.CusScrollView;
import com.lvfq.homepage_master.view.FilterView;
import com.lvfq.homepage_master.view.MaxListView;

import java.util.ArrayList;
import java.util.List;

import static com.lvfq.homepage_master.util.DPUtil.dip2px;

public class MainActivity extends FragmentActivity {


    private FilterView filterView;
    private CusScrollView cus_scroll_view;
    private MaxListView main_max_listview;
    private LinearLayout ll_list_parent;
    private LinearLayout ll_toolbar;

    private ListViewBaseAdapter<String> mAdapter;

    private String strArea = "初始值"; // 选择区域
    private String strPosition = "初始值"; // 选择职位
    private String strExp = "初始值";  // 选择经验
    private int initY;

    private int toolBarHeight = 44; // toolbar 高度
    private int filterHeight = 40;  // 筛选条件布局高度

    private List<String> testList = new ArrayList<>();

    private BannerViewPagerFragment bannerFragment;    // 首页轮播图

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.setCancelable(false);

        initView();
        initBanner();
        initData();
        initAdapter();

    }

    private void initView() {
        filterView = V.find(this, R.id.main_filter);
        cus_scroll_view = V.find(this, R.id.cus_scroll_view);
        main_max_listview = V.find(this, R.id.main_max_listview);
        ll_list_parent = V.find(this, R.id.ll_list_parent);
        ll_toolbar = V.find(this, R.id.ll_toolbar);

    }

    /**
     * 初始化 banner 图
     */
    private void initBanner() {
        bannerFragment = new BannerViewPagerFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_main_container, bannerFragment).commit();
    }

    private void initData() {

        addModel(0, 5);

        setToolBarDefBg();

        filterView.setAreaCallBack(new IAreaCallBack() {
            @Override
            public void callBack(String city, String area) {
                strArea = city + " - " + area;
                showProgress();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgress();
                                    resetRefreshLocal();
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

        filterView.setFilterCallBack(new IFilterCallBack() {
            @Override
            public void callBack(int index, String string) {
                switch (index) {
                    case 1:
                        strPosition = string;
                        break;
                    case 2:
                        strExp = string;
                        break;
                }
                // 模拟数据加载
                showProgress();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 模拟成功回调
                                    dismissProgress();
                                    resetRefreshLocal();
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        /**
         * 设置ListView 的 父布局的最小高度
         */
        ll_list_parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int screenHeight = getResources().getDisplayMetrics().heightPixels;

                int statusHeight = DPUtil.getStatusBarHeight(MainActivity.this);    // 状态栏高度，像素
                initY = (int) ll_list_parent.getY();

                int minHeight = screenHeight - dip2px(MainActivity.this, toolBarHeight + filterHeight) - statusHeight;
                ll_list_parent.setMinimumHeight(minHeight);
                ll_list_parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        cus_scroll_view.setOnScrollChangedListener(new OnScrollChangedListener() {
            @Override
            public void onScrollChanged(CusScrollView scrollView, int x, int y, int oldx, int oldy) {
                int bannerHeight = DPUtil.dip2px(MainActivity.this, 180 - toolBarHeight);  // 180 是 布局里面写死的 banner 高度。
                if (y > bannerHeight) {
                    ll_toolbar.setBackgroundColor(Color.argb((int) 255, 255, 122, 122));    // 给定的一个颜色值
                } else {
                    if (y <= 0) {
                        //设置 标题栏默认样式
                        setToolBarDefBg();
                    } else {
                        int alpha = (int) (255 * ((float) y / bannerHeight));
                        ll_toolbar.setBackgroundColor(Color.argb(alpha, 255, 122, 122));
                    }
                }

                // 控制筛选条件是否显示。
                int optionHeight = DPUtil.dip2px(MainActivity.this, 80);    // 80 是界面中 模拟占位区域的高度。
                if (y >= bannerHeight + optionHeight - filterHeight - toolBarHeight) {
                    filterView.setVisibility(View.VISIBLE);
                } else {
                    filterView.setVisibility(View.GONE);
                }

            }
        });

    }

    /**
     * 定位列表到第一条
     */
    private void resetRefreshLocal() {
        int offset = dip2px(this, filterHeight + toolBarHeight);   // 84  是 toolbar + 筛选条件的布局高度
        cus_scroll_view.scrollTo(0, initY - offset);// 设置 scrollView 滚动到某一个点，
    }

    /**
     * 设置 标题栏默认样式
     */
    private void setToolBarDefBg() {
        int colors[] = {0x88000000, 0x00000000};
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        ll_toolbar.setBackgroundDrawable(g);
    }

    private void initAdapter() {
        mAdapter = new ListViewBaseAdapter<String>(this, R.layout.item_main_listview, testList) {
            @Override
            public void convert(int position, String item) {
                TextView tv_index = f(R.id.tv_item_index);
                TextView tv_position = f(R.id.tv_item_position);
                TextView tv_area = f(R.id.tv_item_area);
                TextView tv_exp = f(R.id.tv_item_exp);

                tv_index.setText((position + 1) + "");
                tv_position.setText(strPosition);
                tv_exp.setText(strExp);
                tv_area.setText(strArea);
                if (position == list.size() - 1 && list.size() < 20) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showProgress();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dismissProgress();
                                                addModel(testList.size(), 10);
                                                notifyDataSetChanged();
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();

                        }
                    });
                } else {
                    itemView.setOnClickListener(null);
                }

            }
        };
        main_max_listview.setAdapter(mAdapter);
    }

    /**
     * 创建模拟数据
     *
     * @param startIndex
     * @param length
     */
    private void addModel(int startIndex, int length) {
        for (int i = startIndex; i < startIndex + length; i++) {
            testList.add("");
        }
    }

    private void showProgress() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgress() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}
