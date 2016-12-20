package com.mlr.demo.holder;

import android.view.View;
import android.widget.TextView;

import com.mlr.demo.R;
import com.mlr.demo.model.AppInfo;
import com.mlr.holder.BaseHolder;
import com.mlr.utils.BaseActivity;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class AppInfoHolder extends BaseHolder<AppInfo> {

    private TextView mTextView;

    // ==========================================================================
    // Constants
    // ==========================================================================
    public AppInfoHolder(View itemView, BaseActivity activity) {
        super(itemView, activity);
        mTextView = (TextView) itemView.findViewById(R.id.tweetText);
    }

    // ==========================================================================
    // Fields
    // ==========================================================================


    // ==========================================================================
    // Constructors
    // ==========================================================================


    // ==========================================================================
    // Getters
    // ==========================================================================


    // ==========================================================================
    // Setters
    // ==========================================================================

    @Override
    public void setData(AppInfo appInfo) {
        super.setData(appInfo);
        mTextView.setText(appInfo.getAppName());
    }

    public void setText(String s) {
        mTextView.setText(s);
    }

    // ==========================================================================
    // Methods
    // ==========================================================================


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

}
