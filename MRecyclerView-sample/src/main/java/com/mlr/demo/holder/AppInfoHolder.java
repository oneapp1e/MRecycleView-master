package com.mlr.demo.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.R;
import com.mlr.demo.model.AppInfo;
import com.mlr.holder.BaseHolder;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class AppInfoHolder extends BaseHolder<AppInfo> {

    private TextView mTextView;

    // ==========================================================================
    // Constants
    // ==========================================================================
    public AppInfoHolder(View itemView, Context context) {
        super(itemView, context);
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
