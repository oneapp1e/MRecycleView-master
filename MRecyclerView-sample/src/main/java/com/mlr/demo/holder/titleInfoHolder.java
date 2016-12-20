package com.mlr.demo.holder;

import android.view.View;
import android.widget.TextView;

import com.mlr.demo.R;
import com.mlr.demo.model.TitleInfo;
import com.mlr.holder.BaseHolder;
import com.mlr.utils.BaseActivity;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class TitleInfoHolder extends BaseHolder<TitleInfo> {

    private TextView mTextView;

    // ==========================================================================
    // Constants
    // ==========================================================================
    public TitleInfoHolder(View itemView, BaseActivity activity) {
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
    public void setData(TitleInfo titleInfo) {
        super.setData(titleInfo);
        mTextView.setText(titleInfo.getTitle());
    }

    // ==========================================================================
    // Methods
    // ==========================================================================


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

}
