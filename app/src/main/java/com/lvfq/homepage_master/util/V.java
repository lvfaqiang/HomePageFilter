package com.lvfq.homepage_master.util;

import android.app.Activity;
import android.view.View;

/**
 * V
 *
 * @author lvfq
 * @date 2017/3/31 下午5:41
 * @mainFunction :
 */

public class V {
    /**
     * 简化Activity里面的 findViewById
     *
     * @param activity
     * @param id
     * @return
     */
    public static <T extends View> T find(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    /**
     * 简化Fragment中的findViewById
     *
     * @param view
     * @param id
     * @return
     */
    public static <T extends View> T find(View view, int id) {
        return (T) view.findViewById(id);
    }

}
