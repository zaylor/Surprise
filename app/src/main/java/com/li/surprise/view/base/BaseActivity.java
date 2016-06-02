package com.li.surprise.view.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.li.surprise.widget.CustomToast;

/**
 * Created by lizili on 16/6/2.
 */
public abstract class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        initView();
    }

    protected abstract int setLayout();

    protected abstract void initView();

    protected abstract void onResumeLoad();

    @Override
    protected void onResume(){
        super.onResume();
        onResumeLoad();
    }
    public void showToast(String info){
        CustomToast.getInstance(this).ToastShow(this, info);
    }
}
