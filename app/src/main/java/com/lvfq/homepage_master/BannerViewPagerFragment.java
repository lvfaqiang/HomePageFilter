package com.lvfq.homepage_master;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BannerViewPagerFragment extends Fragment {

    private TextView banner_title_tv;
    private int currentItem = 0; // 当前图片的索引号
    private ViewPager viewPager;
    private BannerPagerAdapter bannerPagerAdapter;
    private List<ImageView> imageViews; // 滑动的图片集合
    private String[] titles; // 图片标题
    private List<View> dots; // 图片标题正文的那些点
    private int screenWidth;
    private LinearLayout dotsLL; // 小圆点的容器
    private Context mContext;
    private FrameLayout banner_fl;
    private ScheduledExecutorService scheduledExecutorService;
    private ImageView imgFault;
    private Boolean flag = false;

    private int[] imgs = new int[]{R.drawable.img_banner_4, R.drawable.img_banner_1, R.drawable.img_banner_2, R.drawable.img_banner_3};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View mView = inflater.inflate(
                getResources().getLayout(R.layout.fragment_bannervp),
                container, false);
        initView(mView);
        setTestData();
        return mView;
    }


    private void setTestData() {
        initFocusView(imgs.length);
        bannerPagerAdapter.notifyDataSetChanged();
//        for (int i = 0; i < mList.size(); i++) {
//            imageViews.get(i).setImageResource(mList.get(i));
//        }
        banner_fl.setVisibility(View.VISIBLE);
        imgFault.setVisibility(View.GONE);

    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub

        viewPager.setCurrentItem(0);// 切换当前显示的图片
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒钟切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 6, 6,
                TimeUnit.SECONDS);
        super.onResume();

    }

    public void initView(View mView) {
        banner_fl = (FrameLayout) mView
                .findViewById(R.id.more_banner_framelayout);
        viewPager = (ViewPager) mView.findViewById(R.id.more_banner_vp);
        dotsLL = (LinearLayout) mView.findViewById(R.id.more_dots_ll);
        banner_title_tv = (TextView) mView
                .findViewById(R.id.more_banner_title_tv);

        bannerPagerAdapter = new BannerPagerAdapter();
        imageViews = new ArrayList<ImageView>();
        dots = new ArrayList<View>();
        viewPager.setAdapter(bannerPagerAdapter);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        imgFault = (ImageView) mView.findViewById(R.id.imgFault);
    }


    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onPause() {
        scheduledExecutorService.shutdown();
        flag = false;
        bannerHandler.removeMessages(0);
        super.onPause();
    }

    @Override
    public void onStop() {
        // 当Activity不可见的时候停止切换
        flag = false;

        bannerHandler.removeMessages(0);
        super.onStop();
    }

    /**
     * 换页切换任务
     *
     * @author Administrator
     */
    private class ScrollTask implements Runnable {

        public void run() {
            synchronized (viewPager) {
                //if(!flag){
                currentItem = (currentItem + 1) % imageViews.size();
                bannerHandler.obtainMessage().sendToTarget(); // 通过Handler切换图片
//				}else{
//				flag=false;
//				currentItem = 0;
//				bannerHandler.obtainMessage().sendToTarget(); // 通过Handler切换图片
//				}
            }
        }

    }

    // 切换当前显示的图片
    private Handler bannerHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (flag) {
                viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
            } else {
                bannerHandler.removeMessages(0);
                viewPager.setCurrentItem(0);
                flag = true;
            }
        }

        ;
    };

    /**
     * 填充ViewPager页面的适配器
     *
     * @author Administrator
     */
    private class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(imageViews.get(arg1));
            return imageViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

    }

    /**
     * 当ViewPager中页面的状态发生改变时调用
     *
     * @author Administrator
     */
    private class MyPageChangeListener implements OnPageChangeListener {
        private int oldPosition = 0;

        /**
         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        public void onPageSelected(int position) {

            currentItem = position;
            banner_title_tv.setText(titles[position]);

            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    /**
     * 初始化焦点图的控件
     */
    private void initFocusView(int num) {
        titles = new String[num];
        if (num > 1) {
            // 设置一个监听器，当ViewPager中的页面改变时调用
            viewPager.setOnPageChangeListener(new MyPageChangeListener());
        }
        imageViews.clear();
        dots.clear();
        dotsLL.removeAllViews();
        for (int i = 0; i < num; i++) {
            // 初始化小圆点控件
            View view = new View(mContext);
            dotsLL.addView(view);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15, 15);
            lp.setMargins(10, 0, 10, 0);
            if (i == 0) {
                view.setBackgroundResource(R.drawable.dot_focused);
            } else {
                view.setBackgroundResource(R.drawable.dot_normal);
            }
            view.setLayoutParams(lp);
            dots.add(view);

            // 初始化图片资源
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setImageResource(imgs[i]);
//            ImageLoad.loadImg(mContext,mList.get(i).getPath() , R.color.c_eeede9, imageView);
//            Glide.with(this).load(mList.get(i).getPath()).placeholder(R.color.c_eeede9).error(R.color.c_eeede9).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(imageView);
            imageViews.add(imageView);

        }

        // 设置焦点图点击事件
        if (null != imageViews && imageViews.size() > 0) {
            for (int i = 0; i < imageViews.size(); i++) {
                final int pos = i;
                imageViews.get(i).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(mContext, pos + "", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    /**
     * banner图片点击事件
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
