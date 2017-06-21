package com.mlr.demo.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mlr.demo.R;
import com.mlr.holder.BaseHolder;
import com.mlr.mvp.entity.NewsSummary;
import com.mlr.utils.ResourceUtils;

import java.util.List;

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
        String ptime = newsSummary.getPtime();

        mNewsSummaryTitleTv.setText(title);
        mNewsSummaryPtimeTv.setText(ptime);

        int PhotoThreeHeight = ResourceUtils.dip2px(getContext(),90);
        int PhotoTwoHeight = ResourceUtils.dip2px(getContext(),120);
        int PhotoOneHeight = ResourceUtils.dip2px(getContext(),150);

        String imgSrcLeft = null;
        String imgSrcMiddle = null;
        String imgSrcRight = null;

        ViewGroup.LayoutParams layoutParams = mNewsSummaryPhotoIvGroup.getLayoutParams();

        if (newsSummary.getAds() != null) {
            List<NewsSummary.AdsBean> adsBeanList = newsSummary.getAds();
            int size = adsBeanList.size();
            if (size >= 3) {
                imgSrcLeft = adsBeanList.get(0).getImgsrc();
                imgSrcMiddle = adsBeanList.get(1).getImgsrc();
                imgSrcRight = adsBeanList.get(2).getImgsrc();

                layoutParams.height = PhotoThreeHeight;

                mNewsSummaryTitleTv.setText(getContext()
                        .getString(R.string.photo_collections, adsBeanList.get(0).getTitle()));
            } else if (size >= 2) {
                imgSrcLeft = adsBeanList.get(0).getImgsrc();
                imgSrcMiddle = adsBeanList.get(1).getImgsrc();

                layoutParams.height = PhotoTwoHeight;
            } else if (size >= 1) {
                imgSrcLeft = adsBeanList.get(0).getImgsrc();

                layoutParams.height = PhotoOneHeight;
            }
        } else if (newsSummary.getImgextra() != null) {
            int size = newsSummary.getImgextra().size();
            if (size >= 3) {
                imgSrcLeft = newsSummary.getImgextra().get(0).getImgsrc();
                imgSrcMiddle = newsSummary.getImgextra().get(1).getImgsrc();
                imgSrcRight = newsSummary.getImgextra().get(2).getImgsrc();

                layoutParams.height = PhotoThreeHeight;
            } else if (size >= 2) {
                imgSrcLeft = newsSummary.getImgextra().get(0).getImgsrc();
                imgSrcMiddle = newsSummary.getImgextra().get(1).getImgsrc();

                layoutParams.height = PhotoTwoHeight;
            } else if (size >= 1) {
                imgSrcLeft = newsSummary.getImgextra().get(0).getImgsrc();

                layoutParams.height = PhotoOneHeight;
            }
        } else {
            imgSrcLeft = newsSummary.getImgsrc();

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
        Glide.with(getContext()).load(imgSrc).asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.image_place_holder)
                .error(R.drawable.ic_load_fail)
                .into(imageView);
    }

    private void hidePhoto(ImageView imageView) {
        imageView.setVisibility(View.GONE);
    }
}
