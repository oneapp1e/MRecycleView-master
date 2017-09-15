package com.mlr.demo;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.mlr.utils.BaseActivity;
import com.mlr.utils.LogUtil;

/**
 * Created by mulinrui on 9/15 0015.
 */

public class SplashActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(R.id.lav);
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                LogUtil.d("mlr lottieAnimationView onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                LogUtil.d("mlr lottieAnimationView onAnimationEnd");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                LogUtil.d("mlr lottieAnimationView onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                LogUtil.d("mlr lottieAnimationView onAnimationRepeat");
            }
        });

        Button btnHome = (Button) findViewById(R.id.tv_home);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("mlr enterHome");
                finish();
                //动画结束  进入主页
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

}
