package com.lvfq.homepage_master;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * StartActivity
 *
 * @author lvfq
 * @date 2017/4/12 下午2:49
 * @mainFunction :
 */

public class StartActivity extends FragmentActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

    }

    public void onClickListView(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickRecyclerView(View view) {
        Intent intent = new Intent(this, RecyclerViewScrollActivity.class);
        startActivity(intent);
    }
}
