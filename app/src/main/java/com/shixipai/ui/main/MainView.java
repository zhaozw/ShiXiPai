package com.shixipai.ui.main;

/**
 * Created by xiepeng on 16/1/19.
 */
public interface MainView {

    //替换
    void replaceFragment(int position);

    void startSettingsActivity();

    void startFeedbackActivity();

    void shareApp();

    //将底部标签变为全部未点击
    void setUnSelect();

}
