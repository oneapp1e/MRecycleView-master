package com.mlr.demo.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mlr.demo.R;
import com.mlr.demo.utils.GlideApp;
import com.mlr.holder.BaseHolder;
import com.mlr.mvp.entity.NewsSummary;
import com.mlr.utils.ResourceUtils;
import com.mlr.utils.StringUtils;

/**
 * Created by mulinrui on 12/21 0021.
 */
public class PhotoViewHolder extends BaseHolder<NewsSummary> {

    private TextView mNewsSummaryTitleTv;
    private LinearLayout mNewsSummaryPhotoIvGroup;
    private ImageView mNewsSummaryPhotoIvLeft;
    private ImageView mNewsSummaryPhotoIvMiddle;
    private ImageView mNewsSummaryPhotoIvRight;
    private TextView mNewsSummaryPtimeTv;

    public PhotoViewHolder(View itemView, Context context) {
        super(itemView, context);
        mNewsSummaryTitleTv = (TextView) itemView.findViewById(R.id.news_summary_title_tv);
        mNewsSummaryPhotoIvGroup = (LinearLayout) itemView.findViewById(R.id.news_summary_photo_iv_group);
        mNewsSummaryPhotoIvLeft = (ImageView) itemView.findViewById(R.id.news_summary_photo_iv_left);
        mNewsSummaryPhotoIvMiddle = (ImageView) itemView.findViewById(R.id.news_summary_photo_iv_middle);
        mNewsSummaryPhotoIvRight = (ImageView) itemView.findViewById(R.id.news_summary_photo_iv_right);
        mNewsSummaryPtimeTv = (TextView) itemView.findViewById(R.id.news_summary_ptime_tv);
    }


    @Override
    public void setData(NewsSummary newsSummary) {
        super.setData(newsSummary);
        String title = newsSummary.getTitle();
        String ptime = newsSummary.getDate();

        mNewsSummaryTitleTv.setText(title);
        mNewsSummaryPtimeTv.setText(ptime);

        int PhotoThreeHeight = ResourceUtils.dip2px(getContext(), 90);
        int PhotoTwoHeight = ResourceUtils.dip2px(getContext(), 120);
        int PhotoOneHeight = ResourceUtils.dip2px(getContext(), 150);

        String imgSrcLeft = null;
        String imgSrcMiddle = null;
        String imgSrcRight = null;

        ViewGroup.LayoutParams layoutParams = mNewsSummaryPhotoIvGroup.getLayoutParams();

        if (!StringUtils.isEmpty(newsSummary.getThumbnail_pic_s())
                && !StringUtils.isEmpty(newsSummary.getThumbnail_pic_s02())
                && !StringUtils.isEmpty(newsSummary.getThumbnail_pic_s03())) {
            imgSrcLeft = newsSummary.getThumbnail_pic_s();
            imgSrcMiddle = newsSummary.getThumbnail_pic_s02();
            imgSrcRight = newsSummary.getThumbnail_pic_s03();

            layoutParams.height = PhotoThreeHeight;
        } else if (!StringUtils.isEmpty(newsSummary.getThumbnail_pic_s())
                && !StringUtils.isEmpty(newsSummary.getThumbnail_pic_s02())) {
            imgSrcLeft = newsSummary.getThumbnail_pic_s();
            imgSrcMiddle = newsSummary.getThumbnail_pic_s02();

            layoutParams.height = PhotoTwoHeight;
        } else {
            imgSrcLeft = newsSummary.getThumbnail_pic_s();

            layoutParams.height = PhotoOneHeight;
        }

        setPhotoImageView(imgSrcLeft, imgSrcMiddle, imgSrcRight);
        mNewsSummaryPhotoIvGroup.setLayoutParams(layoutParams);

    }

    private void setPhotoImageView(String imgSrcLeft, String imgSrcMiddle, String imgSrcRight) {
        if (imgSrcLeft != null) {
            showAndSetPhoto(mNewsSummaryPhotoIvLeft, imgSrcLeft);
        } else {
            hidePhoto(mNewsSummaryPhotoIvLeft);
        }

        if (imgSrcMiddle != null) {
            showAndSetPhoto(mNewsSummaryPhotoIvMiddle, imgSrcMiddle);
        } else {
            hidePhoto(mNewsSummaryPhotoIvMiddle);
        }

        if (imgSrcRight != null) {
            showAndSetPhoto(mNewsSummaryPhotoIvRight, imgSrcRight);
        } else {
            hidePhoto(mNewsSummaryPhotoIvRight);
        }
    }

    private void showAndSetPhoto(ImageView imageView, String imgSrc) {
        imageView.setVisibility(View.VISIBLE);

//        imgSrc = "file:///android_asset/gif01.gif";
        GlideApp.with(getContext()).asBitmap().load(imgSrc).into(imageView);
    }

    private void hidePhoto(ImageView imageView) {
        imageView.setVisibility(View.GONE);
    }
}
