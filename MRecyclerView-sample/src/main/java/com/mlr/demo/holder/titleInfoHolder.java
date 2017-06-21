package com.mlr.demo.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.mlr.demo.R;
import com.mlr.demo.model.TitleInfo;
import com.mlr.holder.BaseHolder;

/**
 * Created by mulinrui on 2016/11/16.
 */
public class TitleInfoHolder extends BaseHolder<TitleInfo> {

    private TextView mTextView;

    // ==========================================================================
    // Constants
    // ==========================================================================
    public TitleInfoHolder(View itemView, Context context) {
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
