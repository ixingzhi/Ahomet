package com.shichuang.open;
import android.content.Context;

/**
 * Created by Administrator on 2017/11/22.
 */

public class Open {
    private static Open mInstance = null;
    private Context mContext;

    private Open() {
    }

    public static Open getInstance() {
        if (mInstance == null) {
            mInstance = new Open();
        }
        return mInstance;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }
}
